package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Node<T> {

  private T value;
  private boolean visited;
  private Map<Double, Double> distances;
  private List<Edge<T>> edges;
  private List<Node<T>> adjacents;
  private Set<Node<T>> adjacentSet;
  private List<Node<T>> incomingAdjacents;
  private List<Edge<T>> inEdges;
  private List<Edge<T>> inCrossEdges;
  private double numOfPaths;
  private double lenOfPaths;

  private boolean isEntryNode;
  private boolean isExitNode;

  // List of cross edges that leave this node
  private List<Edge<T>> crossOutEdges;
  // List of cross edges that enter this node
  private List<Edge<T>> crossInEdges;
  // Reference to its split node (only goes from entry to exit);
  private Node<T> splitNode;

  public Node(T value) {
    this.value = value;
    this.visited = false;
    this.distances = new HashMap<>();
    this.adjacents = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.adjacentSet = new HashSet<>();
    this.incomingAdjacents = new ArrayList<>();
    this.inEdges = new ArrayList<>();
    this.inCrossEdges = new ArrayList<>();
    this.numOfPaths = 0;
    this.lenOfPaths = 0;
    this.isEntryNode = false;
    this.isExitNode = false;

    this.crossOutEdges = new ArrayList<>();
    this.crossInEdges = new ArrayList<>();
    splitNode = null;
  }

  // Outgoing cross edges are identified after edges have been created,
  // so this method does not create a new edge, but rather keeps track
  // of newly identified outgoing cross edges. 
  public void addCrossOutgoing(Node<T> node, Edge<T> edge) {
    // Since there is an outgoing cross edge, this node is an exit node
    isExitNode = true;
    // Edge is identified as cross edge
    edge.setCross(true);
    // Keep track of outgoing cross edge
    crossOutEdges.add(edge);
  }

  public void addCrossIncoming(Node<T> node, Edge<T> edge) {
    isEntryNode = true;
    edge.setCross(true);
    crossInEdges.add(edge);
  }

  public Node<T> getSplitNode() {
    return splitNode;
  }

  // If this method is called, current node is both entry and exit node.
  // Node must be split and newly created exit node is returned.
  public Node<T> exitTransfer() {
    if (!isEntryNode || !isExitNode) {
      System.out.println("Node " + this + " is not split node");
    }
    // Newly split exit node
    Node<T> node = new Node(value);

    // Move outgoing cross edges from this node to the newly split exit node
    for (Edge<T> e : crossOutEdges) {
      Edge<T> edge = node.addSuperAdjacent(e.getExitNode(), e.getNumOfPaths(), e.getTotalLength());
      edge.setCross(true);
      e.getExitNode().removeIncomingAdjacent(this);
      e.getExitNode().addIncomingAdjacent(node, edge);
    }

    // Remove those outgoing cross edges from our list of outgoing edges
    for (Edge<T> e : crossOutEdges) {
      adjacents.remove(e.getExitNode());
      adjacentSet.remove(e.getExitNode());
      edges.remove(e);
    }

    // Clear outgoing cross edges list
    crossOutEdges = new ArrayList<>();

    // Mark this node as not being an exit node
    isExitNode = false;

    // Add a special edge of length 0 from this node to the newly split node
    Edge<T> edge = addSuperAdjacent(node, 1, 0);
    node.addIncomingAdjacent(this, edge);
    splitNode = node;
    node.setSplitNode(this);
    return node;
  }

  public void setSplitNode(Node<T> node) {
    this.splitNode = node;
  }

  public void setNumOfPaths(double paths) {
    numOfPaths = paths;
  }

  public void setLengthOfPaths(double length) {
    lenOfPaths = length;
  }

  public double getNumOfPaths() {
    return numOfPaths;
  }

  public double getLengthOfPaths() {
    return lenOfPaths;
  }

  public boolean isEntryNode() {
    return isEntryNode;
  }

  public boolean isExitNode() {
    return isExitNode;
  }

  public void setEntry(boolean val) {
    isEntryNode = val;
  }

  public void setExit(boolean val) {
    isExitNode = val;
  }

  /** Adds a node to a node's adjacent list. */
  public Edge<T> addAdjacent(Node<T> node) {
    adjacents.add(node);
    adjacentSet.add(node);
    Edge<T> edge = new Edge(this, node);
    edges.add(edge);
    return edge;
  }

  public Edge<T> addSuperAdjacent(Node<T> node, double num, double length) {
    adjacents.add(node);
    adjacentSet.add(node);
    Edge<T> edge = new Edge(this, node, num, length);
    edges.add(edge);
    return edge;
  }

  public Edge<T> addSuperEdge(Node<T> node) {
    Edge<T> nEdge = new Edge(this, node, 1, 1);
    edges.add(nEdge);
    return nEdge;
  }

  public void addIncomingAdjacent(Node<T> node, Edge<T> edge) {
    incomingAdjacents.add(node);
    inEdges.add(edge);
  }

  /** Retrieved the value contained by this node. */
  public T getValue() {
    return value;
  }

  /** Sets the value that determines whether or not a node has been visited. */
  public void setVisited(boolean visited) {
    this.visited = visited;
  }

  /** Returns a boolean value indicating that a node has been visited */
  public boolean getVisited() {
    return visited;
  }

  /** Returns a list of adjacent nodes. */
  public List<Node<T>> getAdjacents() {
    return adjacents;
  }

  public List<Node<T>> getIncomingAdjacents() {
    return incomingAdjacents;
  }

  // Does not remove inEdges record!
  public void removeIncomingAdjacent(Node<T> node) {
    incomingAdjacents.remove(node);
    for (Edge<T> edge : new ArrayList<>(inEdges)) {
      if (edge.getExitNode().equals(this)) {
        inEdges.remove(edge);
      }
    }
  }

  public List<Edge<T>> getEdges() {
    return edges;
  }

  public List<Edge<T>> getOutgoingEdges() {
    return edges;
  }

  public List<Edge<T>> getIncomingEdges() {
    return inEdges;
  }

  /**
   * Adds a length to the value to the map of nodes determined by the
   * distance to a destination
   */
  public void addDistance(double length) {
    if (distances.containsKey(length)) {
      distances.put(length, distances.get(length) + 1);
    } else {
      distances.put(length, 1.0);
    }
  }

  /**
   * Increments the values associated with the key (length)
   * for the mapping of path-length to occurrences.
   */
  public void addDistance(double length, double times) {
    distances.put(length, times);
  }

  /** Retrives the path-length to occurrence mappings. */
  public Map<Double, Double> getDistances() {
    return distances;
  }

  /** Gets the count for a partcular distance */
  public Double getDistanceCount(double count) {
    return distances.get(count);
  }

  /** Determines whether or not a node has an edge to another node */
  public boolean hasEdge(Node<T> node) {
    return adjacentSet.contains(node);
  }

  public boolean equalValue(Node<T> node) {
    // int a = this.value < 0 ? -1*this.value : this.value;
    // int b = node.getValue() < 0 ? -1*node.getValue() : node.getValue();
    // return a == b;
    return this.value == node.getValue();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Node) {
      boolean equality = this.value == ((Node<T>) obj).getValue();
      equality &= this.isEntryNode == ((Node<T>) obj).isEntryNode();
      equality &= this.isExitNode == ((Node<T>) obj).isExitNode();
      // System.out.println("Equality between " + this + " and " + (Node<T>) obj + "= " + equality);
      return equality;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    if (isEntryNode && isExitNode) {
      return "en/ex-" + value.toString();
    } else if (isEntryNode) {
      return "entry-" + value.toString();
    } else if (isExitNode) {
      return "exit-" + value.toString();
    } else {
      return value.toString();
    }
  }
}
