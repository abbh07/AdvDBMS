package util;

import java.util.*;

public class Graph {
    private static final int totalVertices = 100;
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

    public void addEdge(int source, int destination) {
        adj.get(source).add(destination);
    }

    public void removeEdge(int source) {
        adj.get(source).clear();
        for (int i = 0; i < adj.size(); i++) {
            adj.get(i).removeAll(Collections.singleton(source));
        }
    }
}
