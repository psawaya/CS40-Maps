package visualizer;

import java.util.LinkedList;

public abstract class MapExtractor {

    public abstract LinkedList<GEdge> getEdges(int maxlat, int minlat, int maxlon, int minlon);
    
    public void startVisualizer() {
	try {
	    new VFrame(this);
	}
	catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}

