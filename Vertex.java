import java.io.*;

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
        out.writeInt(id);
        out.writeInt(x);
        out.writeInt(y);
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
