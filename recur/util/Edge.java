package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Edge<T> {

  Node<T> entryNode;
  Node<T> exitNode;
  double numOfPaths;
  double totalLength;
  boolean isCrossEdge;

  public Edge(Node<T> entry, Node<T> exit) {
    this(entry, exit, false);
  }

  public Edge(Node<T> entry, Node<T> exit, boolean crossEdge) {
    this(entry, exit, 1, 1, crossEdge);
  }

  public Edge(Node<T> entry, Node<T> exit, double num, double length) {
    this(entry, exit, num, length, false);
  }

  public Edge(Node<T> entry, Node<T> exit, double num, double length, boolean crossEdge) {
    entryNode = entry;
    exitNode = exit;
    numOfPaths = num;
    totalLength = length;
    isCrossEdge = crossEdge;
  }

  public void setCross(boolean isCrossEdge) {
    this.isCrossEdge = isCrossEdge;
  }

  public boolean isCrossEdge() {
    return isCrossEdge;
  }

  public boolean contains(Node<T> node) {
    return exitNode.equals(node);
  }

  public void setNumOfPaths(double num) {
    numOfPaths = num;
  }

  public void setTotalLength(double length) {
    totalLength = length;
  }

  public double getNumOfPaths() {
    return numOfPaths;
  }

  public double getTotalLength() {
    return totalLength;
  }

  public Node<T> getEntryNode() {
    return entryNode;
  }

  public Node<T> getExitNode() {
    return exitNode;
  }

  @Override
  public String toString() {
    return "Edge(" + entryNode + "-" + numOfPaths + "/" + totalLength + "-" + exitNode + ")";
  }

}
