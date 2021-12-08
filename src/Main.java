/**
 * @author Aakash Bhattacharya and Shobhit Sinha
 * @version 1.0.0
 * @date 11/21/2021
 */

import TransactionManager.TransactionManager;

import java.io.File;

public class Main {

    /**
     * Main method of the whole codebase. Creates a TransactionManager object and initiates the simulation.
     *
     * @param args Command line arguments. args[0] is the full path of the file to be used as input.
     * @side-effect Can raise a run time exception.
     */
    public static void main(String[] args) {
        String filename = new File(args[0]).getAbsolutePath();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.init();
        transactionManager.simulate(filename);

    }

}
