package IOManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class IOManager {
    private BufferedReader reader;

    public IOManager(String filename){
        try{
            reader = new BufferedReader(new FileReader(filename));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine(){
        String line = "";
        try{
            line = reader.readLine();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
