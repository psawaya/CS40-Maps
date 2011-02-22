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
        
        try {
            mapReader.readMap(args[0]);   
        }
        catch (IOException e) {
            System.out.println ("Couldn't read file.");
            return;
        }
        
        try {
            writeFile(args[0]);
        }
        catch (FileNotFoundException e) {
            System.out.println ("Couldn't write file.");
            return;
        }

        System.out.println ("File successfully processed. You may run MapLoader now.");
    }
    
    MakeTree getTree() {
        return tree;
    }

    static void writeFile(String filename) throws IOException, FileNotFoundException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename + ".bin")));
        for (int i=0; i < tree.vertices.length; i++) {
            tree.vertices[i].write(out);
        }
        out.flush();
    }
    
    void readMap(String filename) throws IOException {
        tree = new MakeTree();
        
        parseVertexFile(filename + ".co");    
        parseArcFile(filename + ".gr");    
        
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
