package Action;

import Transaction.Transaction;

public class BeginAction extends Action {
    public BeginAction(Transaction transaction){
        this.operation = Operations.BEGIN;
        this.transaction = transaction;
    }
}
