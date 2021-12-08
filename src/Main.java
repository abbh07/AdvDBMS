/**
 * @author Aakash Bhattacharya and Shobhit Sinha
 * @version 1.0.0
 * @date 11/21/2021
 */

import TransactionManager.TransactionManager;

import java.io.File;
import java.util.Scanner;

/**
 * Main class of the project.
 */
public class Main {

    /**
     * Main method of the whole codebase. Creates a TransactionManager object and initiates the simulation.
     * The filename is read from the command line input.
     *
     * @param args Command line arguments.
     * @side-effect Can raise a run time exceptions.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the complete input file path: ");
        String file = scanner.nextLine();
        String filename = new File(file).getAbsolutePath();

        // String filename = new File(args[0]).getAbsolutePath();

        TransactionManager transactionManager = new TransactionManager();
        transactionManager.init();
        transactionManager.simulate(filename);

    }

}
