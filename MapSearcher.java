import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import visualizer.*;

public class MapSearcher extends MapExtractor{
    MakeTree map;
    
    public MapSearcher (MakeTree map_) {
        map = map_;
    }

	public LinkedList<GEdge> getEdges(int maxy, int miny, int maxx, int minx){

//		System.out.println("maxx"+maxx+ "   minx"+minx+ "   maxy"+maxy+ "  miny"+miny);
		List<Vertex> vertexList = findInMap(minx,miny,maxx-minx,maxy-miny);
		
		LinkedList<GEdge> edgeList= new LinkedList<GEdge>();
//		System.out.println(vertexList.size());
		
		for (Vertex v: vertexList ){
			for (Edge e: v.edges){
				if (e==null) continue;
				Vertex w=map.get(e.vertexIdx);
				if (v.id < w.id)
					edgeList.add(new GEdge(v.y,v.x,w.y,w.x));
			}			
		}
		return edgeList;
	}

	List<Vertex> findInMap(int x, int y, int width, int height) {
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        
        return findInRect(new MapRect(x,y,width,height), vertexList);
     
    }
    
    List<Vertex> findInRect(MapRect rect, List<Vertex> vertexList) {
        // Vertex[] vertices = map.getVertexArray();
        
        addMatchingVertices(rect, 0, map.getNumVertices(), true, vertexList);

        return vertexList;
    }

    void addMatchingVertices(MapRect rect, int start, int end, boolean xAxis, List<Vertex> vertexList) {
        int medianIdx = (start+end)/2;
        Vertex medianVertex = map.get(medianIdx);
        
        if (end - start <= 1) {
            vertexList.add(medianVertex);
            return;
        }
        
        VertexComparator comparator = new VertexComparator(xAxis);
        
        if (comparator.compare(medianVertex, rect) <= 0)
            addMatchingVertices(rect, medianIdx, end, !xAxis, vertexList);
        
        if (comparator.compare(medianVertex, rect) >= 0)
            addMatchingVertices(rect, start, medianIdx, !xAxis, vertexList);
    }
}
