package estimator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Node.java
 *
 * Node class for {@link Graph} objects
 *
 *
 */
public class Node<T> {

  private T value;
  private boolean visited;
  private Map<Integer, Integer> distances;
  private List<Node<T>> adjacents;
  private List<Edge<T>> edges;
  private Set<Node<T>> adjacentSet;
  private List<Node<T>> incomingAdjacents;

  // For use in naive path generation
  private double likelihood;
  private int counter;

  // For use in length distribution method
  private double sLikelihood;
  private int sCounter;

  // for unique paths alg
  private int paths;

  /** Constructor */
  public Node(T value) {
    this.value = value;
    this.visited = false;
    this.distances = new HashMap<>();
    this.adjacents = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.adjacentSet = new HashSet<>();
    this.incomingAdjacents = new ArrayList<>();
  }

  /** Adds a node to a node's adjacent list. */
  public void addAdjacent(Node<T> node) {
    adjacents.add(node);
    adjacentSet.add(node);
  }

  public void addAdjacent(Node<T> node, int num, int length) {
    adjacents.add(node);
    edges.add(new Edge(this, node, num, length));
    adjacentSet.add(node);
  }

  public void addIncomingAdjacent(Node<T> node) {
    incomingAdjacents.add(node);
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

  /** Returns a set of adjacent nodes. */
  public Set<Node<T>> getAdjacentSet() {
    return adjacentSet;
  }

  public List<Node<T>> getIncomingAdjacents() {
    return incomingAdjacents;
  }

  /**
   * Adds a length to the value to the map of nodes determined by the
   * distance to a destination
   *
   */
  public void addDistance(int length) {
    if (distances.containsKey(length)) {
      distances.put(length, distances.get(length) + 1);
    } else {
      distances.put(length, 1);
    }
  }

  /**
   * Increments the values associated with the key (length)
   * for the mapping of path-length to occurrences.
   *
   */
  public void addDistance(int length, int times) {
    distances.put(length, times);
  }

  /** Retrives the path-length to occurrence mappings. */
  public Map<Integer, Integer> getDistances() {
    return distances;
  }

  /** Gets the count for a partcular distance */
  public Integer getDistanceCount(int count) {
    return distances.get(count);
  }

  /** Determines whether or not a node has an edge to another node */
  public boolean hasEdge(Node<T> node) {
    return adjacentSet.contains(node);
  }

  /** Sets the likelihood */
  public void setLikelihood(double likelihood) {
    this.likelihood = likelihood;
  }

  /** Retrieves the likelihood */
  public double getLikelihood() {
    return likelihood;
  }

  /** Sets the counter */
  public void setCounter(int counter) {
    this.counter = counter;
  }

  /** Retrieves the counter */
  public int getCounter() {
    return counter;
  }

  /** Sets the likelihood */
  public void setSLikelihood(double sLikelihood) {
    this.sLikelihood = sLikelihood;
  }

  /** Retrieves the likelihood */
  public double getSLikelihood() {
    return sLikelihood;
  }

  /** Sets the counter */
  public void setSCounter(int sCounter) {
    this.sCounter = sCounter;
  }

  /** Retrieves the counter */
  public int getSCounter() {
    return sCounter;
  }

  /** Sets the number of paths from this node to the destination */
  public void setNumPaths(int paths) {
    this.paths = paths;
  }

  /** Retrieves the number of paths from this node to the destination */
  public int getNumPaths() {
    return paths;
  }

  /** Rich output of information for this node */
  public void printDetail() {
    System.out.println("Value: " + value);
    System.out.println("\tLikelihood: " + likelihood);
    System.out.println("\tCounter: " + counter);
  }

  /** Rich output of information for this node */
  public void sPrintDetail() {
    System.out.println("Value: " + value);
    System.out.println("\tsLikelihood: " + sLikelihood);
    System.out.println("\tsCounter: " + sCounter);
  }

  @Override
  public String toString() {
    StringBuffer strBffr = new StringBuffer();
    strBffr.append("{value: ");
    strBffr.append(value);
    strBffr.append(", visited: ");
    strBffr.append(visited);
    strBffr.append("}");
    return strBffr.toString();
  }
}
