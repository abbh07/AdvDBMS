/**
 * @author Shobhit Sinha
 * @version 1.0.0
 * @date 11/28/2021
 */
package Action;

import Transaction.Transaction;

/**
 * WriteAction subclass with a constructor and getters/setters.
 */
public class WriteAction extends Action {
    int value;
    String variable;

    public WriteAction(Transaction transaction, String variable, int value) {
        this.operation = Operations.WRITE;
        this.transaction = transaction;
        this.variable = variable;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
