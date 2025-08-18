package algorithms;

import java.util.*;

/**
 * Implementation of the A* pathfinding algorithm.
 *
 * A* finds the shortest path from a start node to a goal node
 * using a combination of actual cost (gScore) and heuristic estimate (fScore).
 */
public class AStar {

    /**
     * Finds the shortest path between two nodes using A*.
     *
     * @param graph the graph containing nodes and edges
     * @param start the starting node
     * @param goal  the destination node
     * @return the list of nodes representing the path from start to goal
     */
    public static List<Node> findPath(Graph graph, Node start, Node goal) {
        // Maps each node to the node it came from (used to reconstruct the path)
        Map<Node, Node> cameFrom = new HashMap<>();

        // gScore = cost of the cheapest path from start to the current node
        Map<Node, Double> gScore = new HashMap<>();

        // fScore = gScore + heuristic (estimated cost to goal)
        Map<Node, Double> fScore = new HashMap<>();

        // Keeps track of nodes already evaluated
        Set<Node> closedSet = new HashSet<>();

        // Initialize gScore and fScore for all nodes
        for (Node node : graph.getNodes()) {
            gScore.put(node, Double.POSITIVE_INFINITY);
            fScore.put(node, Double.POSITIVE_INFINITY);
        }

        // Starting node has cost 0
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));

        // Priority queue ordered by fScore (lowest first)
        PriorityQueue<Node> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(fScore::get)
        );
        openSet.add(start);

        // Main loop: process nodes until openSet is empty
        while (!openSet.isEmpty()) {
            Node current = openSet.poll(); // Node with lowest fScore

            // If we already processed this node, skip it
            if (closedSet.contains(current)) continue;
            closedSet.add(current);

            // If we've reached the goal, reconstruct and return the path
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            // Explore all neighbors of the current node
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                double tentativeG = gScore.get(current) + edge.getWeight(); // cost so far

                // If we found a cheaper path to this neighbor
                if (tentativeG < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current); // Remember the best path
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, goal));

                    // Update priority queue with the new cost
                    openSet.remove(neighbor); // remove old entry if present
                    openSet.add(neighbor);    // re-add with updated fScore
                }
            }
        }

        // If we exit the loop, no path was found
        return new ArrayList<>();
    }

    /**
     * Heuristic function: estimates cost from node a to node b.
     * Here we use Euclidean distance (straight-line distance).
     */
    private static double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Reconstructs the path from the cameFrom map.
     *
     * @param cameFrom mapping of nodes to their previous node
     * @param current  the goal node (trace backwards to start)
     * @return list of nodes representing the path from start to goal
     */
    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path); // Reverse so path goes start → goal
        return path;
    }
}
