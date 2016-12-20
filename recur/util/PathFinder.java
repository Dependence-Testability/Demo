package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathFinder {

  public static <T> double[] superDagTraversal(Graph<T> graph,
      Node<T> sNode, Node<T> eNode) {
    List<Node<T>> dfsOrdered = dfsTopoSort(graph, sNode);
    eNode.setVisited(true);
    calculateSuperPath(dfsOrdered, 0, sNode, eNode);
    return new double[]{sNode.getNumOfPaths(), sNode.getLengthOfPaths()};
  }

  /**
   * Performs a Topologicial Sort of the nodes in graph in
   * order of Depth-First Search.
   *
   * @param graph the graph for which the Topological Sort
   *     is to take place on.
   * @param sNode the node to begin the Topological Sort at.
   *
   * @return an {@link List} of the nodes in order of Topological Sort
   *
   */
  private static <T> List<Node<T>> dfsTopoSort(Graph<T> graph,
      Node<T> sNode) {
    List<Node<T>> topoSorted = new ArrayList<>();
    dfsTopoSort(topoSorted, new HashSet<>(), sNode);
    // Since topological sort returns the list of nodes in order of
    // finishing times, the first node we exam (our start node), will
    // always be the last node in the list. For clarity in the algorithm
    // for computing the number of paths from s to t as well as the
    // average length of those paths between them, we will reverse the list.
    Collections.reverse(topoSorted);
    return topoSorted;
  }

  /** Auxiliary method to aid in computing the Topological Sort */
  private static <T> void dfsTopoSort(List<Node<T>> tSorted,
      Set<Node<T>> tSortSet, Node<T> node) {
    node.setVisited(true);
    for (Node<T> adj : node.getAdjacents()) {
      if (!adj.getVisited() && !tSortSet.contains(adj)) {
        dfsTopoSort(tSorted, tSortSet, adj);
      }
    }
    node.setVisited(false);
    tSorted.add(node);
    tSortSet.add(node);
  }

  private static <T> void calculateSuperPath(List<Node<T>> sorted,
      int position, Node<T> curr, Node<T> end) {
    if (!curr.getVisited()) {
      curr.setVisited(true);
      for (int i = position + 1; i < sorted.size(); i++) {
        if (curr.hasEdge(sorted.get(i))) {
          Node<T> adj = sorted.get(i);
          calculateSuperPath(sorted, i, adj, end);

          Edge<T> adjEdge = null;
          for (Edge<T> edge : curr.getEdges()) {
            if (edge.contains(adj)) {
              adjEdge = edge;
            }
          }

          double totalNumPaths = adj.getNumOfPaths() == 0 ? 1 : adj.getNumOfPaths();
          double totalLength = adj.getLengthOfPaths();

          double originalNumPaths = adjEdge.getNumOfPaths();
          double originalLength = adjEdge.getTotalLength();

          double newNumPaths = totalNumPaths * originalNumPaths;
          double newLenPaths = (totalNumPaths * originalLength) + (originalNumPaths * totalLength);

          double currNumPaths = curr.getNumOfPaths();
          double currLength = curr.getLengthOfPaths();

          curr.setNumOfPaths(newNumPaths + currNumPaths);
          curr.setLengthOfPaths(newLenPaths + currLength);
        }
      }
    }
  }
}