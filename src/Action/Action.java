/**
 * @author Shobhit Sinha
 * @version 1.0.0
 */
package Action;

import Transaction.Transaction;

public class Action {
    protected Operations operation ;
    protected Transaction transaction;

    public Operations getOperation() {
        return operation;
    }

    public void setOperation(Operations operation) {
        this.operation = operation;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
