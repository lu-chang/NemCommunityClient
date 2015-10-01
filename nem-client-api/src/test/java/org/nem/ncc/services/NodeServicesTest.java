package org.nem.ncc.services;

import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.connect.FatalPeerException;
import org.nem.core.connect.client.*;
import org.nem.core.metadata.ApplicationMetaData;
import org.nem.core.node.*;
import org.nem.core.serialization.Deserializer;
import org.nem.core.time.*;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NodeServicesTest {

	//region getNodeAsync

	@Test
	public void getNodeAsyncReturnsNodeOnSuccess() {
		// Assert:
		final TestContext context = new TestContext();
		ServicesUtils.assertSuccessfulDelegationToConnector(
				endpoint -> context.services.getNodeAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_NODE_INFO,
				GraphUtils.createNode());
	}

	@Test
	public void getNodeAsyncThrowsExceptionOnError() {
		// Assert:
		final TestContext context = new TestContext();
		ServicesUtils.assertFailureDelegationToConnector(
				endpoint -> context.services.getNodeAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_NODE_INFO);
	}

	//endregion

	//region getNisNodeInfoAsync

	@Test
	public void getNisNodeInfoAsyncReturnsNisNodeInfoOnSuccess() {
		// Assert:
		final TestContext context = new TestContext();
		ServicesUtils.assertSuccessfulDelegationToConnector(
				endpoint -> context.services.getNisNodeInfoAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_NODE_EXTENDED_INFO,
				createNisNodeInfo());
	}

	@Test
	public void getNisNodeInfoAsyncThrowsExceptionOnError() {
		// Assert:
		final TestContext context = new TestContext();
		ServicesUtils.assertFailureDelegationToConnector(
				endpoint -> context.services.getNisNodeInfoAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_NODE_EXTENDED_INFO);
	}

	//endregion

	//region getNodesAsync

	@Test
	public void getNodesAsyncReturnsNodesOnSuccess() {
		// Arrange:
		final TestContext context = new TestContext();

		final List<Node> originalNodes = createThreeTestNodes();
		for (final Node node : originalNodes) {
			context.setNodeInfoForNode(node, Utils.createDeserializerFuture(node));
		}

		// Act:
		final List<NodeEndpoint> endpoints = originalNodes.stream().map(Node::getEndpoint).collect(Collectors.toList());
		final Collection<Node> nodes = context.services.getNodesAsync(endpoints).join();

		// Assert:
		Assert.assertThat(nodes, IsEquivalent.equivalentTo(originalNodes));
		context.verifyNumNodeInfoRequests(3);
		for (final Node node : originalNodes) {
			context.verifySingleNodeInfoRequest(node.getEndpoint());
		}
	}

	@Test
	public void getNodesAsyncFiltersOutFailedNodes() {
		// Arrange:
		final TestContext context = new TestContext();

		final List<Node> originalNodes = createThreeTestNodes();
		context.setNodeInfoForNode(originalNodes.get(0), Utils.createExceptionalFuture(new FatalPeerException("badness")));
		context.setNodeInfoForNode(originalNodes.get(1), Utils.createDeserializerFuture(originalNodes.get(1)));
		context.setNodeInfoForNode(originalNodes.get(2), Utils.createExceptionalFuture(new FatalPeerException("badness")));

		// Act:
		final List<NodeEndpoint> endpoints = originalNodes.stream().map(Node::getEndpoint).collect(Collectors.toList());
		final Collection<Node> nodes = context.services.getNodesAsync(endpoints).join();

		// Assert:
		Assert.assertThat(nodes, IsEquivalent.equivalentTo(Collections.singletonList(originalNodes.get(1))));
		context.verifyNumNodeInfoRequests(3);
		for (final Node node : originalNodes) {
			context.verifySingleNodeInfoRequest(node.getEndpoint());
		}
	}

	//endregion

	private static List<Node> createThreeTestNodes() {
		return Arrays.asList(
				GraphUtils.createNodeFromHost("10.0.0.1"),
				GraphUtils.createNodeFromHost("10.0.0.2"),
				GraphUtils.createNodeFromHost("10.0.0.3"));
	}

	private static NisNodeInfo createNisNodeInfo() {
		return new NisNodeInfo(GraphUtils.createNodeFromHost("10.0.0.1"), createAppMetaData("NIS", "1.0.0"));
	}

	private static ApplicationMetaData createAppMetaData(final String name, final String version) {
		final TimeProvider timeProvider = Mockito.mock(TimeProvider.class);
		Mockito.when(timeProvider.getCurrentTime()).thenReturn(new TimeInstant(17));
		return new ApplicationMetaData(name, version, null, timeProvider);
	}

	private static class TestContext {
		final AsyncNisConnector connector = Mockito.mock(AsyncNisConnector.class);
		final NodeServices services = new NodeServices(this.connector);

		private void setNodeInfoForNode(final Node node, final CompletableFuture<Deserializer> future) {
			this.setNodeInfoForNode(node.getEndpoint(), future);
		}

		private void setNodeInfoForNode(final NodeEndpoint endpoint, final CompletableFuture<Deserializer> future) {
			Mockito.when(this.connector.getAsync(endpoint, NisApiId.NIS_REST_NODE_INFO, null))
					.thenReturn(future);
		}

		private void verifyNumNodeInfoRequests(final int numExpectedRequests) {
			Mockito.verify(this.connector, Mockito.times(numExpectedRequests)).getAsync(
					Mockito.any(),
					Mockito.eq(NisApiId.NIS_REST_NODE_INFO),
					Mockito.eq(null));
		}

		private void verifySingleNodeInfoRequest(final NodeEndpoint expectedEndpoint) {
			Mockito.verify(this.connector, Mockito.times(1)).getAsync(
					expectedEndpoint,
					NisApiId.NIS_REST_NODE_INFO,
					null);
		}
	}
}
