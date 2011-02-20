package visualizer;

class VEdge {

    int vx, vy, wx, wy;

    VEdge(int vx, int vy, int wx, int wy) {
	this.vx = vx;
	this.vy = vy;
	this.wx = wx;
	this.wy = wy;
    }
    public String toString() {
	return "VEdge: " + vx + " " + vy + "   " + wx + " " + wy;
    }
}
