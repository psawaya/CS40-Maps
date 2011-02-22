import java.io.*;

public class MapLoader {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
           System.out.println("USAGE: java MapReader <file>.co");
            return;
        }
        
        Tree tree;
        try {
            tree = new Tree(args[0]);
        }
        catch (IOException e) {
            System.out.println ("Couldn't load file.");
            return;
        }
        
		MapSearcher mapSearcher = new MapSearcher(tree);
		mapSearcher.startVisualizer();
    }
}
