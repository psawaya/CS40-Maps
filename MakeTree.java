import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

class Vertex {
    public int id;
    public int x;
    public int y;
    public Edge[] edges;
    public Vertex(int id_, int x_, int y_) {
        id = id_;
        x = x_;
        y = y_;
        edges = new Edge[10]; //No vertex should have over 10 edges.
    }
}

class Edge {
    int distance;
    int vertexIdx;
}

class VertexComparator implements Comparator<Vertex> {
    boolean sortOnX;
    public VertexComparator(boolean sortOnX_) {
        sortOnX = sortOnX_;
    }
    public int compare(Vertex a, Vertex b) {
        if (sortOnX)
            return a.x - b.x;
        else
            return a.y - b.y;
    }
}

public class MakeTree {
    public static int getVertexCount(Scanner s) {
        int size = -1;
        while (s.hasNext()) {
            if (s.next().equals("p") ) {
                s.next();
                s.next();
                s.next();
                size = s.nextInt();
                break;
            }
            s.nextLine();
        }
        return size;
    }
    public static Vertex[] loadVertices(String fname) throws IOException {
        Scanner s = new Scanner(new BufferedInputStream(new FileInputStream(fname + ".co"), 1<<24));
        Vertex[] vertices = new Vertex[getVertexCount(s)];
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
        return vertices;
    }

    static VertexComparator comparator;
    static int targetpos;
    public static void splitOnMedian(Vertex[] arr, boolean splitOnX) {
        splitOnMedian(arr, 0, arr.length, splitOnX);
    }
    public static void splitOnMedian(Vertex[] arr, int leftpos, int rightpos, boolean splitOnX) {
        comparator = new VertexComparator(splitOnX);
        targetpos = (leftpos+rightpos)/2;
        splitOnMedian(arr, leftpos, rightpos);
    }
    public static void splitOnMedian(Vertex[] arr, int leftpos, int rightpos) {
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
                splitOnMedian(arr, leftpos, i);
            } else {
                splitOnMedian(arr, i+1, rightpos);
            }
        }
    }
    public static void naiveSplitOnMedian(Vertex[] vertices, boolean splitOnX) {
        naiveSplitOnMedian(vertices, 0, vertices.length, splitOnX);
    }
    public static void naiveSplitOnMedian(Vertex[] vertices, int left, int right, boolean splitOnX) {
        Comparator<Vertex> vertexComparator = new VertexComparator(splitOnX);
        Arrays.sort(vertices, left, right, vertexComparator);
    }
    public static void treeify(Vertex[] vertices) {
        treeify(vertices, true, 0, vertices.length);
    }
    public static void treeify(Vertex[] vertices, boolean useXaxis, int left, int right) {
        int minSize = 1;
        if (right - left > minSize) {
            splitOnMedian(vertices, left, right, useXaxis);
            int midpt = (right+left)/2;
            treeify(vertices, !useXaxis, left, midpt);
            treeify(vertices, !useXaxis, midpt, right);
        }
    }
    public static int[] generateVertexMap(Vertex[] vertices) {
        int[] map = new int[vertices.length];
        for (int i=0; i < vertices.length; i++)  {
            map[vertices[i].id] = i;
        }
        return map;
    }
    public static void main (String[] args) throws IOException {
        Vertex[] vertices = loadVertices(args[0]);
        treeify(vertices);
        //for (Vertex v : vertices) System.out.println(v.id + " " + v.x + " " + v.y);
    }
}
