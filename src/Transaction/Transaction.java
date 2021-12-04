package Transaction;

public class Transaction {
    private TRANSACTIONOPS transactionOperation;
    private TRANSACTIONS transaction;
    private String transactionId;
    private String variable;
    private int value;
    private int siteId;

    public Transaction(TRANSACTIONOPS transactionOperation, TRANSACTIONS transaction, String transactionId, String variable, int value, int siteId) {
        this.transactionOperation = transactionOperation;
        this.transaction = transaction;
        this.transactionId = transactionId;
        this.variable = variable;
        this.value = value;
        this.siteId = siteId;
    }

    public TRANSACTIONOPS getOperation() {
        return transactionOperation;
    }

    public void setOperation(TRANSACTIONOPS transactionOperation) {
        this.transactionOperation = transactionOperation;
    }

    public TRANSACTIONS getTransaction() {
        return transaction;
    }

    public void setTransaction(TRANSACTIONS transaction) {
        this.transaction = transaction;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
}
