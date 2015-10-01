package org.nem.ncc.model.graph;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.crypto.KeyPair;
import org.nem.core.node.*;
import org.nem.ncc.services.*;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.concurrent.*;

public class GraphViewModelFactoryTest {
	//region basic tests

	@Test
	public void createViewModelDelegatesToServices() {
		// Arrange:
		final BasicTestContext context = new BasicTestContext();
		final Collection<NodeEndpoint> endpoints = Arrays.asList(
				NodeEndpoint.fromHost("10.0.0.1"),
				NodeEndpoint.fromHost("10.0.0.2"));
		final Collection<Node> nodes = Arrays.asList(
				GraphUtils.createNodeFromHost("10.0.0.5"),
				GraphUtils.createNodeFromHost("10.0.0.6"));
		final CompletableFuture<Collection<Node>> nodesFuture = CompletableFuture.completedFuture(nodes);

		Mockito.when(context.nodeServices.getNodesAsync(endpoints))
				.thenReturn(nodesFuture);
		Mockito.when(context.networkServices.getNodePeerListsAsync(nodes))
				.thenReturn(CompletableFuture.completedFuture(new HashMap<>()));

		// Act:
		final GraphViewModelFactory factory = new GraphViewModelFactory();
		factory.createViewModel(context.networkServices, context.nodeServices, endpoints);

		// Assert:
		Mockito.verify(context.nodeServices, Mockito.times(1)).getNodesAsync(endpoints);
		Mockito.verify(context.networkServices, Mockito.times(1)).getNodePeerListsAsync(nodes);
	}

	private static class BasicTestContext {
		private final NetworkServices networkServices = Mockito.mock(NetworkServices.class);
		private final NodeServices nodeServices = Mockito.mock(NodeServices.class);
	}

	// endregion

	//region integration tests

	/**
	 * Graph:
	 * A = active, I = Inactive.
	 * Number = last 8 bits of the ip address.
	 * o = direction of edge.
	 *
	 *
	 * I15o----------------A11o---------oA12
	 *                      o\
	 *                     /  \
	 *                    /    \
	 *                   /      \
	 *                  /        \
	 *                 /          \
	 *                /            o
	 * I14o----------A1o----------oA13
	 *
	 */

	/**
	 * The lists of used NIS HOSTS, key pairs, graph nodes, graph edges and graph meta data.
	 */
	private static final List<String> HOSTS = Arrays.asList(
			"127.0.0.1", "127.0.0.11", "127.0.0.12",
			"127.0.0.13", "127.0.0.14", "127.0.0.15");
	private static final List<KeyPair> KEY_PAIRS = Arrays.asList(
			new KeyPair(Utils.generateRandomPublicKey()),
			new KeyPair(Utils.generateRandomPublicKey()),
			new KeyPair(Utils.generateRandomPublicKey()),
			new KeyPair(Utils.generateRandomPublicKey()),
			new KeyPair(Utils.generateRandomPublicKey()),
			new KeyPair(Utils.generateRandomPublicKey()));
	private static final List<GraphNode> GRAPH_NODES = Arrays.asList(
			new GraphNode(createNode(HOSTS.get(0))),
			new GraphNode(createNode(HOSTS.get(1))),
			new GraphNode(createNode(HOSTS.get(2))),
			new GraphNode(createNode(HOSTS.get(3))),
			new GraphNode(createNode(HOSTS.get(4))),
			new GraphNode(createNode(HOSTS.get(5))));
	private static final List<GraphEdge> GRAPH_EDGES = Arrays.asList(
			new GraphEdge(GRAPH_NODES.get(0), GRAPH_NODES.get(1)),
			new GraphEdge(GRAPH_NODES.get(0), GRAPH_NODES.get(3)),
			new GraphEdge(GRAPH_NODES.get(0), GRAPH_NODES.get(4)),
			new GraphEdge(GRAPH_NODES.get(1), GRAPH_NODES.get(2)),
			new GraphEdge(GRAPH_NODES.get(1), GRAPH_NODES.get(3)),
			new GraphEdge(GRAPH_NODES.get(1), GRAPH_NODES.get(5)),
			new GraphEdge(GRAPH_NODES.get(2), GRAPH_NODES.get(1)),
			new GraphEdge(GRAPH_NODES.get(3), GRAPH_NODES.get(0)));
	private static final List<GraphNodeMetaData> GRAPH_NODE_META_DATAS = Arrays.asList(
			new GraphNodeMetaData(createNode(HOSTS.get(0)), true),
			new GraphNodeMetaData(createNode(HOSTS.get(1)), true),
			new GraphNodeMetaData(createNode(HOSTS.get(2)), true),
			new GraphNodeMetaData(createNode(HOSTS.get(3)), true),
			new GraphNodeMetaData(createNode(HOSTS.get(4)), false),
			new GraphNodeMetaData(createNode(HOSTS.get(5)), false));

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForUrlListConsistingOfA01() {
		// Assert (true = use constructed endpoints in createViewModel call):
		this.assertCreateViewModelReturnsCorrectGraphViewModel(true);
	}

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForUrlListConsistingOfA11() {
		// Arrange
		final List<NodeEndpoint> endpoints = Collections.singletonList(NodeEndpoint.fromHost("127.0.0.11"));
		final TestContext context = new TestContext(endpoints);
		final GraphViewModelFactory factory = new GraphViewModelFactory();

		// Act
		final GraphViewModel viewModel = factory.createViewModel(context.networkServices, context.nodeServices, endpoints);

		final Set<GraphNode> expectedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedNodes.addAll(this.getGraphNodes(Arrays.asList(1, 2, 3, 5)));
		final Set<GraphEdge> expectedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedEdges.addAll(this.getGraphEdges(Arrays.asList(3, 4, 5)));
		final Graph expectedGraph = new Graph(expectedNodes, expectedEdges);
		final Collection<GraphNodeMetaData> expectedGraphNodeMetaData = this.getGraphNodeMetaData(Arrays.asList(1, 2, 3, 5));
		final GraphMetaData expectedGraphMetaData = new GraphMetaData();
		expectedGraphMetaData.addAll(expectedGraphNodeMetaData);

		// Assert
		Assert.assertThat(viewModel.getGraph(), IsEqual.equalTo(expectedGraph));
		Assert.assertThat(viewModel.getMetaData(), IsEqual.equalTo(expectedGraphMetaData));
	}

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForUrlListConsistingOfA12() {
		// Arrange
		final List<NodeEndpoint> endpoints = Collections.singletonList(NodeEndpoint.fromHost("127.0.0.12"));
		final TestContext context = new TestContext(endpoints);
		final GraphViewModelFactory factory = new GraphViewModelFactory();

		// Act
		final GraphViewModel viewModel = factory.createViewModel(context.networkServices, context.nodeServices, endpoints);

		final Set<GraphNode> expectedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedNodes.addAll(this.getGraphNodes(Arrays.asList(1, 2)));
		final Set<GraphEdge> expectedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedEdges.addAll(this.getGraphEdges(Collections.singletonList(6)));
		final Graph expectedGraph = new Graph(expectedNodes, expectedEdges);
		final Collection<GraphNodeMetaData> expectedGraphNodeMetaData = this.getGraphNodeMetaData(Arrays.asList(1, 2));
		final GraphMetaData expectedGraphMetaData = new GraphMetaData();
		expectedGraphMetaData.addAll(expectedGraphNodeMetaData);

		// Assert
		Assert.assertThat(viewModel.getGraph(), IsEqual.equalTo(expectedGraph));
		Assert.assertThat(viewModel.getMetaData(), IsEqual.equalTo(expectedGraphMetaData));
	}

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForUrlListConsistingOfA10AndA11() {
		// Arrange:
		final List<NodeEndpoint> endpoints = Arrays.asList(
				NodeEndpoint.fromHost("127.0.0.1"),
				NodeEndpoint.fromHost("127.0.0.11"));
		final TestContext context = new TestContext(endpoints);
		final GraphViewModelFactory factory = new GraphViewModelFactory();

		// Act
		final GraphViewModel viewModel = factory.createViewModel(context.networkServices, context.nodeServices, endpoints);

		final Set<GraphNode> expectedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedNodes.addAll(GRAPH_NODES);
		final Set<GraphEdge> expectedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedEdges.addAll(this.getGraphEdges(Arrays.asList(0, 1, 2, 3, 4, 5)));
		final Graph expectedGraph = new Graph(expectedNodes, expectedEdges);
		final Collection<GraphNodeMetaData> expectedGraphNodeMetaData = GRAPH_NODE_META_DATAS;
		final GraphMetaData expectedGraphMetaData = new GraphMetaData();
		expectedGraphMetaData.addAll(expectedGraphNodeMetaData);

		// Assert
		Assert.assertThat(viewModel.getGraph(), IsEqual.equalTo(expectedGraph));
		Assert.assertThat(viewModel.getMetaData(), IsEqual.equalTo(expectedGraphMetaData));
	}

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForUrlListConsistingOfAllActiveNodes() {
		// Arrange:
		final List<NodeEndpoint> endpoints = Arrays.asList(
				NodeEndpoint.fromHost("127.0.0.1"),
				NodeEndpoint.fromHost("127.0.0.11"),
				NodeEndpoint.fromHost("127.0.0.12"),
				NodeEndpoint.fromHost("127.0.0.13"));
		final TestContext context = new TestContext(endpoints);
		final GraphViewModelFactory factory = new GraphViewModelFactory();

		// Act
		final GraphViewModel viewModel = factory.createViewModel(context.networkServices, context.nodeServices, endpoints);

		final Set<GraphNode> expectedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedNodes.addAll(GRAPH_NODES);
		final Set<GraphEdge> expectedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedEdges.addAll(GRAPH_EDGES);
		final Graph expectedGraph = new Graph(expectedNodes, expectedEdges);
		final Collection<GraphNodeMetaData> expectedGraphNodeMetaData = GRAPH_NODE_META_DATAS;
		final GraphMetaData expectedGraphMetaData = new GraphMetaData();
		expectedGraphMetaData.addAll(expectedGraphNodeMetaData);

		// Assert
		Assert.assertThat(viewModel.getGraph(), IsEqual.equalTo(expectedGraph));
		Assert.assertThat(viewModel.getMetaData(), IsEqual.equalTo(expectedGraphMetaData));
	}

	@Test
	public void buildGraphReturnsCorrectGraphViewModelForNoUrlList() {
		// Assert (false = do not use constructed endpoints in createViewModel call):
		this.assertCreateViewModelReturnsCorrectGraphViewModel(false);
	}

	private void assertCreateViewModelReturnsCorrectGraphViewModel(final boolean useConstructedEndpoints) {
		// Arrange
		final List<NodeEndpoint> endpoints = Collections.singletonList(NodeEndpoint.fromHost("127.0.0.1"));
		final TestContext context = new TestContext(endpoints);
		final GraphViewModelFactory factory = new GraphViewModelFactory();

		// Act
		final GraphViewModel viewModel = useConstructedEndpoints
				? factory.createViewModel(context.networkServices, context.nodeServices, endpoints)
				: factory.createViewModel(context.networkServices, context.nodeServices);

		final Set<GraphNode> expectedNodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedNodes.addAll(this.getGraphNodes(Arrays.asList(0, 1, 3, 4)));
		final Set<GraphEdge> expectedEdges = Collections.newSetFromMap(new ConcurrentHashMap<>());
		expectedEdges.addAll(this.getGraphEdges(Arrays.asList(0, 1, 2)));
		final Graph expectedGraph = new Graph(expectedNodes, expectedEdges);
		final Collection<GraphNodeMetaData> expectedGraphNodeMetaData = this.getGraphNodeMetaData(Arrays.asList(0, 1, 3, 4));
		final GraphMetaData expectedGraphMetaData = new GraphMetaData();
		expectedGraphMetaData.addAll(expectedGraphNodeMetaData);

		// Assert
		Assert.assertThat(viewModel.getGraph(), IsEqual.equalTo(expectedGraph));
		Assert.assertThat(viewModel.getMetaData(), IsEqual.equalTo(expectedGraphMetaData));
	}

	/**
	 * Return list of graph nodes given a list of indices.
	 *
	 * @param indices The list of indices.
	 * @return The list of graph nodes.
	 */
	private List<GraphNode> getGraphNodes(final List<Integer> indices) {
		final List<GraphNode> nodes = new ArrayList<>();
		indices.stream().forEach(i -> nodes.add(GRAPH_NODES.get(i)));
		return nodes;
	}

	/**
	 * Return list of graph edges given a list of indices.
	 *
	 * @param indices The list of indices.
	 * @return The list of graph edges.
	 */
	private List<GraphEdge> getGraphEdges(final List<Integer> indices) {
		final List<GraphEdge> edges = new ArrayList<>();
		indices.stream().forEach(i -> edges.add(GRAPH_EDGES.get(i)));
		return edges;
	}

	/**
	 * Return list of graph meta data given a list of indices.
	 *
	 * @param indices The list of indices.
	 * @return The list of graph meta data.
	 */
	private List<GraphNodeMetaData> getGraphNodeMetaData(final List<Integer> indices) {
		final List<GraphNodeMetaData> metaData = new ArrayList<>();
		indices.stream().forEach(i -> metaData.add(GRAPH_NODE_META_DATAS.get(i)));
		return metaData;
	}

	/**
	 * Create a node with a given host.
	 *
	 * @param host The host.
	 * @return The node.
	 */
	private static Node createNode(final String host) {
		final NodeIdentity identity = new NodeIdentity(KEY_PAIRS.get(HOSTS.indexOf(host)), "alice" + (HOSTS.indexOf(host) + 1));
		final NodeEndpoint endpoint = NodeEndpoint.fromHost(host);
		final NodeMetaData metaData = new NodeMetaData("Linux", "NIS");
		return new Node(identity, endpoint, metaData);
	}

	/**
	 * Test context class.
	 */
	private static class TestContext {
		private final NetworkServices networkServices;
		private final NodeServices nodeServices;
		private final Map<Node, NodeCollection> neighborhoods;

		public TestContext(final Collection<NodeEndpoint> endpoints) {
			this.networkServices = Mockito.mock(NetworkServices.class);
			this.nodeServices = Mockito.mock(NodeServices.class);
			this.neighborhoods = new HashMap<>();
			this.prepareNeighborhoods();

			final Map<Node, NodeCollection> visibleNeighborhoods = new HashMap<>();
			this.neighborhoods.entrySet().stream()
					.filter(e -> endpoints.contains(e.getKey().getEndpoint()))
					.forEach(e -> visibleNeighborhoods.put(e.getKey(), e.getValue()));

			Mockito.when(this.nodeServices.getNodesAsync(Mockito.any()))
					.thenReturn(CompletableFuture.completedFuture(null));
			Mockito.when(this.nodeServices.getNodeAsync(Mockito.any()))
					.thenReturn(CompletableFuture.completedFuture(createNode(HOSTS.get(0))));
			Mockito.when(this.networkServices.getNodePeerListsAsync(Mockito.any()))
					.thenReturn(CompletableFuture.completedFuture(visibleNeighborhoods));
		}

		/**
		 * Creates a hash map with NodeCollection neighborhood for each given node.
		 */
		private void prepareNeighborhoods() {
			// A01
			this.addNodeWithNeighbors(
					"127.0.0.1",
					Arrays.asList(createNode("127.0.0.11"), createNode("127.0.0.13")),
					Collections.singletonList(createNode("127.0.0.14")));

			// A11
			this.addNodeWithNeighbors(
					"127.0.0.11",
					Arrays.asList(createNode("127.0.0.12"), createNode("127.0.0.13")),
					Collections.singletonList(createNode("127.0.0.15")));

			// A12
			this.addNodeWithNeighbors("127.0.0.12", Collections.singletonList(createNode("127.0.0.11")), Collections.emptyList());

			// A13
			this.addNodeWithNeighbors("127.0.0.13", Collections.singletonList(createNode("127.0.0.1")), Collections.emptyList());

			// I14
			this.addNodeWithoutNeighbors("127.0.0.14");

			// I15
			this.addNodeWithoutNeighbors("127.0.0.15");
		}

		private void addNodeWithoutNeighbors(final String host) {
			final Node node = createNode(host);
			this.neighborhoods.put(node, new NodeCollection());
		}

		private void addNodeWithNeighbors(
				final String host,
				final Collection<Node> activeNodes,
				final Collection<Node> inactiveNodes) {
			final Node node = createNode(host);
			this.neighborhoods.put(node, createNodeCollection(activeNodes, inactiveNodes));
		}

		private static NodeCollection createNodeCollection(
				final Collection<Node> activeNodes,
				final Collection<Node> inactiveNodes) {
			final NodeCollection nodes = new NodeCollection();
			activeNodes.forEach(n -> nodes.update(n, NodeStatus.ACTIVE));
			inactiveNodes.forEach(n -> nodes.update(n, NodeStatus.BUSY));
			return nodes;
		}
	}

	//endregion
}
