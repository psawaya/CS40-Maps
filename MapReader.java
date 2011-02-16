import java.io.*;
import java.util.Scanner;

public class MapReader {
    
    MakeTree tree;
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("USAGE: java MapReader <file>.co");
            return;
        }
        
        MapReader mapReader = new MapReader();
        mapReader.readMap(args[0]);
    }
    
    void readMap(String filename) {
        tree = new MakeTree();
        
        try {
            parseFile(filename + ".co");    
        }
        catch (IOException e) {
            
        }
        
        tree.buildTree();
    }
    
    void parseFile(String filename) throws IOException {
        Scanner s = new Scanner(new BufferedInputStream(new FileInputStream(filename), 1<<24));
        
        //Get vertex count
        while (s.hasNext()) {
            if (s.next().equals("p") ) {
                int size;
                
                s.next();
                s.next();
                s.next();
                size = s.nextInt();
        
                tree.createVertexArray(size);
                break;
            }
            s.nextLine();
        }
        
        Vertex[] vertices = tree.getVertexArray();
        
        while (s.hasNext()) {
            String t = s.next();
            if (!t.equals("v")) {
                s.nextLine();
                continue;
            }
            int id = s.nextInt() - 1;
            int x = s.nextInt();
            int y = s.nextInt();
            vertices[id] = new Vertex(id, x, y);
        }    
    }
    
}