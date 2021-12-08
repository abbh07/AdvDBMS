/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/22/2021
 */
package Transaction;

/**
 * Transaction class holding the TransactionId, its type and a boolean.
 * The class contains a constructor along with appropriate getters and setters.
 */
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
