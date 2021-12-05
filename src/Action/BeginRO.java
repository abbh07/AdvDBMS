package Action;

import Transaction.Transaction;

public class BeginRO extends Action{
    public BeginRO(Transaction transaction){
        this.operation = Operations.BEGINRO;
        this.transaction = transaction;
    }
}
