import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class Tree {
    IntBuffer map;
    int size;

    public Tree(String filename) throws IOException {
        FileChannel channel = new RandomAccessFile(filename + ".bin", "r").getChannel();
        map = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        size = (int)channel.size()/23/4;
    }
    
    public int getNumVertices(){
        return size;
    }
    
    public Vertex getVertexByID(int id) {
        if (id < 0 || id > size-1)
            return null;

        return get(map.get(id));
    }

    public Vertex get(int n) {
        int startpos = n*23; // TODO: no magic constants!
        int id = map.get(startpos);
        int x = map.get(startpos+1);
        int y = map.get(startpos+2);
                
        Vertex res = new Vertex(id, x, y);
        for (int i=0; i<Vertex.maxEdges; i++) {
            int distance = map.get(startpos+3+i*2);
            int vertexIdx = map.get(startpos+3+i*2+1);
            if (vertexIdx >= 0) {
                res.edges[res.edgeCount++] = new Edge(vertexIdx, distance);
            }
        }
        return res;
    }
}
