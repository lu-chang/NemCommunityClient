package org.nem.ncc.model.graph;

import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.serialization.Deserializer;
import org.nem.ncc.test.*;

import java.util.*;

public class GraphTest {

	// region constructor

	@Test
	public void canCreateGraphWithoutParameters() {
		// Act:
		final Graph graph = new Graph();

		// Assert:
		Assert.assertThat(graph.getNodes().size(), IsEqual.equalTo(0));
		Assert.assertThat(graph.getEdges().size(), IsEqual.equalTo(0));
	}

	@Test
	public void canCreateGraphFromGivenNodesAndEdges() {
		// Arrange:
		final GraphNode node1 = GraphUtils.createGraphNode();
		final GraphNode node2 = GraphUtils.createGraphNode();
		final GraphEdge edge = GraphUtils.createGraphEdge(node1, node2);

		// Act
		final Graph graph = new Graph(Arrays.asList(node1, node2), Collections.singletonList(edge));

		// Assert:
		Assert.assertThat(graph.getNodes(), IsEquivalent.equivalentTo(Arrays.asList(node1, node2)));
		Assert.assertThat(graph.getEdges(), IsEquivalent.equivalentTo(Collections.singletonList(edge)));
	}

	// endregion

	// region add

	@Test
	public void canAddNodesToGraph() {
		// Arrange
		final Graph graph = new Graph();
		final GraphNode node1 = GraphUtils.createGraphNode();
		final GraphNode node2 = GraphUtils.createGraphNode();

		// Act
		graph.addNode(node1);
		graph.addNode(node2);

		// Assert:
		Assert.assertThat(graph.getNodes(), IsEquivalent.equivalentTo(Arrays.asList(node1, node2)));
	}

	@Test
	public void canAddEdgesToGraph() {
		// Arrange
		final Graph graph = new Graph();
		final GraphNode node1 = GraphUtils.createGraphNode();
		final GraphNode node2 = GraphUtils.createGraphNode();
		final GraphEdge edge = GraphUtils.createGraphEdge(node1, node2);

		// Act
		graph.addEdge(edge);

		// Assert:
		Assert.assertThat(graph.getEdges(), IsEquivalent.equivalentTo(Collections.singletonList(edge)));
	}

	// endregion

	// region serialization

	@Test
	public void canRoundTripGraph() {
		// Arrange:
		final GraphNode node1 = GraphUtils.createGraphNode();
		final GraphNode node2 = GraphUtils.createGraphNode();
		final GraphEdge edge = GraphUtils.createGraphEdge(node1, node2);
		final Graph originalGraph = new Graph(Arrays.asList(node1, node2), Collections.singletonList(edge));

		// Act
		final Deserializer deserializer = Utils.roundtripSerializableEntity(originalGraph, null);
		final Graph graph = new Graph(deserializer);

		// Assert:
		Assert.assertThat(graph.getNodes(), IsEqual.equalTo(originalGraph.getNodes()));
		Assert.assertThat(graph.getEdges(), IsEqual.equalTo(originalGraph.getEdges()));
	}

	// endregion

	//region equals / hashCode

	@Test
	public void equalsOnlyReturnsTrueForEquivalentObjects() {
		// Act
		final List<Graph> graphs = createTestGraphs();

		// Assert:
		Assert.assertThat(graphs.get(0), IsEqual.equalTo(graphs.get(1)));
		Assert.assertThat(graphs.get(0), IsNot.not(IsEqual.equalTo(graphs.get(2))));
		Assert.assertThat(graphs.get(0), IsNot.not(IsEqual.equalTo(graphs.get(3))));
	}

	@Test
	public void hashCodesAreEqualForEquivalentObjects() {
		// Act
		final List<Graph> graphs = createTestGraphs();
		final int hashCode = graphs.get(0).hashCode();

		// Assert:
		Assert.assertThat(hashCode, IsEqual.equalTo(graphs.get(1).hashCode()));
		Assert.assertThat(hashCode, IsNot.not(IsEqual.equalTo(graphs.get(2).hashCode())));
		Assert.assertThat(hashCode, IsNot.not(IsEqual.equalTo(graphs.get(3).hashCode())));
	}

	private static List<Graph> createTestGraphs() {
		final GraphNode node1 = GraphUtils.createGraphNode();
		final GraphNode node2 = GraphUtils.createGraphNode();
		final GraphNode node3 = GraphUtils.createGraphNode();
		final GraphNode node4 = GraphUtils.createGraphNode();
		final GraphEdge edge1 = GraphUtils.createGraphEdge(node1, node2);
		final GraphEdge edge2 = GraphUtils.createGraphEdge(node1, node4);
		final GraphEdge edge3 = GraphUtils.createGraphEdge(node2, node3);
		final GraphEdge edge4 = GraphUtils.createGraphEdge(node4, node3);
		final Graph graph1 = new Graph(Arrays.asList(node1, node2, node3, node4), Arrays.asList(edge1, edge2, edge3, edge4));
		final Graph graph2 = new Graph(Arrays.asList(node4, node2, node1, node3), Arrays.asList(edge3, edge4, edge1, edge2));
		final Graph graph3 = new Graph(Arrays.asList(node1, node2, node3), Arrays.asList(edge1, edge2, edge3, edge4));
		final Graph graph4 = new Graph(Arrays.asList(node1, node2, node3, node4), Arrays.asList(edge1, edge2, edge3));
		return Arrays.asList(graph1, graph2, graph3, graph4);
	}

	//endregion

	//region string representation

	@Test
	public void graphStringRepresentationIsCorrect() {
		// Arrange
		final GraphNode node1 = GraphUtils.createGraphNodeWithId("Alice");
		final GraphNode node2 = GraphUtils.createGraphNodeWithId("Trudy");
		final GraphNode node3 = GraphUtils.createGraphNodeWithId("Bob");
		final GraphEdge edge1 = new GraphEdge(node1, node2);
		final GraphEdge edge2 = new GraphEdge(node2, node1);
		final GraphEdge edge3 = new GraphEdge(node2, node3);
		final GraphEdge edge4 = new GraphEdge(node3, node1);
		final Graph graph = new Graph(
				Arrays.asList(node1, node2, node3),
				Arrays.asList(edge1, edge2, edge3, edge4));

		// Assert:
		final String expectedResult =
				"[3 nodes, 4 edges]" + System.lineSeparator() +
						"node: ALICE" + System.lineSeparator() +
						"node: BOB" + System.lineSeparator() +
						"node: TRUDY" + System.lineSeparator() +
						"edge: ALICE-TRUDY" + System.lineSeparator() +
						"edge: BOB-ALICE" + System.lineSeparator() +
						"edge: TRUDY-ALICE" + System.lineSeparator() +
						"edge: TRUDY-BOB";
		Assert.assertThat(graph.toString(), IsEqual.equalTo(expectedResult));
	}

	//endregion
}
