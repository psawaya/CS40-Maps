package visualizer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class VViewport extends JViewport {

    int width;
    int height;

    VPanel thePanel;
    private VComponent theCanvas;
    private String clickMode;

    public VViewport (VPanel p, int w, int h, MapExtractor m) throws IOException {
	width = w;
	height = h;
	thePanel = p;
	setPreferredSize(new Dimension(width, height));
	setView(theCanvas = new VComponent(this, m));
	scrollRectToVisible(theCanvas.visibleRect);

	addMouseMotionListener(new MyMouseMotionListener());
	addMouseListener(new MyMouseListener());

	setMaximumSize(getPreferredSize());
	setMinimumSize(getPreferredSize());
    }

    private Rectangle mouseDownRect;
    private int mouseDownX, mouseDownY;

    private class MyMouseListener extends MouseAdapter {
	
	public void mousePressed(MouseEvent e) {
	    mouseDownRect = getViewRect();
	    mouseDownX = e.getX();
	    mouseDownY = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
	    theCanvas.handleClick(clickMode, 
				  mouseDownRect.x + mouseDownX,
				  mouseDownRect.y + mouseDownY);
	}
    }
    
    private class MyMouseMotionListener extends MouseMotionAdapter {
	
	public void mouseDragged(MouseEvent e) {
	    
	    Rectangle newRect = new Rectangle(mouseDownRect.x + mouseDownX - e.getX(),
					      mouseDownRect.y + mouseDownY - e.getY(),
					      mouseDownRect.width,
					      mouseDownRect.height);
	    theCanvas.scrollRectToVisible(newRect);
	}
    }

    void setClickMode(String clickMode) {
	this.clickMode = clickMode;
    }

    void doZoom(int delta) {
	theCanvas.doZoom(delta);
    }

    void doDetail(int delta) {
	theCanvas.doDetail(delta);
    }

    void computePath() {
	theCanvas.computePath();
    }

    void clearRoute() {
	theCanvas.clearRoute();
    }
}
