package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Dijkstra {
    private Graph graph;
    public Dijkstra(Graph graph) {
        this.graph = graph;
    }
    public List<Node> shortestDistance(Node start, Node goal) {
        // Maps each node to the node it came from (used to reconstruct the path)
        Map<Node, Node> cameFrom = new HashMap<>();

        // gScore = cost of the cheapest path from start to the current node
        Map<Node, Double> gScore = new HashMap<>();

        // Priority queue ordered by gScore (lowest first)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(gScore::get));

        // Initialize gScore for all nodes
        for (Node node : graph.getNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
        }

        // Starting node has cost 0
        gScore.put(start, 0.0);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll(); // Node with lowest gScore

            // If we reached the goal, reconstruct and return the path
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            // Process neighbors
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                double tentativeGScore = gScore.get(current) + edge.getWeight();

                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path); // Reverse to get the path from start to goal
        return path;
    }
}
