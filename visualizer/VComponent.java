package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

class VComponent extends JComponent {

    private VViewport viewport;
    private VComponent theCanvas;
    private VPanel thePanel;

    private int windowWidth, windowHeight;           // for window
    private int blockWidth, blockHeight;
    private double xfactor, yfactor;
    private double latlonratio = 1.5;
    private int canvasCenter = 1000000000;
    Rectangle visibleRect;

    private int centerLat, centerLon;   // center point for this zoom level
    private LinkedList<Block> blocks = new LinkedList<Block>();
    private MapExtractor level;

    private static class Block {
	Rectangle rect;
	boolean ready;
	LinkedList<VEdge> edges;

	Block (Rectangle r) {
	    rect = r;
	}
    }

    VComponent (VViewport v, MapExtractor m) throws IOException {

	level = m;
	viewport = v;
	theCanvas = this;
	setOpaque(true);

	setPreferredSize(new Dimension(2*canvasCenter, 2*canvasCenter));
	windowWidth = viewport.width;
	windowHeight = viewport.height;
	blockWidth = windowWidth/3;
	blockHeight = windowHeight/3;

	thePanel = viewport.thePanel;

	//	zoomLevel = 0;
	//	zoom = 1.0;
	xfactor = .002;
	yfactor = xfactor*latlonratio;
	
	centerLat = 42380280;
	centerLon = -72523610;
	int centerX   = getX(centerLon);
	int centerY   = getY(centerLat);
	visibleRect = new Rectangle(centerX-windowWidth/2,
					      centerY-windowHeight/2,
					      windowWidth, windowHeight);
	//	System.out.println ("visible rect is " + visibleRect);
    }

    /*
	void handleResponse(DataInputStream in) throws IOException {
	    //	    System.out.println ("Receiving response");
	    LinkedList<VEdge> list = new LinkedList<VEdge>();
	    int count = in.readInt();
	    //	    System.out.println ("   count is " + count);

	    for (int i=0; i<count; ++i) {
		int vx = in.readInt();
		int vy = in.readInt();
		int wx = in.readInt();
		int wy = in.readInt();
		list.add(new VEdge(vx, vy, wx, wy));
	    }

	    if (waitingForReset) return;

	    synchronized(blocks) {
		for (Block b : blocks) {
		    if (b.rect.x == x && b.rect.y == y) {
			b.ready = true;
			b.edges = list;
			//			System.out.println ("Got block " + x + " " + y);
			repaint(); //repaint(b.rect);
			return;
		    }
		}
		//		System.out.println ("Received unneeded block");
	    }
	    //	    System.out.println ("Got block " + x + " " + y);
	}
	} */
	
    public void paintComponent(Graphics g) {
	Rectangle clip = g.getClipBounds(new Rectangle());
	int xlim = clip.x + clip.width;
	int ylim = clip.y + clip.height;

	//	System.out.println ("Clip rect is " + clip);

	g.setColor(Color.WHITE);
	g.fillRect(clip.x, clip.y, clip.width, clip.height);
	g.setColor(Color.BLACK);

	getNeededBlocks(clip);

	synchronized(blocks) {
	    for (Block b : blocks)
		if (b.ready && b.rect.intersects(clip))
		    for (VEdge e : b.edges) {
			g.drawLine(e.vx, e.vy, e.wx, e.wy);
			//			g.drawRect(e.vx, e.vy, 1, 1);
			//			g.drawRect(e.wx, e.wy, 1, 1);
		    }
	}
    }

    private void getNeededBlocks(Rectangle clip) {

	//	System.out.println ("Clipping rectangle is " + clip);
	//	System.out.println ("Visible rectange is " + viewport.getViewRect());
	int x1 = clip.x/blockWidth-1;
	int x2 = (clip.x + clip.width + 3*blockWidth/2) / blockWidth;
	int y1 = clip.y/blockHeight-1;
	int y2 = (clip.y + clip.height + 3*blockHeight/2) / blockHeight;
	
	x1 *= blockWidth;
	x2 = x2*blockWidth - x1;

     	y1 *= blockHeight;
	y2 = y2*blockHeight - y1;
	
	Rectangle r = new Rectangle(x1, y1, x2, y2);
	//	System.out.println ("Needed rectangle is " + r);

	Rectangle visibleRect = viewport.getViewRect();

	synchronized (blocks) {
	    Iterator<Block> it = blocks.iterator();
	    while (it.hasNext()) {
		Block b = it.next();
		if ((b.rect.x < visibleRect.x - 2*windowWidth) ||
		    (b.rect.x > visibleRect.x + 3*windowWidth) ||
		    (b.rect.y < visibleRect.y - 2*windowHeight) ||
		    (b.rect.y > visibleRect.y + 3*windowHeight)) {
		    //		    System.out.println ("Deleting block with rectangle " + b.rect);
		    it.remove();
		}
	    }
	}

	synchronized (blocks) {
	    for (int x = r.x; x < r.x + r.width; x += blockWidth) {
		yloop: for (int y = r.y; y < r.y + r.height; y += blockHeight) {
		    Rectangle q = new Rectangle(x, y, blockWidth, blockHeight);
		    for (Block b : blocks)
			if (b.rect.equals(q)) continue yloop;
		    
		    int maxLon = getLon(x+blockWidth);
		    int minLon = getLon(x);
		    int maxLat = getLat(y);
		    int minLat = getLat(y+blockHeight);

		    //		    System.out.println (maxLon + " " + minLon + " " + maxLat + " " + minLat);
		    Block b = new Block(q);
		    LinkedList<GEdge> l1 = level.getEdges(maxLat, minLat, maxLon, minLon);
		    LinkedList<VEdge> l2 = new LinkedList<VEdge>();
		    //		    for (GEdge e : l1)
		    //			System.out.println(e);
		    for (GEdge e : l1) 
			l2.add(new VEdge(getX(e.vlon), getY(e.vlat),
					 getX(e.wlon), getY(e.wlat)));
		    //		    for (VEdge e : l2)
		    //			System.out.println(e);
		    b.edges = l2;
		    b.ready = true;
		    blocks.add(b);
		}
	    }
	}
    }

    int getX (int lon) {
	return (int)((lon-centerLon)*xfactor) + canvasCenter;
    }

    int getY (int lat) {
	return (int)((centerLat-lat)*yfactor) + canvasCenter;
    }

    int getLon (int x) {
	x -= canvasCenter;
	return (int)(x/xfactor) + centerLon;
    }

    int getLat(int y) {
	y -= canvasCenter;
	return centerLat - (int)(y/yfactor);
    }
    
    void handleClick(String mode, int x, int y) {}

    void doZoom (int delta) {
	Rectangle visibleRect = viewport.getViewRect();
	int centerX = visibleRect.x + visibleRect.width/2;
	int centerY = visibleRect.y + visibleRect.height/2;
	int centerLon = getLon(centerX);
	int centerLat = getLat(centerY);

	if (delta == 1) xfactor *= 2;
	else xfactor /= 2;
	yfactor = xfactor * latlonratio;

	centerX   = getX(centerLon);
	centerY   = getY(centerLat);
	visibleRect = new Rectangle(centerX-windowWidth/2,
					      centerY-windowHeight/2,
					      windowWidth, windowHeight);
	blocks.clear();
	scrollRectToVisible(visibleRect);
	repaint();
    }


    void computePath() {}
    
    void clearRoute() {}

    void doDetail(int delta) {}

}


    
