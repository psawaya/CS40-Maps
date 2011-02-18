import java.io.*;
import java.util.Scanner;

public class MapReader {
    
    static MakeTree tree;
    
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("USAGE: java MapReader <file>.co");
            return;
        }
        
        MapReader mapReader = new MapReader();
        mapReader.readMap(args[0]);
        
        try {
            writeFile();
        }
        catch (FileNotFoundException e) {
            System.out.println ("File not found.");
            return;
        }

        MapSearcher mapSearcher = new MapSearcher(mapReader.getTree());
        System.out.println ("Found " + mapSearcher.findInMap(-73576169,41079696,200000,200000).size() + " vertices.");
    }
    
    MakeTree getTree() {
        return tree;
    }

    static void writeFile() throws IOException, FileNotFoundException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("tmp")));
        for (int i=0; i < tree.vertices.length; i++) {
            tree.vertices[i].write(out);
        }
    }
    
    void readMap(String filename) {
        tree = new MakeTree();
        
        try {
            parseVertexFile(filename + ".co");    
            parseArcFile(filename + ".gr");    
        }
        catch (IOException e) {
        }
        
        tree.buildTree();
    }
    
    void parseVertexFile(String filename) throws IOException {
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

    void parseArcFile(String filename) throws IOException {
        Scanner s = new Scanner(new BufferedInputStream(new FileInputStream(filename), 1<<24));
        Vertex[] vertices = tree.getVertexArray();
        while (s.hasNext()) {
            String t = s.next();
            if (!t.equals("a")) {
                s.nextLine();
                continue;
            }
            int v1 = s.nextInt() - 1;
            int v2 = s.nextInt() - 1;
            int weight = s.nextInt();
            vertices[v1].addEdge(v2, weight);
        }
    }
    
}
