import java.util.*;
import java.util.stream.Collectors;

public class Edge {
    private String source;
    private String target;
    private double distance; // in meters
    private String pathType;
    private int timeMinutes;

    public Edge(String source, String target, double distance, String pathType, int timeMinutes) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.pathType = pathType;
        this.timeMinutes = timeMinutes;
    }

    // Getters
    public String getSource() { return source; }
    public String getTarget() { return target; }
    public double getDistance() { return distance; }
    public String getPathType() { return pathType; }
    public int getTimeMinutes() { return timeMinutes; }

    @Override
    public String toString() {
        return String.format("%s → %s (%.0fm, %s, %dmin)", 
                source, target, distance, pathType, timeMinutes);
    }

    public static List<Edge> loadAllEdges() {
        List<Edge> edges = new ArrayList<>();

        // =====================
        // 1. GATE CONNECTIONS (12)
        // =====================
        edges.add(new Edge("gate1", "admin1", 320, "paved_walkway", 4)); // Main Gate → Great Hall
        edges.add(new Edge("gate1", "bus1", 25, "paved_walkway", 1));    // → Main Bus Stop
        edges.add(new Edge("gate1", "gate2", 450, "road", 6));           // → Okponglo Gate
        edges.add(new Edge("gate1", "food4", 200, "paved_walkway", 3));  // → Banking Square
        edges.add(new Edge("gate2", "school2", 350, "road", 5));         // Okponglo → Business School
        edges.add(new Edge("gate2", "hostel1", 400, "road", 6));         // → Pentagon Hostel
        edges.add(new Edge("gate3", "health2", 150, "paved_walkway", 2));// Atomic → UGMC
        edges.add(new Edge("gate3", "rec1", 600, "footpath", 9));        // → Legon Gardens
        edges.add(new Edge("gate4", "health2", 50, "paved_walkway", 1)); // UGMC Gate → UGMC
        edges.add(new Edge("gate4", "lecture5", 300, "paved_walkway", 4));// → Physics Dept
        edges.add(new Edge("gate4", "school5", 350, "paved_walkway", 5));// → Biological Sciences
        edges.add(new Edge("gate4", "dias4", 500, "road", 7));           // → Elizabeth Sey Hall

        // =====================
        // 2. ACADEMIC CORE (18)
        // =====================
        // Great Hall connections
        edges.add(new Edge("admin1", "admin2", 80, "paved_walkway", 1));  // → Registry
        edges.add(new Edge("admin1", "lib1", 180, "paved_walkway", 2));   // → Balme Library
        edges.add(new Edge("admin1", "lecture7", 250, "paved_walkway", 3));// → Amegashie Auditorium
        
        // Library connections
        edges.add(new Edge("lib1", "lib2", 120, "paved_walkway", 2));     // Balme → Law Library
        edges.add(new Edge("lib1", "lib3", 350, "paved_walkway", 5));     // → Science Library
        edges.add(new Edge("lib1", "lecture1", 200, "paved_walkway", 3)); // → N Block
        edges.add(new Edge("lib1", "health3", 90, "paved_walkway", 1));   // → Pharmacy
        
        // Lecture building links
        edges.add(new Edge("lecture1", "lecture2", 150, "paved_walkway", 2)); // N Block → JQB
        edges.add(new Edge("lecture2", "lecture3", 100, "paved_walkway", 1));  // JQB → Math Dept
        edges.add(new Edge("lecture2", "bus4", 50, "paved_walkway", 1));       // → JQB Bus Stop
        edges.add(new Edge("lecture3", "lecture4", 120, "paved_walkway", 2));  // Math → Chemistry
        edges.add(new Edge("lecture4", "lecture5", 80, "paved_walkway", 1));   // Chemistry → Physics
        edges.add(new Edge("lecture5", "school4", 150, "paved_walkway", 2));   // Physics → Physical Sciences
        edges.add(new Edge("lecture6", "school6", 200, "paved_walkway", 3));    // Archaeology → Arts
        edges.add(new Edge("lecture7", "lecture8", 180, "paved_walkway", 2));   // Amegashie → Busia Hall
        edges.add(new Edge("lecture8", "school8", 220, "paved_walkway", 3));    // Busia → Education
        edges.add(new Edge("lecture2", "school1", 300, "paved_walkway", 4));    // JQB → Law School

        // =====================
        // 3. HALLS & HOSTELS (22)
        // =====================
        // Traditional Halls
        edges.add(new Edge("hall1", "hall2", 350, "paved_walkway", 5));  // Legon → Akuafo
        edges.add(new Edge("hall1", "food1", 400, "paved_walkway", 6));  // → Central Cafeteria
        edges.add(new Edge("hall2", "rec2", 250, "footpath", 4));        // Akuafo → Comm Garden
        edges.add(new Edge("hall3", "food2", 200, "footpath", 3));       // Commonwealth → Night Market
        edges.add(new Edge("hall3", "bus2", 180, "paved_walkway", 2));   // → Night Market Bus
        edges.add(new Edge("hall4", "school7", 300, "paved_walkway", 4));// Volta → Languages
        edges.add(new Edge("hall5", "food3", 180, "footpath", 2));       // Sarbah → Bush Canteen
        
        // Diaspora Halls
        edges.add(new Edge("dias1", "dias2", 150, "paved_walkway", 2));  // Kwapong → Limann
        edges.add(new Edge("dias2", "dias3", 120, "paved_walkway", 2));   // Limann → Jean Nelson
        edges.add(new Edge("dias3", "dias4", 100, "paved_walkway", 1));   // Jean Nelson → Elizabeth Sey
        edges.add(new Edge("dias4", "hostel1", 250, "paved_walkway", 4)); // → Pentagon Hostel
        
        // Hostels
        edges.add(new Edge("hostel1", "hostel2", 200, "paved_walkway", 3)); // Pentagon → Evandy
        edges.add(new Edge("hostel2", "hostel3", 180, "paved_walkway", 3)); // Evandy → TF
        edges.add(new Edge("hostel3", "hostel4", 150, "paved_walkway", 2)); // TF → Bani
        edges.add(new Edge("hostel4", "hostel5", 300, "paved_walkway", 5)); // Bani → ISH1
        edges.add(new Edge("hostel5", "hostel6", 250, "paved_walkway", 4)); // ISH1 → ISH2
        edges.add(new Edge("hostel1", "school2", 400, "road", 6));          // Pentagon → Business School
        edges.add(new Edge("hostel3", "food4", 350, "paved_walkway", 5));   // TF → Banking Square
        edges.add(new Edge("hostel6", "school10", 200, "paved_walkway", 3));// ISH2 → Social Sciences

        // =====================
        // 4. SCHOOL CONNECTIONS (15)
        // =====================
        edges.add(new Edge("school1", "school10", 250, "paved_walkway", 3)); // Law → Social Sciences
        edges.add(new Edge("school2", "lib4", 100, "paved_walkway", 1));     // Business → UGBS Library
        edges.add(new Edge("school2", "bus5", 40, "paved_walkway", 1));      // → UGBS Bus Stop
        edges.add(new Edge("school3", "lecture5", 180, "paved_walkway", 2)); // Engineering → Physics
        edges.add(new Edge("school4", "school5", 150, "paved_walkway", 2));  // Physical → Biological Sci
        edges.add(new Edge("school5", "health1", 400, "paved_walkway", 6));  // Biological → Legon Hospital
        edges.add(new Edge("school6", "school7", 120, "paved_walkway", 2));  // Arts → Languages
        edges.add(new Edge("school7", "school8", 200, "paved_walkway", 3));  // Languages → Education
        edges.add(new Edge("school8", "school9", 180, "paved_walkway", 3));  // Education → Comm Studies
        edges.add(new Edge("school9", "school10", 220, "paved_walkway", 3)); // Comm Studies → Social Sci
        edges.add(new Edge("school3", "school4", 300, "paved_walkway", 4));  // Engineering → Physical Sci
        edges.add(new Edge("school6", "lecture6", 150, "paved_walkway", 2)); // Arts → Archaeology
        edges.add(new Edge("school9", "lib3", 250, "paved_walkway", 4));     // Comm Studies → Science Lib
        edges.add(new Edge("school10", "dias1", 350, "paved_walkway", 5));   // Social Sci → Kwapong Hall
        edges.add(new Edge("school4", "lecture4", 200, "paved_walkway", 3)); // Physical Sci → Chemistry

        // =====================
        // 5. HEALTH & SERVICES (8)
        // =====================
        edges.add(new Edge("health1", "health2", 600, "road", 8));       // Legon Hosp → UGMC
        edges.add(new Edge("health1", "gate4", 400, "paved_walkway", 5));// → UGMC Gate
        edges.add(new Edge("health1", "health3", 120, "paved_walkway", 2));// → Pharmacy
        edges.add(new Edge("health2", "gate3", 150, "paved_walkway", 2)); // UGMC → Atomic Gate
        edges.add(new Edge("health3", "lib1", 90, "paved_walkway", 1));   // Pharmacy → Balme
        edges.add(new Edge("health3", "food1", 180, "paved_walkway", 3)); // → Central Cafeteria
        edges.add(new Edge("health1", "school5", 400, "paved_walkway", 6));// → Biological Sciences
        edges.add(new Edge("health2", "school3", 500, "road", 7));        // UGMC → Engineering

        // =====================
        // 6. FOOD & TRANSPORT (10)
        // =====================
        edges.add(new Edge("food1", "food2", 150, "paved_walkway", 2));  // Central Caf → Night Market
        edges.add(new Edge("food2", "bus2", 30, "paved_walkway", 1));    // Night Market → Bus Stop
        edges.add(new Edge("food3", "hall5", 180, "footpath", 2));       // Bush Canteen → Sarbah
        edges.add(new Edge("food4", "gate1", 200, "paved_walkway", 3));  // Banking Square → Main Gate
        edges.add(new Edge("bus1", "admin1", 300, "paved_walkway", 4));  // Main Bus → Great Hall
        edges.add(new Edge("bus3", "food1", 50, "paved_walkway", 1));    // Cafeteria Bus → Central Caf
        edges.add(new Edge("bus4", "lecture2", 50, "paved_walkway", 1)); // JQB Bus → JQB
        edges.add(new Edge("bus5", "school2", 40, "paved_walkway", 1));  // UGBS Bus → Business School
        edges.add(new Edge("food1", "admin5", 100, "paved_walkway", 2)); // Central Caf → Post Office
        edges.add(new Edge("food3", "hostel3", 250, "footpath", 4));     // Bush Canteen → TF Hostel

        // =====================
        // 7. RECREATION (2)
        // =====================
        edges.add(new Edge("rec1", "rec2", 700, "footpath", 10));       // Legon Gardens → Comm Garden
        edges.add(new Edge("rec1", "gate3", 600, "footpath", 9));        // → Atomic Gate

        return edges;
    }

    // Utility methods for data analysis
    public static Map<String, List<Edge>> getEdgesByPathType() {
        Map<String, List<Edge>> pathTypeMap = new HashMap<>();
        for (Edge edge : loadAllEdges()) {
            pathTypeMap.computeIfAbsent(edge.getPathType(), k -> new ArrayList<>()).add(edge);
        }
        return pathTypeMap;
    }

    public static List<Edge> findEdgesBySource(String sourceId) {
        return loadAllEdges().stream()
                .filter(edge -> edge.getSource().equals(sourceId))
                .collect(Collectors.toList());
    }

    public static List<Edge> findEdgesByTarget(String targetId) {
        return loadAllEdges().stream()
                .filter(edge -> edge.getTarget().equals(targetId))
                .collect(Collectors.toList());
    }

    public static double getTotalNetworkDistance() {
        return loadAllEdges().stream()
                .mapToDouble(Edge::getDistance)
                .sum();
    }

    public static int getTotalEdgeCount() {
        return loadAllEdges().size();
    }
}