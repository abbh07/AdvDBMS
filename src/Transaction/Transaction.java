package Transaction;

import Action.Operations;

public class Transaction {
    private String transactionId;
    Boolean isLive;

    private TransactionType transactionType;

    private int startTime;
    public Transaction(String transactionId, TransactionType transactionType, int startTime) {
        this.transactionId = transactionId;
        this.isLive = true;
        this.transactionType = transactionType;
        this.startTime = startTime;
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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

}
