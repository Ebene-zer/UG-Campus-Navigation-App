package algorithms;

import java.util.*;

/**
 * Represents the University of Ghana campus as a graph data structure.
 * This class uses the existing Node and Edge classes from your project.
 */
public class Graph {
    private final Map<String, List<Edge>> adjList = new HashMap<>();
    private final Map<String, Node> nodeMap = new HashMap<>();

    // Singleton instance
    private static Graph instance;

    /**
     * Private constructor to enforce singleton pattern
     */
    private Graph() {
        buildGraph();
    }

    /**
     * Gets the singleton instance of the Graph
     */
    public static Graph getInstance() {
        if (instance == null) {
            instance = new Graph();
        }
        return instance;
    }

    /**
     * Populates the graph from the Node and Edge classes
     */
    public void buildGraph() {
        // Load all nodes and edges
        List<Node> nodes = Node.loadAllNodes();
        List<Edge> edges = Edge.loadAllEdges();

        // Create node map for quick lookup
        for (Node node : nodes) {
            nodeMap.put(node.getId(), node);
            adjList.put(node.getId(), new ArrayList<>());
        }

        // Add edges to adjacency list (bidirectional)
        for (Edge edge : edges) {
            // Add forward edge
            if (adjList.containsKey(edge.getSource())) {
                adjList.get(edge.getSource()).add(edge);
            }
            
            // Add reverse edge for bidirectional movement
            Edge reverseEdge = new Edge(
                edge.getTarget(), 
                edge.getSource(), 
                edge.getDistance(), 
                edge.getPathType(), 
                edge.getTimeMinutes()
            );
            
            if (adjList.containsKey(edge.getTarget())) {
                adjList.get(edge.getTarget()).add(reverseEdge);
            }
        }
    }

    /**
     * Gets a node by its ID
     */
    public Node getNode(String nodeId) {
        return nodeMap.get(nodeId);
    }

    /**
     * Gets all edges from a node
     */
    public List<Edge> getEdgesFromNode(String nodeId) {
        return adjList.getOrDefault(nodeId, Collections.emptyList());
    }

    /**
     * Gets all nodes in the graph
     */
    public Collection<Node> getAllNodes() {
        return nodeMap.values();
    }

    /**
     * Gets all node IDs in the graph
     */
    public Set<String> getAllNodeIds() {
        return nodeMap.keySet();
    }

    /**
     * Finds the shortest path using Dijkstra's algorithm
     */
    public PathResult findShortestPath(String startId, String endId) {
        if (!nodeMap.containsKey(startId) || !nodeMap.containsKey(endId)) {
            return null;
        }

        // Initialize data structures
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Initialize all distances to infinity
        for (String nodeId : nodeMap.keySet()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);
        pq.add(new NodeDistance(startId, 0.0));

        // Dijkstra's algorithm
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            
            if (visited.contains(current.nodeId)) continue;
            visited.add(current.nodeId);
            
            if (current.nodeId.equals(endId)) break;
            
            for (Edge edge : adjList.get(current.nodeId)) {
                String neighborId = edge.getTarget();
                double newDist = distances.get(current.nodeId) + edge.getDistance();
                
                if (newDist < distances.get(neighborId)) {
                    distances.put(neighborId, newDist);
                    previous.put(neighborId, current.nodeId);
                    pq.add(new NodeDistance(neighborId, newDist));
                }
            }
        }

        // Build the path
        List<String> path = new ArrayList<>();
        double totalDistance = distances.get(endId);
        
        if (totalDistance == Double.MAX_VALUE) {
            return null; // No path found
        }
        
        // Backtrack to build the path
        String currentId = endId;
        while (currentId != null) {
            path.add(0, currentId);
            currentId = previous.get(currentId);
        }
        
        return new PathResult(path, totalDistance);
    }

    /**
     * Finds the fastest path (considering time instead of distance)
     */
    public PathResult findFastestPath(String startId, String endId) {
        if (!nodeMap.containsKey(startId) || !nodeMap.containsKey(endId)) {
            return null;
        }

        // Initialize data structures
        Map<String, Integer> times = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeTime> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Initialize all times to maximum
        for (String nodeId : nodeMap.keySet()) {
            times.put(nodeId, Integer.MAX_VALUE);
        }
        times.put(startId, 0);
        pq.add(new NodeTime(startId, 0));

        // Dijkstra's algorithm using time
        while (!pq.isEmpty()) {
            NodeTime current = pq.poll();
            
            if (visited.contains(current.nodeId)) continue;
            visited.add(current.nodeId);
            
            if (current.nodeId.equals(endId)) break;
            
            for (Edge edge : adjList.get(current.nodeId)) {
                String neighborId = edge.getTarget();
                int newTime = times.get(current.nodeId) + edge.getTimeMinutes();
                
                if (newTime < times.get(neighborId)) {
                    times.put(neighborId, newTime);
                    previous.put(neighborId, current.nodeId);
                    pq.add(new NodeTime(neighborId, newTime));
                }
            }
        }

        // Build the path
        List<String> path = new ArrayList<>();
        int totalTime = times.get(endId);
        
        if (totalTime == Integer.MAX_VALUE) {
            return null; // No path found
        }
        
        // Backtrack to build the path
        String currentId = endId;
        while (currentId != null) {
            path.add(0, currentId);
            currentId = previous.get(currentId);
        }
        
        return new PathResult(path, totalTime);
    }

    /**
     * Helper class for priority queue in Dijkstra's algorithm (distance)
     */
    private static class NodeDistance implements Comparable<NodeDistance> {
        String nodeId;
        double distance;
        
        NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * Helper class for priority queue in Dijkstra's algorithm (time)
     */
    private static class NodeTime implements Comparable<NodeTime> {
        String nodeId;
        int time;
        
        NodeTime(String nodeId, int time) {
            this.nodeId = nodeId;
            this.time = time;
        }
        
        @Override
        public int compareTo(NodeTime other) {
            return Integer.compare(this.time, other.time);
        }
    }

    /**
     * Class to represent the result of a pathfinding algorithm
     */
    public static class PathResult {
        private List<String> nodeIds;
        private double totalValue; // Could be distance or time
        
        public PathResult(List<String> nodeIds, double totalValue) {
            this.nodeIds = nodeIds;
            this.totalValue = totalValue;
        }
        
        public List<String> getNodeIds() {
            return nodeIds;
        }
        
        public double getTotalValue() {
            return totalValue;
        }
        
        public List<Node> getNodes(Map<String, Node> nodeMap) {
            List<Node> nodes = new ArrayList<>();
            for (String id : nodeIds) {
                nodes.add(nodeMap.get(id));
            }
            return nodes;
        }
        
        @Override
        public String toString() {
            return "Path: " + nodeIds + " (Total: " + totalValue + ")";
        }
    }

    /**
     * Test method to demonstrate the graph functionality
     */
    public static void main(String[] args) {
        Graph graph = Graph.getInstance();
        
        // Test shortest path
        PathResult result = graph.findShortestPath("gate1", "lib1");
        if (result != null) {
            System.out.println("Shortest path from Main Gate to Balme Library:");
            System.out.println("Distance: " + result.getTotalValue() + " meters");
            System.out.println("Path: " + result.getNodeIds());
        }
        
        // Test fastest path
        PathResult timeResult = graph.findFastestPath("gate1", "lib1");
        if (timeResult != null) {
            System.out.println("\nFastest path from Main Gate to Balme Library:");
            System.out.println("Time: " + timeResult.getTotalValue() + " minutes");
            System.out.println("Path: " + timeResult.getNodeIds());
        }
    }
}