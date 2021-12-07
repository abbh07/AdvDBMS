import TransactionManager.TransactionManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        String filename = new File("/Users/shobhitsinha/Documents/Course/Sem-2/ADB/proj/AdvDBMS/input/input1").getAbsolutePath();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.init();
        transactionManager.simulate(filename);

    }

}
