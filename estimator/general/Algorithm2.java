package estimator.general;

import estimator.util.Graph;
import estimator.util.Node;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Random;
import java.io.File;
import java.io.IOException;

public class Algorithm2 {

  private static final int N_BASE = 10000;
  private static final int N_PRIME = 5000;

  public static double[] lengthDistribution(int[] itinerary, int[] excluded) {
    Set<Integer> excludeSet = new HashSet<>();
    for (int e : excluded) {
      excludeSet.add(e);
    }
    List<Point> edges = readSubGraph(excludeSet);

    Integer origin = itinerary[0];
    Integer dest = itinerary[1];
    Graph<Integer> subgraph = createGraph(edges);
    List<Graph<Integer>> graphs = createNCopies(N_BASE, edges);
    return lengthDistribution(graphs, subgraph, origin, dest);
  }

  private static List<Point> readSubGraph(Set<Integer> exclude) {
    File subgraphFile = new File("subgraph.txt");
    List<Point> subgraphPts = new ArrayList<>();
    Scanner scan = null;
    try {
      scan = new Scanner(subgraphFile);
      while (scan.hasNextLine()) {
	String[] adjacents = scan.nextLine().split(" ");
	int origin = Integer.parseInt(adjacents[0]);
        for (int i = 1; i < adjacents.length && !exclude.contains(origin); i++) {
	  int dest = Integer.parseInt(adjacents[i]);
          if (!exclude.contains(dest)) {
	    subgraphPts.add(new Point(origin, dest));
	  }
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (scan != null) {
        scan.close();
      }
    }
    return subgraphPts;
  }

  private static List<Graph<Integer>> createNCopies(int n, List<Point> edges) {
    Graph<Integer> graph;
    List<Graph<Integer>> graphs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      graphs.add(createGraph(edges));
    }
    return graphs;
  }

  private static Graph<Integer> createGraph(List<Point> edges) {
    Graph<Integer> graph = new Graph<>();
    for (Point pt : edges) {
      graph.addEdge(pt.x, pt.y);
    }
    return graph;
  }

  public static <T> double[] lengthDistribution(List<Graph<T>> nGraph, Graph<T> prGraph, T start, T end) {
    // Step 1
    List<Double> pilotRun = pilotRun(prGraph, start, end);

    double paths = 0;
    double length = 0;
    int count = 0;
    for (int i = 0; i < N_BASE; i++) {
      Graph<T> graph = nGraph.get(i);
      Node<T> sNode = graph.getNode(start);
      Node<T> eNode = graph.getNode(end);
      double currLen = traversal(sNode, eNode, pilotRun, 1.0, 1);
      if (eNode.getVisited()) {
	paths += 1.0/eNode.getSLikelihood();
        length += currLen;
        count++;
      }
    }
    length = count == 0 ? 0.0 : length/(double) count;
    return new double[]{Math.ceil(paths/(double) N_BASE), length};
  }

  private static <T> List<Double> pilotRun(Graph<T> graph, T start, T end) {
     List<Double> pilotList = new ArrayList<>(
         Collections.nCopies(graph.size(), 0.0));
     List<Double> numer = new ArrayList<>(
	 Collections.nCopies(graph.size(), 0.0));
     List<Double> denom = new ArrayList<>(
	 Collections.nCopies(graph.size(), 0.0));

     for (int i = 0; i < N_PRIME; i++) {
       List<Node<T>> path = Algorithm1.naivePathGeneration(graph, start, end);
       Node<T> eNode = graph.getNode(end);
       int index = eNode.getCounter() - 1;
       double newValue;

       if (path.get(path.size() - 1) == eNode) {
	 newValue = numer.get(index) + (1.0/eNode.getLikelihood());
         numer.set(index, newValue);

         for (int j = 0; j < path.size(); j++) {
	   Node<T> node = path.get(j);
	   newValue = denom.get(j) + (1.0/node.getLikelihood());
	   denom.set(j, newValue);
         }
       }
       resetGraph(graph);
     }

     for (int i = 0; i < pilotList.size(); i++) {
       double averaged = denom.get(i) == 0.0 ? 0.0 : numer.get(i)/denom.get(i);
       pilotList.set(i, averaged);
     }
     return pilotList;
  }

  private static <T> int traversal(Node<T> start, Node<T> end,
      List<Double> pilotRun, double likelihood, int counter) {
    // Step 2
    Node<T> curr = start;
    Node<T> next;
    List<Node<T>> unvisited;
    curr.setSLikelihood(likelihood);
    curr.setSCounter(counter);
    curr.setVisited(true);

    boolean completed = false;
    while (!completed) {
      // Step 3
      if (curr.hasEdge(end)) {
	if (curr.getAdjacents().size() == 1) {
	  end.setVisited(true);
          end.setSLikelihood(likelihood);
	  completed = true;
	  continue;
	} else {
	  double nProbability = pilotRun.get(counter - 1);
	  double guessProbability = Math.random();
	  if (guessProbability < nProbability) {
	    likelihood = likelihood * nProbability;
            end.setSLikelihood(likelihood);
            end.setVisited(true);
	    completed = true;
            continue;
	  } else {
	    likelihood = likelihood * (1.0 - nProbability);
	  }
	}
      }

      // Step 5
      unvisited = getUnvisited(curr, end);
      if (unvisited.isEmpty()) {
        completed = true;
        continue;
      }

      // Step 6
      int index = (int) ((double) unvisited.size() * Math.random());
      next = unvisited.get(index);

      // Step 7
      counter++;
      likelihood = likelihood / (double) unvisited.size();
      next.setSCounter(counter);
      next.setSLikelihood(likelihood);
      next.setVisited(true);
      curr = next;
    }
    return counter;
  }

    private static <T> List<Node<T>> getUnvisited(Node<T> curr, Node<T> end) {
    List<Node<T>> validAdj = new ArrayList<>();
    for (Node<T> node : curr.getAdjacents()) {
      if (!node.getVisited() && node != end) {
	validAdj.add(node);
      }
    }
    return validAdj;
  }

  private static <T> void resetGraph(Graph<T> graph) {
    for (Node<T> node : graph.getNodes()) {
      node.setLikelihood(0.0);
      node.setSLikelihood(0.0);
      node.setCounter(0);
      node.setSCounter(0);
      node.setVisited(false);
    }
  }
}
