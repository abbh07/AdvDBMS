import IOManager.IOManager;
import Transaction.Transaction;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String filename = new File("/Users/aakash/Programming/IntelliJ Projects/AdvDBMS/input/input0").getAbsolutePath();
        IOManager ioManager = new IOManager();
        ArrayList<Transaction> transactions = ioManager.readInput(filename);
        for (Transaction t : transactions) {
            System.out.println(t.getOperation() + " " + t.getTransaction() + " " + t.getTransactionId() + " " + t.getVariable() + " " + t.getValue() + " " + t.getSiteId());
        }
    }

}
