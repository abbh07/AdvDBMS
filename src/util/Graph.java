/**
 * @author Aakash Bhattacharya
 * @version 1.0.0
 * @date 11/21/2021
 */
package util;

import java.util.*;

public class Graph {
    private static final int totalVertices = 100000;
    private List<List<Integer>> adj;

    public ArrayList<Integer> getAffectedNodes() {
        return affectedNodes;
    }

    private ArrayList<Integer> affectedNodes;

    public Graph() {
        affectedNodes = new ArrayList<>();
        adj = new ArrayList<>();
        for (int i = 0; i < totalVertices; i++) {
            adj.add(new LinkedList<>());
        }
    }

    /**
     * The method performs a DFS traversal of the graph and finds a cycle.
     *
     * @param index          Start index of the traversal
     * @param visited        Visited nodes
     * @param recursionStack Nodes in the current stack
     * @return Boolean identifying if a cycle is found
     */
    private boolean dfs(int index, boolean[] visited, boolean[] recursionStack) {
        if (recursionStack[index]) {
            return true;
        }
        if (visited[index]) {
            return false;
        }

        recursionStack[index] = true;

        List<Integer> neighbors = adj.get(index);

        for (Integer n : neighbors) {
            if (dfs(n, visited, recursionStack)) {
                affectedNodes.add(n);
                return true;
            }
        }
        recursionStack[index] = false;

        return false;
    }

    /**
     * Helper to identify a cycle in the graph.
     *
     * @return Boolean identifying a cycle
     */
    public boolean isCyclic() {
        boolean[] visited = new boolean[totalVertices];
        boolean[] recursionStack = new boolean[totalVertices];

        for (int i = 0; i < totalVertices; i++) {
            affectedNodes.clear();
            if (dfs(i, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add edge to the graph.
     *
     * @param source      Source node
     * @param destination Destination node
     */
    public void addEdge(int source, int destination) {
        adj.get(source).add(destination);
    }

    /**
     * Remove an edge from the graph.
     *
     * @param source Node which has to be removed
     */
    public void removeEdge(int source) {
        adj.get(source).clear();
        for (int i = 0; i < adj.size(); i++) {
            adj.get(i).removeAll(Collections.singleton(source));
        }
    }
}
