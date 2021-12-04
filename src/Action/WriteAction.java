package Action;

public class WriteAction extends Action{
    int value;
    WriteAction(String var){
        this.variable = var;
    }
    public int execute(String var, int value){
        return 0;
    }
}
