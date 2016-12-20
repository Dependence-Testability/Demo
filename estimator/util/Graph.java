package estimator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Graph<T> {

  private Map<T, Node<T>> nodeMap;

  public Graph() {
    nodeMap = new HashMap<>();
  }

  public void addNode(T value) {
    if (!containsNode(value)) {
      Node<T> node = new Node<>(value);
      nodeMap.put(value, node);
    }
  }

  public void addEdge(T start, T end) {
    Node<T> s = findOrConstructNode(start);
    Node<T> e = findOrConstructNode(end);
    s.addAdjacent(e);
  }

  public void addEdge(T start, T end, int num, int length) {
    Node<T> s = findOrConstructNode(start);
    Node<T> e = findOrConstructNode(end);
    s.addAdjacent(e, num, length);
    e.addIncomingAdjacent(s);
  }

  public Node<T> getNode(T value) {
    return nodeMap.get(value);
  }

  public Collection<Node<T>> getNodes() {
    return nodeMap.values();
  }

  public Set<T> getNodeValues() {
    return nodeMap.keySet();
  }

  public int size() {
    return nodeMap.size();
  }

  private Node<T> findOrConstructNode(T value) {
    if (!containsNode(value)) {
      addNode(value);
    }
    return getNode(value);
  }

  public boolean containsNode(T value) {
    return nodeMap.containsKey(value);
  }

  @Override
  public String toString() {
    StringBuilder strBldr = new StringBuilder();
    for (Map.Entry<T, Node<T>> entry : nodeMap.entrySet()) {
      strBldr.append(entry.getValue());
      strBldr.append(": ");
      strBldr.append(entry.getValue().getAdjacents());
      strBldr.append("\n");
    }
    return strBldr.toString();
  }
}
