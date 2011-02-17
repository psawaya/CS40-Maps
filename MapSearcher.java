import java.util.ArrayList;
import java.util.List;

public class MapSearcher {
    MakeTree map;
    
    public MapSearcher (MakeTree map_) {
        map = map_;
    }
    
    List<Vertex> findInMap(int x, int y, int width, int height) {
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        
        findInRect(new MapRect(x,y,width,height), vertexList);
        
        return vertexList;
    }
    
    List<Vertex> findInRect(MapRect rect, List<Vertex> vertexList) {
        Vertex[] vertices = map.getVertexArray();
        
        addMatchingVertices(rect, vertices, 0, vertices.length, true, vertexList);

        return vertexList;
    }

    void addMatchingVertices(MapRect rect, Vertex[] vertices, int start, int end, boolean xAxis, List<Vertex> vertexList) {
        int medianIdx = (start+end)/2;
        
        if (end - start <= 1) {
            vertexList.add(vertices[medianIdx]);
            return;
        }
        
        VertexComparator comparator = new VertexComparator(xAxis);
        
        if (comparator.compare(vertices[medianIdx], rect) <= 0)
            addMatchingVertices(rect, vertices, medianIdx, end, !xAxis, vertexList);
        
        if (comparator.compare(vertices[medianIdx], rect) >= 0)
            addMatchingVertices(rect, vertices, start, medianIdx, !xAxis, vertexList);
    }
}