import java.util.Comparator;

class MapRect {
    int x;
    int y;
    int width;
    int height;
    public MapRect(int x_, int y_, int width_, int height_) {
        x = x_;
        y = y_;
        width = width_;
        height = height_;
    }
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
    public int compare(Vertex vertex, MapRect rect) {
        if (sortOnX) {
            if (vertex.x > rect.x) {
                if (vertex.x <= rect.x+rect.width)
                    return 0;
                
                return 1;
            }
            
            return -1;
        }
        else {
            if (vertex.y > rect.y) {
                if (vertex.y <= rect.y+rect.height)
                    return 0;
                
                return 1;
            }
            
            return -1;
        }
    }
}