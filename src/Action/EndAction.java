/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/28/2021
 */
package Action;

import Transaction.Transaction;

public class EndAction extends Action {
    public EndAction(Transaction transaction){
        this.operation = Operations.END;
        this.transaction = transaction;
    }
}
