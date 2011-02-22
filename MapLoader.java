import java.io.*;

public class MapLoader {
    
    static MakeTree tree;
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
           System.out.println("USAGE: java MapReader <file>.co");
            return;
        }
        
        MapLoader mapLoader = new MapLoader();
        
        try {
            mapLoader.loadMap(args[0]);   
        }
        catch (IOException e) {
            System.out.println ("Couldn't load file.");
            return;
        }
        
		MapSearcher mapSearcher = new MapSearcher(mapLoader.getTree());
		mapSearcher.startVisualizer();
    }
    
    MakeTree getTree() {
        return tree;
    }
    
    void loadMap(String filename) throws IOException {
        tree = new MakeTree();
        tree.loadTreeFromBinary(filename);
    }
}
