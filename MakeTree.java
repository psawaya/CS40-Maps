import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

class Vertex {
    public int id;
    public int x;
    public int y;
    public Edge[] edges;
    public String toString() {
        return x + ", "+ y;
    }
    public int edgeCount;
    static int maxEdges = 10;
    public Vertex(int id_, int x_, int y_) {
        id = id_;
        x = x_;
        y = y_;
        edgeCount = 0;
        edges = new Edge[maxEdges]; //No vertex should have over 10 edges.
    }
    public void write(DataOutputStream out) throws IOException {
        out.write(id);
        out.write(x);
        out.write(y);
        for (int i = 0; i < maxEdges; i++) {
            if (edges[i] != null)
                edges[i].write(out);
            else
                Edge.writeNull(out);
        }
    }
    public void addEdge(int to, int weight) {
        edges[edgeCount++] = new Edge(to, weight);
        assert edgeCount <= maxEdges;
    }
}

class Edge {
    int distance;
    int vertexIdx;
    public Edge(int vertexIdx_, int distance_) {
        vertexIdx = vertexIdx_;
        distance = distance_;
    }
    public void write(DataOutputStream out) throws IOException  {
        out.write(distance);
        out.write(vertexIdx);
    }
    public static void writeNull(DataOutputStream out) throws IOException {
        out.write(-1);
        out.write(-1);
    }
}

class MedianSplitter {
    static VertexComparator comparator;
    static int targetpos;
    public static void split(Vertex[] arr, boolean splitOnX) {
        split(arr, 0, arr.length, splitOnX);
    }
    public static void split(Vertex[] arr, int leftpos, int rightpos, boolean splitOnX) {
        comparator = new VertexComparator(splitOnX);
        targetpos = (leftpos+rightpos)/2;
        split(arr, leftpos, rightpos);
    }
    static void split(Vertex[] arr, int leftpos, int rightpos) {
        int midpt = (leftpos+rightpos)/2;
        Vertex pivot = arr[midpt];
        arr[midpt] = arr[rightpos-1];
        int i = leftpos;
        int j = rightpos - 1;
        // ensure all values in the array are placed in position < i are <= pivot,
        // and those that are placed in position > j are > pivot.
        for (; i < j; i++) {
            if (comparator.compare(arr[i], pivot) > 0) {
                boolean terminate = false;
                while (comparator.compare(arr[j-1], pivot) > 0) {
                    j--;
                    if (i == j) {
                        terminate = true;
                        break;
                    }
                }
                if (terminate)
                    break;

                Vertex tmp = arr[j-1];
                arr[j-1] = arr[i];
                arr[i] = tmp;
            }
        }

        arr[rightpos-1] = arr[i];
        arr[i] = pivot;

        if (i != targetpos) { // have we found the median?
            if (i > targetpos) { // if not, exclude it and recursively search the rest
                split(arr, leftpos, i);
            } else {
                split(arr, i+1, rightpos);
            }
        }
    }
    public static void naiveSplit(Vertex[] vertices, boolean splitOnX) {
        naiveSplit(vertices, 0, vertices.length, splitOnX);
    }
    public static void naiveSplit(Vertex[] vertices, int left, int right, boolean splitOnX) {
        Comparator<Vertex> vertexComparator = new VertexComparator(splitOnX);
        Arrays.sort(vertices, left, right, vertexComparator);
    }
}

public class MakeTree {
    public final static int minSize = 1;
    Vertex[] vertices;
    
    public void createVertexArray(int size){
        vertices = new Vertex[size];
    }
    
    public Vertex[] getVertexArray() {
        return vertices;
    }
    
    public void buildTree() {
        treeify(vertices);
    }

    static void treeify(Vertex[] vertices) {
        treeify(vertices, true, 0, vertices.length);
    }
    static void treeify(Vertex[] vertices, boolean useXaxis, int left, int right) {
        MedianSplitter splitter = new MedianSplitter();
        if (right - left > minSize) {
            splitter.split(vertices, left, right, useXaxis);
            int midpt = (right+left)/2;
            treeify(vertices, !useXaxis, left, midpt);
            treeify(vertices, !useXaxis, midpt, right);
        }
    }
    static int[] generateVertexMap(Vertex[] vertices) {
        int[] map = new int[vertices.length];
        for (int i=0; i < vertices.length; i++)  {
            map[vertices[i].id] = i;
        }
        return map;
    }

    MappedByteBuffer map;
    public void LoadTreeFromBinary(String[] args) throws IOException {
        FileChannel channel = new RandomAccessFile(args[0] + ".bin", "r").getChannel();
        map = channel.map(FileChannel.MapMode.READ_ONLY, 0, (int)channel.size());
    }
    public Vertex get(int n) {
        int startpos = n*23; // TODO: no magic constants!
        int id = map.get(startpos);
        int x  = map.get(startpos+1);
        int y = map.get(startpos+2);
        Vertex res = new Vertex(id, x, y);
        for (int i=0; i<10; i++) {
            res.edges[i].distance = map.get(startpos+3+i*2);
            res.edges[i].vertexIdx = map.get(startpos+3+i*2+1);
        }
        return res;
    }
}
