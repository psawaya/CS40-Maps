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
        int minSize = 1;
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
}
