import TransactionManager.TransactionManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String filename = new File("/Users/aakash/Programming/IntelliJ Projects/AdvDBMS/input/input0").getAbsolutePath();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.init();
        transactionManager.simulate(filename);

    }

}
