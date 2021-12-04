package Transaction;

public class Transaction {
    private OPERATIONS operation;
    private TRANSACTIONS transaction;
    private String transactionId;
    private String variable;
    private int value;
    private int siteId;

    public Transaction(OPERATIONS operation, TRANSACTIONS transaction, String transactionId, String variable, int value, int siteId) {
        this.operation = operation;
        this.transaction = transaction;
        this.transactionId = transactionId;
        this.variable = variable;
        this.value = value;
        this.siteId = siteId;
    }

    public OPERATIONS getOperation() {
        return operation;
    }

    public void setOperation(OPERATIONS operation) {
        this.operation = operation;
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
