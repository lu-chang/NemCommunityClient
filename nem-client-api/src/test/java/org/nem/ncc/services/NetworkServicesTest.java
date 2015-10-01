package org.nem.ncc.services;

import org.hamcrest.core.IsNull;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.connect.FatalPeerException;
import org.nem.core.connect.client.*;
import org.nem.core.node.*;
import org.nem.core.serialization.*;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class NetworkServicesTest {

	// region getNodePeerListAsync

	@Test
	public void getNodePeerListAsyncReturnsNodeCollectionOnSuccess() {
		// Arrange:
		final NodeCollection originalNodes = new NodeCollection();
		originalNodes.update(GraphUtils.createNodeFromHost("10.0.0.1"), NodeStatus.ACTIVE);
		originalNodes.update(GraphUtils.createNodeFromHost("10.0.0.2"), NodeStatus.INACTIVE);
		originalNodes.update(GraphUtils.createNodeFromHost("10.0.0.3"), NodeStatus.ACTIVE);

		// Act:
		final TestContext context = new TestContext();
		ServicesUtils.assertSuccessfulDelegationToConnector(
				endpoint -> context.services.getNodePeerListAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_PEER_LIST,
				originalNodes);
	}

	@Test
	public void getNodePeerListThrowsExceptionOnError() {
		final TestContext context = new TestContext();
		ServicesUtils.assertFailureDelegationToConnector(
				endpoint -> context.services.getNodePeerListAsync(endpoint).join(),
				context.connector,
				NisApiId.NIS_REST_PEER_LIST);
	}

	// endregion

	// region getPeerListsAsync

	@Test
	public void getNodePeerListsAsyncAsyncReturnsNodesOnSuccess() {
		// Arrange:
		final TestContext context = new TestContext();

		final List<Node> originalNodes = createThreeTestNodes();
		for (final Node node : originalNodes) {
			// create a different node collection for each node
			context.setPeerListForNode(node, createNodeCollectionFuture(node));
		}

		// Act:
		final Map<Node, NodeCollection> nodePeersMap = context.services.getNodePeerListsAsync(originalNodes).join();

		// Assert:
		for (final Node node : originalNodes) {
			Assert.assertThat(nodePeersMap.get(node).getActiveNodes(), IsEquivalent.equivalentTo(Collections.singletonList(node)));
		}

		context.verifyNumPeerListRequests(3);
		for (final Node node : originalNodes) {
			context.verifySinglePeerListRequest(node.getEndpoint());
		}
	}

	@Test
	public void getNodePeerListsAsyncIncludesFailedNodes() {
		// Arrange:
		final TestContext context = new TestContext();

		final List<Node> originalNodes = createThreeTestNodes();
		context.setPeerListForNode(originalNodes.get(0), Utils.createExceptionalFuture(new FatalPeerException("badness")));
		context.setPeerListForNode(originalNodes.get(1), createNodeCollectionFuture(originalNodes.get(1)));
		context.setPeerListForNode(originalNodes.get(2), Utils.createExceptionalFuture(new FatalPeerException("badness")));

		// Act:
		final Map<Node, NodeCollection> nodePeersMap = context.services.getNodePeerListsAsync(originalNodes).join();

		// Assert:
		Assert.assertThat(nodePeersMap.get(originalNodes.get(0)), IsNull.nullValue());
		Assert.assertThat(nodePeersMap.get(originalNodes.get(1)).getActiveNodes(), IsEquivalent.equivalentTo(Collections.singletonList(originalNodes.get(1))));
		Assert.assertThat(nodePeersMap.get(originalNodes.get(2)), IsNull.nullValue());

		context.verifyNumPeerListRequests(3);
		for (final Node node : originalNodes) {
			context.verifySinglePeerListRequest(node.getEndpoint());
		}
	}

	// endregion

	private static List<Node> createThreeTestNodes() {
		return Arrays.asList(
				GraphUtils.createNodeFromHost("10.0.0.1"),
				GraphUtils.createNodeFromHost("10.0.0.2"),
				GraphUtils.createNodeFromHost("10.0.0.3"));
	}

	private static CompletableFuture<Deserializer> createNodeCollectionFuture(final NodeCollection nodes) {
		final Deserializer deserializer = Utils.createDeserializer(JsonSerializer.serializeToJson(nodes));
		return CompletableFuture.completedFuture(deserializer);
	}

	private static CompletableFuture<Deserializer> createNodeCollectionFuture(final Node node) {
		final NodeCollection nodes = new NodeCollection();
		nodes.update(node, NodeStatus.ACTIVE);
		return createNodeCollectionFuture(nodes);
	}

	private static class TestContext {
		final AsyncNisConnector connector = Mockito.mock(AsyncNisConnector.class);
		final NetworkServices services = new NetworkServices(this.connector);

		private void setPeerListForNode(final Node node, final CompletableFuture<Deserializer> future) {
			this.setPeerListForNode(node.getEndpoint(), future);
		}

		private void setPeerListForNode(final NodeEndpoint endpoint, final CompletableFuture<Deserializer> future) {
			Mockito.when(this.connector.getAsync(endpoint, NisApiId.NIS_REST_PEER_LIST, null))
					.thenReturn(future);
		}

		private void verifyNumPeerListRequests(final int numExpectedRequests) {
			Mockito.verify(this.connector, Mockito.times(numExpectedRequests)).getAsync(
					Mockito.any(),
					Mockito.eq(NisApiId.NIS_REST_PEER_LIST),
					Mockito.eq(null));
		}

		private void verifySinglePeerListRequest(final NodeEndpoint expectedEndpoint) {
			Mockito.verify(this.connector, Mockito.times(1)).getAsync(
					expectedEndpoint,
					NisApiId.NIS_REST_PEER_LIST,
					null);
		}
	}
}