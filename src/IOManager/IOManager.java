package IOManager;

import Action.Operations;
import Transaction.TRANSACTIONS;
import Transaction.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class IOManager {
    private BufferedReader reader;

    public IOManager(String filename){
        try{
            reader = new BufferedReader(new FileReader(filename));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine(){
        String line = "";
        try{
            line = reader.readLine();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public ArrayList<Transaction> readInput(String filename) {
        BufferedReader reader;
        ArrayList<Transaction> transactions = new ArrayList<>();
        HashSet<String> readOnlyTransactions = new HashSet<>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();

            int siteId;
            int value;
            String transactionId;
            String variable;
            Operations operation;
            TRANSACTIONS transaction;
            while (line != null) {
                siteId = -1;
                value = -1;
                transactionId = null;
                variable = null;
                operation = null;
                transaction = null;
                if (line.startsWith("beginRO")) {
                    transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    operation = Operations.BEGINRO;
                    readOnlyTransactions.add(transactionId);
                } else if (line.startsWith("begin")) {
                    transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    operation = Operations.BEGIN;
                } else if (line.startsWith("fail")) {
                    siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
                    operation = Operations.FAIL;
                } else if (line.startsWith("recover")) {
                    siteId = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
                    operation = Operations.RECOVER;
                } else if (line.startsWith("end")) {
                    transactionId = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    operation = Operations.END;
                } else if (line.startsWith("dump")) {
                    operation = Operations.DUMP;
                } else if (line.startsWith("W")) {
                    String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    transactionId = fields.split(",")[0];
                    variable = fields.split(",")[1];
                    value = Integer.parseInt(fields.split(",")[2]);
                    transaction = TRANSACTIONS.WRITE;
                } else if (line.startsWith("R")) {
                    String fields = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    transactionId = fields.split(",")[0];
                    if (readOnlyTransactions.contains(transactionId)) {
                        transaction = TRANSACTIONS.READONLY;
                    } else {
                        transaction = TRANSACTIONS.READ;
                    }
                    variable = fields.split(",")[1];
                }
                Transaction t = new Transaction(operation, transaction, transactionId, variable, value, siteId);
                transactions.add(t);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
