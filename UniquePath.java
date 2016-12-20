import static estimator.util.GraphFactory.constructDenseDAG;
import static estimator.util.GraphFactory.generateDenseDAG;
import static estimator.util.GraphFactory.generateGraph;
import static estimator.util.GraphFactory.Density;
import static estimator.general.Algorithm1.naivePathGeneration;
import static estimator.general.Algorithm2.lengthDistribution;
import static estimator.general.PathFinder.dagTraversal;
import static estimator.general.PathFinder.uniquePaths;
import static estimator.general.PathFinder.uniquePathsAverageLength;

import java.awt.Point;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import estimator.util.Graph;
import estimator.util.Node;

public class UniquePath {

  private static int[][] edges = {{1, 2}, {1, 3}, {1, 4}, {1, 5},
                                 {2, 1}, {2, 3}, {2, 4}, {2, 5},
                                 {3, 1}, {3, 2}, {3, 4}, {3, 5},
                                 {4, 1}, {4, 2}, {4, 3}, {4, 5},
                                 {5, 1}, {5, 2}, {5, 3}, {5, 4}};
  private static int[][] dedges = {{1, 2}, {1, 3}, {1, 4}, {1, 5},
                                          {2, 3}, {2, 4}, {2, 5},
                                                  {3, 4}, {3, 5},
				                          {4, 5}};

  public static void main(String[] args) {
    // System.out.println("***** UNIQUE PATHS *****");
    // testUniquePaths();
    // System.out.println("***** UNIQUE PATHS -- END *****\n\n");
    // System.out.println("***** DAG UNIQUE PATHS *****");
    // testDAGUniquePaths();
    // System.out.println("***** DAG UNIQUE PATHS -- END *****\n\n");
    // System.out.println("***** NAIVE PATH GENERATION *****");
    // testNaivePathGeneration();
    // System.out.println("***** NAIVE PATH GENERATION -- END *****\n\n");
    // System.out.println("***** SCC PATH GENERATION *****");
    // testSCCAlgorithm();
    // System.out.println("***** SCC PATH GENERATION -- END *****\n\n");
    // writeGraphs();
    System.out.println("***** LENGTH DISTRIBUTION *****");
    testLengthDist();
    System.out.println("***** LENGTH DISTRIBUTION -- END *****");
  }

  private static void writeGraphs(List<Point> graph, List<Point> dag) {
    // Generates a random graph and write it to file.
    int n = 50;
    double d = .8;
    File file = new File("random_graph.txt");
    FileWriter fw = null;
    BufferedWriter bw = null;
    if (file.exists()) {
      file.delete();
    }
    try {
      fw = new FileWriter(file.getName());
      bw = new BufferedWriter(fw);
      for (Point p : graph) {
	String line = p.x + " " + p.y + "\n";
        bw.write(line);
      }
      bw.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error on graph write!");    
    } finally {
      try {
        if (bw != null) {
          bw.close();
	}
        if (fw != null) {
          fw.close();
	}
      } catch (IOException e) {
	e.printStackTrace();
      }
    }

    // Generates a random DAG and writes it to file.
    file = new File("random_dag.txt");
    if (file.exists()) {
      file.delete();
    }
    try {
      fw = new FileWriter(file.getName());
      bw = new BufferedWriter(fw);
      for (Point p : dag) {
	String line = p.x + " " + p.y + "\n";
        bw.write(line);
      }
      bw.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error on graph write!");    
    } finally {
      try {
        if (bw != null) {
          bw.close();
	}
        if (fw != null) {
          fw.close();
	}
      } catch (IOException e) {
	e.printStackTrace();
      }
    }
  }

  private static void testUniquePaths() {
    Graph<Integer> graph = new Graph<Integer>();
    
    for (int[] edge : edges) {
      graph.addEdge(edge[0], edge[1]);
    }

    int count = uniquePaths(graph, 1, 5);
    System.out.printf("The number of unique paths from vertex 1 to 5: %d\n", count);

    int average = uniquePathsAverageLength(graph, 1, 5);
    System.out.printf("The average length of unique paths from node 1 to 5 is: %d \n\n", average);
  }

  private static void testDAGUniquePaths() {
    int[][] dagEdges
        = {{1, 2}, {1, 3}, {1, 4}, {1, 5},
           {2, 3}, {2, 4}, {2, 5}, {3, 4},
           {3, 5}, {4, 5}};
    Graph<Integer> dag = new Graph<>();
    for (int[] edge : dagEdges) {
      dag.addEdge(edge[0], edge[1]);
    }

    int[] dagCounts = dagTraversal(dag, 1, 5);
    System.out.printf("The number of paths between nodes 1 and 5 is: %d\n", 
        dagCounts[0]);
    System.out.printf("The average length of the paths between nodes 1 and 5 is: %d\n\n", dagCounts[1]);

    Random generator = new Random();
    Graph<Integer> randomDag = constructDenseDAG();
    int start = getValidStart(randomDag);
    int end = getValidEnd(randomDag);
    int[] randDagCounts = dagTraversal(randomDag, start, end);
    System.out.println(randomDag);
    System.out.printf("The number of unique paths from node %d to %d in this DAG is: %d \n", start, end, randDagCounts[0]);
    System.out.printf("The average length of the number of unique paths from node %d to %d in this DAG is: %d \n", start, end, randDagCounts[1]);
  }

  private static void testNaivePathGeneration() {
    // Testing for static DAG
    Graph<Integer> naivePathDag = new Graph<>();
    for (int[] edge : dedges) {
      naivePathDag.addEdge(edge[0], edge[1]);
    }

    naivePathGeneration(naivePathDag, 1, 5);
    for (int value : naivePathDag.getNodeValues()) {
      Node<Integer> node = naivePathDag.getNode(value);
      node.printDetail();
    }
  }

  /** Testing for algorithm 2 (length distribution). */
  private static void testLengthDist() {
    int nPrime = 200;

    //Simple Graph
    List<Graph<Integer>> graphs = new ArrayList<>();
    List<Graph<Integer>> dags = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
      Graph<Integer> graph = new Graph<>();
      Graph<Integer> dag = new Graph<>();
      for (int[] edge : edges) {
        graph.addEdge(edge[0], edge[1]);
      }
      for (int[] edge : dedges) {
        dag.addEdge(edge[0], edge[1]);
      }
      graphs.add(graph);
      dags.add(dag);
    }

    Graph<Integer> graph = new Graph<>();
    Graph<Integer> dag = new Graph<>();
    for (int[] edge : edges) {
      graph.addEdge(edge[0], edge[1]);
    }
    for (int[] edge : dedges) {
      dag.addEdge(edge[0], edge[1]);
    }

    System.out.println("SIMPLE Graph");
    double[] tuple = lengthDistribution(graphs, graph, 1, 5);
    System.out.printf("The estimated number of unique paths from vertex %d to " + "%d: %f\n", 1, 5, tuple[0]);
    System.out.printf("The estimated average length of the paths from %d to %d is %f\n", 1, 5, tuple[1]);
    System.out.printf("The number of unique paths from vertex %d to %d: %d\n", 1, 5, uniquePaths(graph, 1, 5));
    System.out.printf("The average length of the unique paths from vertex %d to %d: %d\n", 1, 5, uniquePathsAverageLength(graph, 1, 5));


    System.out.println("Simple DAG");
    tuple = lengthDistribution(dags, dag, 1, 5);
    System.out.printf("The estimated number of unique paths from vertex %d to " + "%d: %f\n", 1, 5, tuple[0]);
    System.out.printf("The estimated average length of the unique paths from vertex %d to %d: %f\n", 1, 5, tuple[1]);
    System.out.printf("The number of unique paths from vertex %d to %d: %d\n", 1, 5, uniquePaths(dag, 1, 5));
    System.out.printf("The average length of the unique paths from vertex %d to %d: %d\n", 1, 5, uniquePathsAverageLength(dag, 1, 5));

    // Dense Graph
    int n = 15;
    double d = .8;
    List<Point> graphPts = generateGraph(n, d);
    List<Point> dagPts = generateDenseDAG();
    /* List<Point> list = Arrays.asList(
      new Point(1, 2),
      new Point(1, 3),
      new Point(1, 4),
      new Point(1, 6),
      new Point(1, 7),
      new Point(2, 1),
      new Point(2, 5),
      new Point(2, 6),
      new Point(2, 7),
      new Point(3, 1),
      new Point(3, 4),
      new Point(3, 5),
      new Point(3, 6),
      new Point(3, 7),
      new Point(3, 8),
      new Point(4, 1),
      new Point(4, 3),
      new Point(4, 5),
      new Point(4, 6),
      new Point(4, 7),
      new Point(4, 8),
      new Point(5, 2),
      new Point(5, 3),
      new Point(5, 4),
      new Point(5, 6),
      new Point(6, 1),
      new Point(6, 2),
      new Point(6, 3),
      new Point(6, 4),
      new Point(6, 5),
      new Point(6, 7),
      new Point(6, 8),
      new Point(7, 1),
      new Point(7, 2),
      new Point(7, 3),
      new Point(7, 4),
      new Point(7, 6),
      new Point(7, 8),
      new Point(8, 1),
      new Point(8, 3),
      new Point(8, 4),
      new Point(8, 6),
      new Point(8, 7)
      ); */

    graphs = new ArrayList<>();
    dags = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
      graph = new Graph<>();
      dag = new Graph<>();
      for (Point p : graphPts) {
	  graph.addEdge(p.x, p.y);
      }
      for (Point p : dagPts) {
	  dag.addEdge(p.x, p.y);
      }
      graphs.add(graph);
      dags.add(dag);
    }

    graph = new Graph<>();
    dag = new Graph<>();
    for (Point p : graphPts) {
      graph.addEdge(p.x, p.y);
    }
    for (Point p : graphPts) {
      dag.addEdge(p.x, p.y);
    }

    System.out.println("Dag size: " + dag.size());

    writeGraphs(graphPts, dagPts);

    System.out.println("Dense Graph");
    int start = 1;
    int end = n;
    double[] est = lengthDistribution(graphs, graph, start, end);
    int count = uniquePaths(graph, start, end);
    System.out.printf("The estimated number of unique paths from vertex %d to %d: %f\n", start, end, est[0]);
    System.out.printf("The estimated average length of unique paths from vertex %d to %d: %f\n", start, end, est[1]);
    System.out.printf("The number of unique paths from vertex %d to %d: %d\n", start, end, count);
    System.out.printf("The average length of the unique paths from vertex %d to %d: %d\n", start, end, uniquePathsAverageLength(graph, start, end));

    System.out.println("Dense DAG");
    start = getValidStart(dag);
    end = getValidEnd(dag);
    est = lengthDistribution(dags, dag, start, end);
    count = uniquePaths(dag, start, end);
    System.out.printf("The estimated number of unique paths from vertex %d to %d: %f\n", start, end, est[0]);
    System.out.printf("The estimated average length of unique paths from vertex %d to %d: %f\n", start, end, est[1]);
    System.out.printf("The number of unique paths from vertex %d to %d: %d\n", start, end, count);
    System.out.printf("The average length of the unique paths from vertex %d to %d: %d\n", start, end, uniquePathsAverageLength(dag, start, end));
  }

  private static int getValidStart(Graph<Integer> graph) {
    Random generator = new Random();
    int start = -1;
    while (graph.getNode(start) == null) {
      start = generator.nextInt(graph.size()/2);
    }
    return start;
  }

  private static int getValidEnd(Graph<Integer> graph) {
    Random generator = new Random();
    int end = -1;
    while (graph.getNode(end) == null) {
      end = generator.nextInt(graph.size()/2) + graph.size()/2;
    }
    return end;
  }
}
