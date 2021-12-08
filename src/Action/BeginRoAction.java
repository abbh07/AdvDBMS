/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/29/2021
 */
package Action;

import Transaction.Transaction;

/**
 * BeginRoAction subclass with a constructor.
 */
public class BeginRoAction extends Action {
    public BeginRoAction(Transaction transaction) {
        this.operation = Operations.BEGINRO;
        this.transaction = transaction;
    }
}
