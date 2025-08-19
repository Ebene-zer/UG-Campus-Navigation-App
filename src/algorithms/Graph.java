package algorithms;

import java.util.*;

/**
 * Represents the University of Ghana campus as a graph data structure.
 * This class now uses static, hardcoded arrays for nodes and paths,
 * removing the need for JSON parsing and external dependencies.
 * The graph is built automatically when an instance of this class is created.
 */
public class Graph {

    // The core data structure for the graph: a map from a node to its list of connections (edges).
    private final Map<Node, List<Edge>> adjList = new HashMap<>();

    // Helper class to define the static edge data clearly.
    private static class Path {
        final String src;
        final String dst;
        final double weight;

        Path(String src, String dst, double weight) {
            this.src = src;
            this.dst = dst;
            this.weight = weight;
        }
    }

    // --- STATIC CAMPUS DATA (USING ARRAYS) ---
    // All nodes (locations) on campus are defined here in a static array.
    private static final Node[] CAMPUS_NODES = new Node[] {
            new Node("Main Gate (Okponglo Entrance)", -0.1855, 5.6510),
            new Node("Balme Library", -0.1983, 5.6506),
            new Node("Jones Quartey Building (JQB)", -0.1982, 5.6531),
            new Node("Commonwealth Hall", -0.1996, 5.6558),
            new Node("Great Hall", -0.1990, 5.6515),
            new Node("Akuafo Hall", -0.1998, 5.6491),
            new Node("Central Cafeteria (CC)", -0.1947, 5.6498),
            new Node("Mensah Sarbah Hall", -0.1995, 5.6458),
            new Node("UGBS Main Campus", -0.1878, 5.6514),
            new Node("Volta Hall", -0.1979, 5.6548),
            new Node("Department of Computer Science", -0.1956, 5.6534),
            new Node("Legon Hall", -0.2016, 5.6483),
            new Node("University Stadium", -0.1945, 5.6441),
            new Node("University Hospital", -0.1865, 5.6429),
            new Node("Noguchi Memorial Institute", -0.1856, 5.6409),
            new Node("Night Market", -0.1985, 5.6450),
            new Node("Diaspora Hostels (Pentagon)", -0.1915, 5.6375),
            new Node("School of Law", -0.1985, 5.6401),
            new Node("Jubilee Hall", -0.2007, 5.6433),
            new Node("Department of Economics", -0.1925, 5.6521),
            new Node("GCB Bank", -0.1940, 5.6505),
            new Node("School of Performing Arts", -0.2033, 5.6493),
            new Node("Cedi Conference Centre", -0.1901, 5.6535),
            new Node("Department of Psychology", -0.1943, 5.6546),
            new Node("International Programmes Office", -0.1982, 5.6441)
    };

    // All edges (connections) between the nodes are defined here in a static array.
    private static final Path[] CAMPUS_EDGES = new Path[] {
            new Path("Main Gate (Okponglo Entrance)", "Balme Library", 1400),
            new Path("Balme Library", "Jones Quartey Building (JQB)", 300),
            new Path("Commonwealth Hall", "Great Hall", 500),
            new Path("Akuafo Hall", "Central Cafeteria (CC)", 550),
            new Path("Mensah Sarbah Hall", "UGBS Main Campus", 1300),
            new Path("Volta Hall", "Department of Computer Science", 350),
            new Path("Legon Hall", "University Stadium", 1100),
            new Path("University Hospital", "Noguchi Memorial Institute", 250),
            new Path("Night Market", "Diaspora Hostels (Pentagon)", 1100),
            new Path("School of Law", "Jubilee Hall", 450),
            new Path("Department of Economics", "Main Gate (Okponglo Entrance)", 750),
            new Path("Great Hall", "Akuafo Hall", 350),
            new Path("UGBS Main Campus", "GCB Bank", 650),
            new Path("Central Cafeteria (CC)", "Balme Library", 400),
            new Path("Jones Quartey Building (JQB)", "Commonwealth Hall", 400),
            new Path("University Stadium", "Mensah Sarbah Hall", 600),
            new Path("Diaspora Hostels (Pentagon)", "School of Law", 850),
            new Path("Noguchi Memorial Institute", "Main Gate (Okponglo Entrance)", 1100),
            new Path("Volta Hall", "Legon Hall", 900),
            new Path("Department of Computer Science", "Central Cafeteria (CC)", 450),
            new Path("GCB Bank", "Akuafo Hall", 600),
            new Path("Jubilee Hall", "Night Market", 300),
            new Path("Balme Library", "Department of Economics", 650),
            new Path("Great Hall", "Volta Hall", 400),
            new Path("Main Gate (Okponglo Entrance)", "University Hospital", 950),
            new Path("School of Performing Arts", "Legon Hall", 250),
            new Path("Cedi Conference Centre", "UGBS Main Campus", 350),
            new Path("Department of Psychology", "Balme Library", 600),
            new Path("International Programmes Office", "Jubilee Hall", 300),
            new Path("Akuafo Hall", "Commonwealth Hall", 750)
    };


    /**
     * Constructs the graph and immediately populates it from the static data arrays.
     */

    public static Graph schoolGraph = new Graph();
    

    private Graph() {
        buildGraph();
    }

    /**
     * Populates the graph's adjacency list from the static CAMPUS_NODES and CAMPUS_EDGES arrays.
     */
    public void buildGraph() {
        // Create a temporary map for quick lookup of nodes by their name string.
        Map<String, Node> nameToNodeMap = new HashMap<>();

        // First, add all nodes from the array to the adjacency list and the lookup map.
        for (Node node : CAMPUS_NODES) {
            addNode(node);
            nameToNodeMap.put(node.getName(), node);
        }

        // Next, create the connections from the path array using the lookup map.
        for (Path path : CAMPUS_EDGES) {
            Node fromNode = nameToNodeMap.get(path.src);
            Node toNode = nameToNodeMap.get(path.dst);

            // Add the edge only if both nodes exist to prevent errors.
            if (fromNode != null && toNode != null) {
                addEdge(fromNode, toNode, path.weight);
            } else {
                System.err.println("Warning: Could not create edge " + path.src + " -> " + path.dst + ". Node not found.");
            }
        }
    }

    /**
     * Adds a node to the adjacency list, preparing it to have edges.
     * @param node The node to add.
     */
    public void addNode(Node node) {
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * Adds a bidirectional edge between two nodes.
     * @param from   The source node.
     * @param to     The target node.
     * @param weight The distance or cost of the edge.
     */
    public void addEdge(Node from, Node to, double weight) {
        adjList.get(from).add(new Edge(to, weight));
        adjList.get(to).add(new Edge(from, weight)); // For an undirected graph
    }

    /**
     * Retrieves all edges connected to a given node.
     * @param node The node whose neighbors are to be retrieved.
     * @return A list of edges connected to the node.
     */
    public List<Edge> getNeighbors(Node node) {
        return adjList.getOrDefault(node, Collections.emptyList());
    }

    /**
     * Returns a set of all nodes in the graph.
     * @return A Set containing all nodes.
     */
    public Set<Node> getNodes() {
        return adjList.keySet();
    }
}