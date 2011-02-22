import java.io.*;

class Edge {
    int distance;
    int vertexIdx;
    public Edge(int vertexIdx_, int distance_) {
        vertexIdx = vertexIdx_;
        distance = distance_;
    }
    public void write(DataOutputStream out) throws IOException  {
        out.writeInt(distance);
        out.writeInt(vertexIdx);
    }
    public static void writeNull(DataOutputStream out) throws IOException {
        out.writeInt(-1);
        out.writeInt(-1);
    }
}

