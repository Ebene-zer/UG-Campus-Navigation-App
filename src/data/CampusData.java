import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages and provides analysis for campus map data, represented as a graph
 * of nodes (locations) and edges (paths).
 * This class is designed to be immutable after construction.
 */
public class CampusData {

    // A constant for the Earth's radius in meters.
    private static final int EARTH_RADIUS_METERS = 6371000;

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final Map<String, Node> nodeMap;
    private final Map<String, List<Edge>> adjacencyList;

    /**
     * Constructs a CampusData object by loading node and edge data.
     */
    public CampusData() {
        this.nodes = Node.loadAllNodes(); // Assuming these return non-null lists
        this.edges = Edge.loadAllEdges();
        
        // Use streams for more concise and modern initialization.
        this.nodeMap = nodes.stream()
                .collect(Collectors.toMap(Node::getId, Function.identity()));
        
        this.adjacencyList = new HashMap<>();
        initializeAdjacencyList();
    }

    /**
     * Populates the adjacency list from the edges, creating a bidirectional graph.
     */
    private void initializeAdjacencyList() {
        for (Edge edge : edges) {
            // Ensure the edge's nodes are valid before adding.
            if (nodeMap.containsKey(edge.getSource()) && nodeMap.containsKey(edge.getTarget())) {
                // Add forward edge. computeIfAbsent creates the list only if it doesn't exist.
                adjacencyList.computeIfAbsent(edge.getSource(), k -> new ArrayList<>()).add(edge);

                // Create and add the reverse edge for bidirectional travel.
                Edge reverseEdge = new Edge(
                    edge.getTarget(),
                    edge.getSource(),
                    edge.getDistance(),
                    edge.getPathType(),
                    edge.getTimeMinutes()
                );
                adjacencyList.computeIfAbsent(edge.getTarget(), k -> new ArrayList<>()).add(reverseEdge);
            }
        }
    }

    // --- Getters for Core Data Structures ---

    /**
     * @return An unmodifiable list of all nodes on campus.
     */
    public List<Node> getAllNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * @return An unmodifiable list of all original edges (pathways).
     */
    public List<Edge> getAllEdges() {
        return Collections.unmodifiableList(edges);
    }
    
    /**
     * @return An unmodifiable view of the adjacency list representing the graph.
     */
    public Map<String, List<Edge>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    // --- Node-Related Utility Methods ---

    /**
     * Finds a node by its unique ID.
     *
     * @param id The ID of the node to find.
     * @return An {@link Optional} containing the node if found, or an empty Optional otherwise.
     */
    public Optional<Node> getNodeById(String id) {
        return Optional.ofNullable(nodeMap.get(id));
    }

    /**
     * @param category The category to search for (case-insensitive).
     * @return A list of nodes matching the specified category.
     */
    public List<Node> getNodesByCategory(String category) {
        return nodes.stream()
                .filter(node -> node.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * @return A list of all unique node categories.
     */
    public List<String> getAllCategories() {
        return nodes.stream()
                .map(Node::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    // --- Edge-Related Utility Methods ---

    /**
     * @param nodeId The ID of the node.
     * @return A list of all outgoing edges from the specified node.
     */
    public List<Edge> getEdgesFromNode(String nodeId) {
        return Collections.unmodifiableList(
            adjacencyList.getOrDefault(nodeId, Collections.emptyList())
        );
    }

    // --- Search and Filtering Methods ---
    
    /**
     * Searches for nodes whose names contain the given search term.
     *
     * @param searchTerm The text to search for (case-insensitive).
     * @return A list of matching nodes.
     */
    public List<Node> searchNodesByName(String searchTerm) {
        final String lowerSearchTerm = searchTerm.toLowerCase();
        return nodes.stream()
                .filter(node -> node.getName().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all nodes within a specified radius of a geographic coordinate.
     *
     * @param latitude       The latitude of the center point.
     * @param longitude      The longitude of the center point.
     * @param radiusMeters   The search radius in meters.
     * @return A list of nodes within the radius.
     */
    public List<Node> findNodesNearLocation(double latitude, double longitude, double radiusMeters) {
        return nodes.stream()
                .filter(node -> calculateHaversineDistance(
                    node.getLatitude(), node.getLongitude(),
                    latitude, longitude) <= radiusMeters)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the distance between two lat/lon points using the Haversine formula.
     * @return The distance in meters.
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    // --- Data Validation Methods ---

    /**
     * Checks if all edges refer to valid, existing nodes.
     * @return true if the data is consistent, false otherwise.
     */
    public boolean validateDataIntegrity() {
        return edges.stream()
                .allMatch(edge -> nodeMap.containsKey(edge.getSource()) && nodeMap.containsKey(edge.getTarget()));
    }
    
    /**
     * Finds nodes that are not connected to any edges.
     * @return A list of IDs of orphaned nodes.
     */
    public List<String> findOrphanedNodes() {
        Set<String> connectedNodeIds = edges.stream()
                .flatMap(edge -> Stream.of(edge.getSource(), edge.getTarget()))
                .collect(Collectors.toSet());

        return nodes.stream()
                .map(Node::getId)
                .filter(id -> !connectedNodeIds.contains(id))
                .collect(Collectors.toList());
    }

    // --- Statistics and Analytics ---

    /**
     * @return A map of node categories to the count of nodes in each category.
     */
    public Map<String, Long> getNodeCountByCategory() {
        return nodes.stream()
                .collect(Collectors.groupingBy(Node::getCategory, Collectors.counting()));
    }

    /**
     * @return A map of path types to the count of edges of each type.
     */
    public Map<String, Long> getEdgeCountByPathType() {
        return edges.stream()
                .collect(Collectors.groupingBy(Edge::getPathType, Collectors.counting()));
    }

    /**
     * @return An {@link OptionalDouble} with the average length of all edges.
     */
    public OptionalDouble getAverageEdgeLength() {
        return edges.stream()
                .mapToDouble(Edge::getDistance)
                .average();
    }

    /**
     * @return An {@link Optional} containing the longest edge by distance.
     */
    public Optional<Edge> getLongestEdge() {
        return edges.stream()
                .max(Comparator.comparingDouble(Edge::getDistance));
    }

    /**
     * @return An {@link Optional} containing the shortest edge by distance.
     */
    public Optional<Edge> getShortestEdge() {
        return edges.stream()
                .min(Comparator.comparingDouble(Edge::getDistance));
    }

    // --- Graph Analysis ---

    /**
     * Finds all disconnected groups of nodes in the graph.
     * @return A list of sets, where each set contains the node IDs of a connected component.
     */
    public List<Set<String>> getConnectedComponents() {
        Set<String> visited = new HashSet<>();
        List<Set<String>> components = new ArrayList<>();
        
        for (String nodeId : nodeMap.keySet()) {
            if (!visited.contains(nodeId)) {
                Set<String> component = new HashSet<>();
                dfs(nodeId, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    /**
     * Recursive Depth-First Search helper to find all nodes in a component.
     */
    private void dfs(String nodeId, Set<String> visited, Set<String> component) {
        visited.add(nodeId);
        component.add(nodeId);
        
        for (Edge edge : adjacencyList.getOrDefault(nodeId, Collections.emptyList())) {
            if (!visited.contains(edge.getTarget())) {
                dfs(edge.getTarget(), visited, component);
            }
        }
    }
    
    /**
     * @return A map of each node's ID to its degree (number of connections).
     */
    public Map<String, Integer> getNodeDegrees() {
        return adjacencyList.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().size()
                ));
    }

    /**
     * Finds the most connected nodes in the graph.
     * @param limit The maximum number of nodes to return.
     * @return A list of the top 'limit' most connected nodes.
     */
    public List<Node> getMostConnectedNodes(int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        
        return getNodeDegrees().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> nodeMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("CampusData: %d nodes, %d edges, %.2f km total pathway",
                nodes.size(), edges.size(),
                edges.stream().mapToDouble(Edge::getDistance).sum() / 1000.0);
    }
}