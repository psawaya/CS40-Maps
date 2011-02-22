import java.io.*;
import java.util.Scanner;

public class MapReader {
    
    static TreeBuilder treeBuilder;
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
           System.out.println("USAGE: java MapReader <file>.co");
            return;
        }
        
        try {
            treeBuilder = new TreeBuilder(args[0]);
        }
        catch (IOException e) {
            System.out.println ("Couldn't read file.");
            return;
        }
        
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(args[0] + ".bin")));
            treeBuilder.write(out);
            out.flush();
        }
        catch (FileNotFoundException e) {
            System.out.println ("Couldn't write file.");
            return;
        }

        System.out.println ("File successfully processed. You may run MapLoader now.");
    }
}
