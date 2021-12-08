/**
 * @author Aakash Bhattacharya
 * @version 1.0.0
 * @date 11/21/2021
 */
package IOManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IOManager {
    private BufferedReader reader;

    /**
     * Creates an instance of BufferedReader with the specified filename.
     *
     * @param filename Name of the file
     * @throws IOException when the specified file is not found
     */
    public IOManager(String filename) {
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a line and returns it.
     *
     * @return String of the line read
     * @throws IOException when the next line is not read
     */
    public String readLine() {
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
