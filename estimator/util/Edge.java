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
  int numOfPaths;
  int avgLength;

  public Edge(Node<T> entry, Node<T> exit) {
    entryNode = entry;
    exitNode = exit;
    numOfPaths = 1;
    avgLength = 1;
  }

  public Edge(Node<T> entry, Node<T> exit, int num, int length) {
    entryNode = entry;
    exitNode = exit;
    numOfPaths = num;
    avgLength = length;
  }

  public Node<T> getEntryNode() {
    return entryNode;
  }

  public Node<T> getExitNode() {
    return exitNode;
  }

}
