package visualizer;

public class GEdge {

    public int vlat, vlon, wlat, wlon;

    public GEdge(int vlat, int vlon, int wlat, int wlon) {
	this.vlat = vlat;
	this.vlon = vlon;
	this.wlat = wlat;
	this.wlon = wlon;
    }
    
    public String toString() {
	return "GEdge: " + vlat + " " + vlon + "   " + wlat + " " + wlon;
    }
}
