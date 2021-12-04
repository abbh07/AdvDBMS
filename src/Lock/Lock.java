package Lock;

import Transaction.Transaction;

public class Lock {

    LOCKTYPES lockType;
    Transaction transaction;

    Lock(){
        this.lockType = LOCKTYPES.NOLOCK;
        this.transaction = null;
    }

    public LOCKTYPES getLockType() {
        return lockType;
    }

    public void setLockType(LOCKTYPES lockType) {
        this.lockType = lockType;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }


}
