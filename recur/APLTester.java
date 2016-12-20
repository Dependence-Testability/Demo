import util.Graph;
import util.Node;
import util.TarjanSCC;
import util.APL;
import java.util.Scanner;
import java.io.FileReader;

public class APLTester {

  public static void main(String[] args) {
    Graph<Integer> graph = new Graph<>();
    // int[][] edges
        // = {{0, 1}, {1, 2}, {2, 3}, {3, 2},
        //    {3, 7}, {7, 3}, {2, 6}, {7, 6},
        //    {5, 6}, {6, 5}, {1, 5}, {4, 5},
        //    {4, 0}, {1, 4}};
        // 3 Strongly Connected Components
        // 4 paths from 0 to 6
        // Avg path length is 3.75

        // = {{1, 2}, {2, 3}, {3, 1}, {4, 2},
        //    {4, 3}, {5, 4}, {4, 5}, {6, 3},
        //    {6, 7}, {7, 6}, {8, 7}, {8, 5},
        //    {8, 8}, {5, 6}};  
        // 4 Strongly Connected Components
        // 3 paths from 4 to 1
        // Avg path length is 3

        // = {{1, 2}, {1, 3}, {1, 4}, {1, 5},
        //    {2, 1}, {2, 3}, {2, 4}, {2, 5},
        //    {3, 1}, {3, 2}, {3, 4}, {3, 5},
        //    {4, 1}, {4, 2}, {4, 3}, {4, 5},
        //    {5, 1}, {5, 2}, {5, 3}, {5, 4}};  
        // 1 Strongly Connected Component
        // 16 paths from 1 to 5
        // Avg path length is 3

        // = {{1, 2}, {1, 3}, {1, 4}, {2, 3},
        //    {2, 4}, {2, 5}, {3, 2}, {3, 4}, 
        //    {3, 5}, {4, 2}, {4, 3}, {4, 5}};
        // 3 Strongly Connected Components
        // 15 paths from 1 to 5
        // Avg path length is 3.2

        // = {{1, 2}, {1, 3}, {1, 4}, {1, 5},
        //    {2, 3}, {2, 4}, {2, 5}, {3, 4},
        //    {3, 5}, {4, 5}};    
        // 5 Strongly Connected Components
        // 8 paths from 1 to 5
        // Avg path length is 2.5

    

    // for (int[] edge : edges) {
    //   graph.addNewEdge(edge[0], edge[1]);
    // }

    // analyze("random_dag.txt", 1, 5);
    analyze("random_graph.txt", 11, 12);

    // TarjanSCC<Integer> scc = new TarjanSCC<Integer>(graph);
    // System.out.println(scc.count() + " Strongly Connected Components");
    // System.out.println(scc);

  }

  public static void analyze(String filename, int start, int end) {
    Graph<Integer> graph = new Graph<>();
    try {
        Scanner in = new Scanner(new FileReader(filename));
        while (in.hasNext()) {
            graph.addNewEdge(in.nextInt(), in.nextInt());
        }
    } catch (Exception e) {
        System.out.println("File not found");
    }

    double[] result = APL.compute(graph, start, end, 2, 10);
    System.out.println("Number of paths is " + result[0]);
    System.out.println("Average length of paths is " + result[1]);

  }
}
