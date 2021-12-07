package Deadlock;

import Transaction.Transaction;
import util.Graph;

import java.util.ArrayList;
import java.util.List;

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

    public Transaction resolveDeadlock(List<Transaction> transactions) {
        // kill the earliest transaction in the cycle
        ArrayList<String> affectedNodes = getAffectedTransactions();
        Transaction earliestTransaction = null;
        int earliestTime = Integer.MIN_VALUE;

        for(Transaction t : transactions) {
            for(String node : affectedNodes) {
                if(t.getTransactionId().equals(node) && t.getStartTime() > earliestTime) {
                    earliestTime = t.getStartTime();
                    earliestTransaction = t;
                }
            }
        }
        removeEdge(earliestTransaction.getTransactionId());
        return earliestTransaction;
    }
}
