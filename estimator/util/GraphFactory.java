package estimator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.awt.Point;

/**
 * DAGGenerator.java
 *
 * Utility class providing an interface for generating
 * acyclical directed graphs (DAGs).
 *
 * Translated and modified version of C solution provided
 * by Arjun Shankar – http://stackoverflow.com/questions/12790337/generating-a-random-dag
 *
 *
 */
public class GraphFactory {

  /** Nodes/Rank: How 'fat' the DAG should be. */
  private static final int MIN_PER_RANK = 1;
  private static final int MAX_PER_RANK = 20;

  /** Ranks: How 'tall' the DAG shuld be. */
  private static final int MIN_RANKS = 1;
  private static final int MAX_RANKS = 20;

  /** Chance of having an edge (dense graph). */
  private static final int DENSE_PERCENT = 80;

  /** Chance of having an edge (sparse graph). */
  private static final int SPARSE_PERCENT = 30;

  public static enum Density {
    DENSE, SPARSE;
  }

  /**
   * FOR TESTING PURPOSES ONLY!!!
   *
   */
  public static void main(String[] args) {
    Graph<Integer> graph = new Graph<>();
    List<Point> edges = generateDenseDAG();
    for (Point point : edges) {
      graph.addEdge(point.x, point.y);
    }
    System.out.println(graph);
    System.out.println(hasCycle(graph) ? "Test failed!" : "Test passed!");
  }

  /**
   * FOR TESTING PURPOSES ONLY!!!
   *
   */
  private static boolean hasCycle(Graph<Integer> graph) {
    Set<Integer> nodes = graph.getNodeValues();
    for (int node : nodes) {
      Node<Integer> graphNode = graph.getNode(node);
      graphNode.setVisited(true);
      if (hasCycle(graphNode, graphNode)) {
        return true;
      }
      graphNode.setVisited(false);
    }
    return false;
  }

  /**
   * FOR TESTING PURPOSES ONLY!!!
   *
   */
  private static boolean hasCycle(Node<Integer> start,
      Node<Integer> graphNode) {
    List<Node<Integer>> adjacents = start.getAdjacents();
    for (Node<Integer> adj : adjacents) {
      if (!adj.getVisited()) {
        if (adj == start) {
          return true;
        }
        adj.setVisited(true);
        hasCycle(adj, graphNode);
        adj.setVisited(false);
      }
    }
    return false;
  }
 
  /** Constructs a random Dense DAG */
  public static Graph<Integer> constructDenseDAG() {
    Graph<Integer> graph = new Graph<>();
    List<Point> edges = generateDenseDAG();
    for (Point edge : edges) {
      graph.addEdge(edge.x, edge.y);
    }
    return graph;
  }

  /**
   * Generates a list of edges-represented by {@link Point}
   * objects–that represent a DAG.
   *
   * @return a list of points that represent the edges in a
   *     Directed Acyclic Graph
   *
   */
  public static List<Point> generateDenseDAG() {
    int nodes = 0;
    Random generator = new Random();

    // Calculates the possible ranks of nodes in the
    // graph (number of nodes in the graph.)
    int ranks = MIN_RANKS
        + (generator.nextInt() % (MAX_RANKS - MIN_RANKS + 1));

    ArrayList<Point> edges = new ArrayList<>();

    for (int i = 0; i < ranks; i++) {
      // New nodes of 'higher' rank than all nodes generated till now.
      int newNodes = MIN_PER_RANK
          + (generator.nextInt() % (MAX_PER_RANK - MIN_PER_RANK + 1));

      // Edges from old nodes ('nodes') to new ones ('newNodes').
      for (int j = 0; j < nodes; j++) {
        for (int k = 0; k < newNodes; k++) {
          if ((generator.nextInt() % 100) < DENSE_PERCENT) {
            int x = j;
            int y = k + nodes;
            edges.add(new Point(x, y));
          }
        }
      }
      nodes += newNodes;
    }
    return edges.isEmpty() ? generateDenseDAG() : edges;
  }

  public static List<Point> generateDag(int size, double density) {
      int maxEdges = (int) ((0.5 * density * (double) (size * (size - 1))) + Math.random());
      List<Point> preList = new ArrayList<>();
      for (int i = 1; i <= size; i++) {
	  for (int j = i + 1; j <= size; j++) {
	      preList.add(new Point(i, j));
	  }
      }

    Set<Point> finalList = new HashSet<>();
    for (int i = 0; i < preList.size() /*<= maxEdges*/; i++) {
        i = i % preList.size();
	if (Math.random() < density) {
	  finalList.add(preList.get(i));
	}
    }

    return new ArrayList<>(finalList);
  }

  public static List<Point> generateGraph(int size, double density) {
    int maxEdges = (int) ((0.5 * density * (double) (size * (size - 1)))
        + Math.random());
    List<Point> preList = new ArrayList<>();
    for (int i = 1; i <= size; i++) {
      for (int j = 1; j <= size; j++) {
        preList.add(new Point(i, j));
      }
    }

    Set<Point> finalList = new HashSet<>();
    for (int i = 0; finalList.size() <= maxEdges; i++) {
	if (Math.random() < density) {
	  finalList.add(preList.remove(i));
	}
    }

    return new ArrayList<>(finalList);
  }
}
