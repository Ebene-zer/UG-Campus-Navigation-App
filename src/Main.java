
import algorithms.Dijkstra;
import algorithms.Graph;
import algorithms.Node;
import java.util.List;

    /*
    This file will be modified (for integration) as and when we progress
     */
    public class Main {
        public static void main(String[] args) {
            System.out.println("UG Navigate project – run UI or tests to see results.");
            Graph graph = Graph.schoolGraph;
            
            Dijkstra dijkstra = new Dijkstra(graph);

            Node start = new Node("null", 0, 0);
            Node goal = new Node("null", 0, 0);

            List<Node> paths = dijkstra.shortestDistance(start, goal);

        }
    }

