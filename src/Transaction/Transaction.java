package Transaction;

import Action.Operations;

public class Transaction {
    private String transactionId;
    Boolean isLive;
    private TransactionType transactionType;
    public Transaction(String transactionId, TransactionType transactionType) {
        this.transactionId = transactionId;
        this.isLive = true;
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Boolean getLive() {
        return isLive;
    }

    public void setLive(Boolean live) {
        isLive = live;
    }

}
