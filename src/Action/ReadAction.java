package Action;

import Transaction.Transaction;

public class ReadAction extends Action{

    String variable;

    public ReadAction(Transaction transaction, String variable){
        this.operation = Operations.READ;
        this.transaction = transaction;
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

}
