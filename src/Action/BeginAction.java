/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/28/2021
 */
package Action;

import Transaction.Transaction;

public class BeginAction extends Action {
    public BeginAction(Transaction transaction){
        this.operation = Operations.BEGIN;
        this.transaction = transaction;
    }
}
