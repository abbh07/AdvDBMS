/**
 * @author Aakash Bhattacharya
 * @version 1.0.0
 * @date 12/02/2021
 */
package Deadlock;

import Transaction.Transaction;
import util.Graph;

import java.util.ArrayList;
import java.util.List;

public class Deadlock {

    private Graph graph;

    public Deadlock() {
        graph = new Graph();
    }

    /**
     * Checks if there's a deadlock in the scenario by using isCyclic() method.
     *
     * @return Boolean specifying the status of deadlock
     */
    public boolean checkForDeadlock() {
        return graph.isCyclic();
    }

    /**
     * Add edge to the graph.
     *
     * @param source      Source node
     * @param destination Destination node
     */
    public void addEdge(String source, String destination) {
        graph.addEdge(Integer.parseInt(source.replace("T", "")), Integer.parseInt(destination.replace("T", "")));
    }

    /**
     * Remove an edge from the graph.
     *
     * @param source Node which has to be removed
     */
    public void removeEdge(String source) {
        graph.removeEdge(Integer.parseInt(source.replace("T", "")));
    }

    /**
     * Computes a list of all the nodes in the cycle.
     *
     * @return A list of affected nodes in the cycle
     */
    public ArrayList<String> getAffectedTransactions() {
        ArrayList<Integer> nodes = graph.getAffectedNodes();
        ArrayList<String> affectedNodes = new ArrayList<>();
        for (Integer n : nodes) {
            affectedNodes.add("T" + n);
        }
        return affectedNodes;
    }

    /**
     * Resolves a deadlock by killing the earliest transaction in the cycle.
     *
     * @param transactions List of all the transactions
     * @return Victim transaction
     */
    public Transaction resolveDeadlock(List<Transaction> transactions) {
        // kill the earliest transaction in the cycle
        ArrayList<String> affectedNodes = getAffectedTransactions();
        Transaction earliestTransaction = null;
        int earliestTime = Integer.MIN_VALUE;

        for (Transaction t : transactions) {
            for (String node : affectedNodes) {
                if (t.getTransactionId().equals(node) && t.getStartTime() > earliestTime) {
                    earliestTime = t.getStartTime();
                    earliestTransaction = t;
                }
            }
        }
        removeEdge(earliestTransaction.getTransactionId());
        return earliestTransaction;
    }
}
