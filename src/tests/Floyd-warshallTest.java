* Main method demonstrating the UG Navigate system
     */
public static void main(String[] args) {
    // Define UG Campus locations
    String[] ugLocations = {
            "Main Gate", "Library", "Science Block", "Engineering Block",
            "Great Hall", "JQB", "Administration", "Sports Complex",
            "Medical Center", "Bus Terminal", "Computing Center", "Bank"
    };

    // Initialize UG Navigate system
    UGNavigateFloydWarshall ugNavigate = new UGNavigateFloydWarshall(ugLocations);

    // Add campus paths with distances (in meters)
    // Note: These are example distances - replace with actual campus measurements
    ugNavigate.addPath("Main Gate", "Administration", 200);
    ugNavigate.addPath("Main Gate", "Bus Terminal", 150);
    ugNavigate.addPath("Administration", "Library", 300);
    ugNavigate.addPath("Library", "Science Block", 250);
    ugNavigate.addPath("Science Block", "Engineering Block", 400);
    ugNavigate.addPath("Engineering Block", "Computing Center", 200);
    ugNavigate.addPath("Library", "JQB", 350);
    ugNavigate.addPath("JQB", "Great Hall", 300);
    ugNavigate.addPath("Great Hall", "Sports Complex", 500);
    ugNavigate.addPath("Administration", "Medical Center", 400);
    ugNavigate.addPath("Medical Center", "Bank", 150);
    ugNavigate.addPath("Bank", "Bus Terminal", 100);
    ugNavigate.addPath("Computing Center", "Bank", 300);

    // Calculate all shortest paths
    ugNavigate.calculateShortestPaths();

    // Demonstrate functionality
    System.out.println("\n=== UG Navigate Demonstration ===");

    // Find shortest distance and path
    String from = "Main Gate";
    String to = "Engineering Block";
    double distance = ugNavigate.getShortestDistance(from, to);
    List<String> path = ugNavigate.getShortestPath(from, to);

    System.out.printf("\nShortest distance from %s to %s: %.0f meters%n", from, to, distance);
    System.out.println("Route: " + String.join(" → ", path));

    // Find locations within walking distance
    System.out.println("\nLocations within 400m of Library:");
    Map<String, Double> nearbyLocations = ugNavigate.getLocationsWithinDistance("Library", 400);
    nearbyLocations.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(entry -> System.out.printf("  %s: %.0f meters%n", entry.getKey(), entry.getValue()));

    // Route via landmarks
    List<String> landmarks = Arrays.asList("Bank", "Library");
    List<String> landmarkRoute = ugNavigate.getRouteViaLandmarks("Main Gate", "Sports Complex", landmarks);
    System.out.println("\nRoute from Main Gate to Sports Complex via Bank and Library:");
    System.out.println("  " + String.join(" → ", landmarkRoute));

    // Print complete distance matrix
    ugNavigate.printDistanceMatrix();
}
}