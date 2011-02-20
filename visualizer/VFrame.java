package visualizer;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class VFrame extends JFrame {

    private VPanel panel;

    public VFrame(MapExtractor m) throws IOException {
	super ("Router Finder");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	//	setContentPane(panel = new VPanel(in, out, 750, 600));
	setContentPane(panel = new VPanel(600, 500, m));
	pack();
	setVisible(true);
    }
}
