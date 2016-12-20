package general;

import util.Graph;
import util.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

public class Algorithm1 {

  private static Random generator = new Random();

  /** Implementation of algorithm 1 (Naive Path Generation). */
  public static <T> List<Node<T>> naivePathGeneration(Graph<T> graph, T start, T end) {
    Node<T> sNode = graph.getNode(start);
    Node<T> eNode = graph.getNode(end);
    return naivePathGeneration(sNode, eNode, 1.0, 1);
  }

    private static <T> List<Node<T>> naivePathGeneration(Node<T> start, Node<T> end, double likelihood, int counter) {
    List<Node<T>> path = new ArrayList<>();
    Node<T> curr = start;

    // Step 1
    curr.setLikelihood(likelihood);
    curr.setCounter(counter);

    // Step 2
    curr.setVisited(true);

    boolean complete = false;
    while (!complete) {
      path.add(curr);

      // Step 3
      List<Node<T>> list = getUnvisited(curr);
      if (list.isEmpty()) {
        complete = true;
        continue;
      }

      // Step 4
      int index = (int) ((double) list.size() * Math.random());
      Node<T> next = list.get(index);

      // Step 5
      likelihood = likelihood/(double) list.size();
      counter++;
      next.setLikelihood(likelihood);
      next.setCounter(counter);
      next.setVisited(true);
      curr = next;

      // Step 6
      if (curr == end) {
	path.add(curr);
	complete = true;
      }
    }
    return path;
  }

  private static <T> List<Node<T>> getUnvisited(Node<T> curr) {
    // Processing of unvisited nodes.
    List<Node<T>> validAdj = new ArrayList<>();
    for (Node<T> node : curr.getAdjacents()) {
      if (!node.getVisited()) {
	validAdj.add(node);
      }
    }
    return validAdj;
  }
}
