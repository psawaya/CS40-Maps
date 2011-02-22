import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

public class TreeBuilder {
    Vertex[] vertices;

    public TreeBuilder(String filename) throws IOException {
        parseVertexFile(filename + ".co");    
        parseArcFile(filename + ".gr");    

        treeify();
        remapIdsToAddresses(vertices);
    }

    void treeify() {
        treeify(true, 0, vertices.length);
    }

    void treeify(boolean useXaxis, int left, int right) {
        MedianSplitter splitter = new MedianSplitter();
        if (right - left > 1) {
            splitter.split(vertices, left, right, useXaxis);
            int midpt = (right+left)/2;
            treeify(!useXaxis, left, midpt);
            treeify(!useXaxis, midpt, right);
        }
    }

    static void remapIdsToAddresses(Vertex[] vertices) {
        int[] map = new int[vertices.length];
        for (int i=0; i < vertices.length; i++)  {
            map[vertices[i].id] = i;
        }
        for (int i=0; i < vertices.length; i++)
            for (int j=0; j<vertices[i].edgeCount; j++)
                vertices[i].edges[j].vertexIdx = map[vertices[i].edges[j].vertexIdx];
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
        
                vertices = new Vertex[size];
                break;
            }
            s.nextLine();
        }
        
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
    
    public void write(DataOutputStream out) throws IOException {
        for (int i=0; i < vertices.length; i++) {
            vertices[i].write(out);
        }
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
