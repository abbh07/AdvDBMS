package Action;

import Transaction.Transaction;

public class EndAction extends Action {
    public EndAction(Transaction transaction){
        this.operation = Operations.END;
        this.transaction = transaction;
    }
}
