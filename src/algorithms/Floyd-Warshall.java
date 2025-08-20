import java.util.*;

/**
 * Floyd-Warshall Algorithm Implementation for UG Navigate
 * Calculates shortest paths between all pairs of campus locations
 */
public class UGNavigateFloydWarshall {

    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private int numLocations;
    private double[][] distanceMatrix;
    private int[][] nextMatrix;
    private Map<String, Integer> locationIndex;
    private String[] locations;

    /**
     * Constructor to initialize the campus navigation system
     * @param campusLocations Array of campus location names
     */
    public UGNavigateFloydWarshall(String[] campusLocations) {
        this.numLocations = campusLocations.length;
        this.locations = campusLocations.clone();
        this.distanceMatrix = new double[numLocations][numLocations];
        this.nextMatrix = new int[numLocations][numLocations];
        this.locationIndex = new HashMap<>();

        // Map location names to indices
        for (int i = 0; i < numLocations; i++) {
            locationIndex.put(campusLocations[i], i);
        }

        initializeMatrices();
    }

    /**
     * Initialize distance and next matrices
     */
    private void initializeMatrices() {
        for (int i = 0; i < numLocations; i++) {
            for (int j = 0; j < numLocations; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    distanceMatrix[i][j] = INFINITY;
                }
                nextMatrix[i][j] = -1;
            }
        }
    }

    /**
     * Add a direct path between two campus locations
     * @param from Source location name
     * @param to Destination location name
     * @param distance Distance between locations in meters
     */
    public void addPath(String from, String to, double distance) {
        Integer fromIndex = locationIndex.get(from);
        Integer toIndex = locationIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            throw new IllegalArgumentException("Invalid location name");
        }

        // Add bidirectional path (assuming campus paths are bidirectional)
        distanceMatrix[fromIndex][toIndex] = distance;
        distanceMatrix[toIndex][fromIndex] = distance;

        nextMatrix[fromIndex][toIndex] = toIndex;
        nextMatrix[toIndex][fromIndex] = fromIndex;
    }

    /**
     * Execute Floyd-Warshall algorithm to find all shortest paths
     */
    public void calculateShortestPaths() {
        System.out.println("Calculating shortest paths for UG Campus...");

        // Main Floyd-Warshall algorithm
        for (int k = 0; k < numLocations; k++) {
            for (int i = 0; i < numLocations; i++) {
                for (int j = 0; j < numLocations; j++) {
                    // Check if path through k is shorter
                    if (distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                        nextMatrix[i][j] = nextMatrix[i][k];
                    }
                }
            }

            // Progress indicator
            System.out.printf("Progress: %.1f%% complete%n",
                    ((double)(k + 1) / numLocations) * 100);
        }

        System.out.println("Shortest paths calculation completed!");
    }

    /**
     * Get shortest distance between two locations
     * @param from Source location
     * @param to Destination location
     * @return Shortest distance in meters
     */
    public double getShortestDistance(String from, String to) {
        Integer fromIndex = locationIndex.get(from);
        Integer toIndex = locationIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            throw new IllegalArgumentException("Invalid location name");
        }

        double distance = distanceMatrix[fromIndex][toIndex];
        return distance == INFINITY ? -1 : distance;
    }

    /**
     * Get the complete shortest path between two locations
     * @param from Source location
     * @param to Destination location
     * @return List of locations representing the shortest path
     */
    public List<String> getShortestPath(String from, String to) {
        Integer fromIndex = locationIndex.get(from);
        Integer toIndex = locationIndex.get(to);

        if (fromIndex == null || toIndex == null) {
            throw new IllegalArgumentException("Invalid location name");
        }

        List<String> path = new ArrayList<>();

        if (distanceMatrix[fromIndex][toIndex] == INFINITY) {
            return path; // No path exists
        }

        // Reconstruct path using next matrix
        reconstructPath(fromIndex, toIndex, path);

        return path;
    }

    /**
     * Recursive method to reconstruct the shortest path
     * @param from Source index
     * @param to Destination index
     * @param path List to store the path
     */
    private void reconstructPath(int from, int to, List<String> path) {
        if (from == to) {
            path.add(locations[from]);
            return;
        }

        if (nextMatrix[from][to] == -1) {
            return; // No path exists
        }

        path.add(locations[from]);
        reconstructPath(nextMatrix[from][to], to, path);
    }

    /**
     * Find all locations within a certain distance from a source location
     * @param sourceLocation Source location name
     * @param maxDistance Maximum distance in meters
     * @return Map of reachable locations and their distances
     */
    public Map<String, Double> getLocationsWithinDistance(String sourceLocation, double maxDistance) {
        Integer sourceIndex = locationIndex.get(sourceLocation);

        if (sourceIndex == null) {
            throw new IllegalArgumentException("Invalid location name");
        }

        Map<String, Double> reachableLocations = new HashMap<>();

        for (int i = 0; i < numLocations; i++) {
            if (i != sourceIndex && distanceMatrix[sourceIndex][i] <= maxDistance) {
                reachableLocations.put(locations[i], distanceMatrix[sourceIndex][i]);
            }
        }

        return reachableLocations;
    }

    /**
     * Find routes passing through specific landmarks
     * @param from Source location
     * @param to Destination location
     * @param landmarks List of landmarks to pass through
     * @return Optimized route considering landmarks
     */
    public List<String> getRouteViaLandmarks(String from, String to, List<String> landmarks) {
        List<String> completeRoute = new ArrayList<>();
        String currentLocation = from;

        // Add source to route
        completeRoute.add(currentLocation);

        // Visit each landmark in optimal order
        List<String> remainingLandmarks = new ArrayList<>(landmarks);

        while (!remainingLandmarks.isEmpty()) {
            String nearestLandmark = findNearestLandmark(currentLocation, remainingLandmarks);
            List<String> pathToLandmark = getShortestPath(currentLocation, nearestLandmark);

            // Add path to landmark (excluding current location to avoid duplication)
            for (int i = 1; i < pathToLandmark.size(); i++) {
                completeRoute.add(pathToLandmark.get(i));
            }

            currentLocation = nearestLandmark;
            remainingLandmarks.remove(nearestLandmark);
        }

        // Add path from last landmark to destination
        List<String> finalPath = getShortestPath(currentLocation, to);
        for (int i = 1; i < finalPath.size(); i++) {
            completeRoute.add(finalPath.get(i));
        }

        return completeRoute;
    }

    /**
     * Find the nearest landmark from current location
     * @param currentLocation Current location
     * @param landmarks Available landmarks
     * @return Nearest landmark name
     */
    private String findNearestLandmark(String currentLocation, List<String> landmarks) {
        String nearest = landmarks.get(0);
        double minDistance = getShortestDistance(currentLocation, nearest);

        for (String landmark : landmarks) {
            double distance = getShortestDistance(currentLocation, landmark);
            if (distance < minDistance && distance != -1) {
                minDistance = distance;
                nearest = landmark;
            }
        }

        return nearest;
    }

    /**
     * Display the complete distance matrix
     */
    public void printDistanceMatrix() {
        System.out.println("\n=== UG Campus Distance Matrix ===");
        System.out.printf("%15s", "");

        // Print header
        for (String location : locations) {
            System.out.printf("%15s", location.substring(0, Math.min(location.length(), 12)));
        }
        System.out.println();

        // Print matrix rows
        for (int i = 0; i < numLocations; i++) {
            System.out.printf("%15s", locations[i].substring(0, Math.min(locations[i].length(), 12)));
            for (int j = 0; j < numLocations; j++) {
                if (distanceMatrix[i][j] == INFINITY) {
                    System.out.printf("%15s", "∞");
                } else {
                    System.out.printf("%15.0f", distanceMatrix[i][j]);
                }
            }
            System.out.println();
        }
    }