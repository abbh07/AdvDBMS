/**
 * @author Shobhit Sinha
 * @version 1.0.0
 */
package Lock;

import Transaction.Transaction;

public class Lock {

    LockTypes lockType;
    Transaction transaction;

    public Lock(){
        this.lockType = LockTypes.NOLOCK;
        this.transaction = null;
    }

    public Lock(LockTypes lockType, Transaction transaction){
        this.lockType = lockType;
        this.transaction = transaction;
    }

    public LockTypes getLockType() {
        return lockType;
    }

    public void setLockType(LockTypes lockType) {
        this.lockType = lockType;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }


}
