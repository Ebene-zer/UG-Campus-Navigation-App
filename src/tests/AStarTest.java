package tests;

import algorithms.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for the A* pathfinding algorithm.
 *
 * These tests validate different scenarios:
 * 1. Simple connected graph with a clear shortest path
 * 2. Direct vs. indirect path comparison
 * 3. No path available
 * 4. Start equals goal
 */
public class AStarTest {

    @Test
    public void testSimplePath() {
        // Create graph
        Graph graph = new Graph();

        // Create nodes with coordinates (for heuristic)
        Node bank = new Node("Bank", 2, 4);
        Node library = new Node("Library", 5, 7);
        Node cafeteria = new Node("Cafeteria", 6, 1);

        // Add nodes to graph
        graph.addNode(bank);
        graph.addNode(library);
        graph.addNode(cafeteria);

        // Add weighted edges
        graph.addEdge(bank, library, 2);        // cheaper
        graph.addEdge(library, cafeteria, 2);  // cheaper
        graph.addEdge(bank, cafeteria, 10);    // more expensive

        // Run A* from Bank to Cafeteria
        List<Node> path = AStar.findPath(graph, bank, cafeteria);

        // Verify result
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals("Bank", path.get(0).getName(), "Path should start at Bank");
        assertEquals("Cafeteria", path.get(path.size() - 1).getName(), "Path should end at Cafeteria");
        assertEquals(3, path.size(), "Path should have 3 nodes (Bank → Library → Cafeteria)");
    }

    @Test
    public void testDirectPathIsChosenWhenCheaper() {
        Graph graph = new Graph();

        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 1);
        Node c = new Node("C", 2, 2);

        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);

        // Direct edge is cheaper
        graph.addEdge(a, c, 3);
        graph.addEdge(a, b, 5);
        graph.addEdge(b, c, 5);

        List<Node> path = AStar.findPath(graph, a, c);

        // Expect direct route: A → C
        assertEquals(2, path.size(), "Direct path should be chosen (A → C)");
        assertEquals("A", path.get(0).getName());
        assertEquals("C", path.get(1).getName());
    }

    @Test
    public void testNoPathExists() {
        Graph graph = new Graph();

        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 1, 1);

        graph.addNode(a);
        graph.addNode(b);

        // No edge between A and B

        List<Node> path = AStar.findPath(graph, a, b);

        // Should return empty path
        assertTrue(path.isEmpty(), "No path should exist between A and B");
    }

    @Test
    public void testStartEqualsGoal() {
        Graph graph = new Graph();

        Node a = new Node("A", 0, 0);
        graph.addNode(a);

        // Start and goal are the same
        List<Node> path = AStar.findPath(graph, a, a);

        // Path should contain only one node
        assertEquals(1, path.size(), "Path should contain only the start node");
        assertEquals("A", path.get(0).getName());
    }
}
