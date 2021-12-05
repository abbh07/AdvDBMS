package Action;

import Transaction.Transaction;

public class DumpAction extends Action {

    public DumpAction(Transaction transaction){
        this.operation = Operations.DUMP;
        this.transaction = transaction;
    }
}
