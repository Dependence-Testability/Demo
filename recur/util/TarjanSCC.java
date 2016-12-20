package util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TarjanSCC<T> {

  private Map<Node<T>, Integer> low;
  private int pre;
  private Stack<Node<T>> stack;
  private List<SCC<T>> sccs;

  public TarjanSCC(Graph<T> graph) {
    if (graph.mapSize() != 0) {
      nodeMap(graph);
    } else {
      nodes(graph);
    }
  }

  private void nodes(Graph<T> graph) {
    sccs = new ArrayList<>();
    low = new HashMap<>();
    stack = new Stack<>();
    int vertices = graph.size();
    for (Node<T> node : graph.getNodes()) {
      if (!node.getVisited()) {
        dfs(graph, node, vertices);
      }
    }
    for (Node<T> node : graph.getNodes()) {
      node.setVisited(false);
    }
  }

  private void nodeMap(Graph<T> graph) {
    sccs = new ArrayList<>();
    low = new HashMap<>();
    stack = new Stack<>();
    int vertices = graph.mapSize();
    for (T nodeValue : graph.getNodeValues()) {
      Node<T> node = graph.getNode(nodeValue);
      if (!node.getVisited()) {
        dfs(graph, node, vertices);
      }
    }
    for (T value : graph.getNodeValues()) {
      graph.getNode(value).setVisited(false);
    }
  }

  private void dfs(Graph<T> graph, Node<T> node, int vertices) {
    node.setVisited(true);
    int min = pre;
    low.put(node, pre++);
    stack.push(node);
    for (Node<T> adjNode : node.getAdjacents()) {
      if (!adjNode.getVisited()) {
        dfs(graph, adjNode, vertices);
      }
      if (low.get(adjNode) < min) {
        min = low.get(adjNode);
      }
    }
    if (min < low.get(node)) {
      low.put(node, min);
      return;
    }
    SCC<T> scc = new SCC<>();
    Node<T> w;
    do {
      w = stack.pop();
      scc.add(w);
      low.put(w, vertices);
    } while (!w.equals(node));
    sccs.add(scc);
  }

  /**
   * Returns the number of strong components.
   * @return the number of strong components
   */ 
  public int count() {
    return sccs.size();
  }

  public List<SCC<T>> getSCCs() {
    return sccs;
  }

  @Override
  public String toString() {
    return sccs.toString();
  }

}
