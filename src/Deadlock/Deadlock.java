package Deadlock;

import util.Graph;

import java.util.ArrayList;

public class Deadlock {

    private Graph graph;

    public Deadlock(){
        graph = new Graph();
    }

    public boolean checkForDeadlock() {
        return graph.isCyclic();
    }

    public void addEdge(String source, String destination) {
        graph.addEdge(Integer.parseInt(source.replace("T", "")), Integer.parseInt(destination.replace("T", "")));
    }

    public void removeEdge(String source) {
        graph.removeEdge(Integer.parseInt(source.replace("T", "")));
    }

    public ArrayList<String> getAffectedTransactions(){
        ArrayList<Integer> nodes = graph.getAffectedNodes();
        ArrayList<String> affectedNodes = new ArrayList<>();
        for(Integer n : nodes) {
            affectedNodes.add("T" + n);
        }
        return affectedNodes;
    }
}
