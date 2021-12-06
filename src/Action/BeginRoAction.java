package Action;

import Transaction.Transaction;

public class BeginRoAction extends Action{
    public BeginRoAction(Transaction transaction){
        this.operation = Operations.BEGINRO;
        this.transaction = transaction;
    }
}
