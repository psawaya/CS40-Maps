package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;

public class VPanel extends JPanel implements ActionListener {

    private VViewport viewport;
    private JRadioButton zoomButton;
    private JPanel clickPanel;

    JButton inButton, outButton, goButton, clearButton;
    JButton moreButton, lessButton;
    String centerAndZoomStr = "center and zoom";
    String pickSourceStr = "choose starting location";
    String pickTargetStr = "choose destination";

    public VPanel(int width, int height, MapExtractor m) throws IOException {
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	setBorder(new EmptyBorder(50, 50, 50, 50));

	JPanel p = new JPanel();
	viewport = new VViewport(this, width, height, m);
	p.add(viewport);
	p.setBorder(new CompoundBorder
		    (new LineBorder(Color.BLACK, 3),
		     new LineBorder(Color.WHITE, 25)));
	p.setMaximumSize(p.getPreferredSize());
	p.setMinimumSize(p.getPreferredSize());
	
	add(p);
	add(Box.createHorizontalStrut(50));

	ButtonPanel buttonPanel = new ButtonPanel();
	add(buttonPanel);
	buttonPanel.add(Box.createGlue());

	inButton = new JButton ("In");
	inButton.addMouseListener (new MyMouseAdapter());
	inButton.setFocusable(false);
	outButton =  new JButton ("Out");
	outButton.addMouseListener (new MyMouseAdapter());
	outButton.setFocusable(false);
	buttonPanel.add(new RowPanel("Zoom:", inButton, outButton));
	buttonPanel.add(Box.createVerticalStrut(25));

	moreButton = new JButton ("More");
	moreButton.addMouseListener (new MyMouseAdapter());
	moreButton.setFocusable(false);
	moreButton.setEnabled(false);
	lessButton =  new JButton ("Less");
	lessButton.addMouseListener (new MyMouseAdapter());
	lessButton.setFocusable(false);
	lessButton.setEnabled(false);
	buttonPanel.add(new RowPanel("Map detail:", moreButton, lessButton));
	buttonPanel.add(Box.createVerticalStrut(25));

	ButtonGroup clickGroup = new ButtonGroup();
	clickPanel = new JPanel();
	clickPanel.setLayout(new BoxLayout(clickPanel,BoxLayout.Y_AXIS));
	clickPanel.setAlignmentX(0.0f);
	buttonPanel.add(clickPanel);
	clickPanel.add(new JLabel("Click on the map to:"));
	buttonPanel.add(Box.createVerticalStrut(25));

	zoomButton = new JRadioButton(centerAndZoomStr);
	zoomButton.setActionCommand(centerAndZoomStr);
	zoomButton.addActionListener(this);
	zoomButton.setFocusPainted(false);
	clickGroup.add(zoomButton);
	clickPanel.add(zoomButton);

	JRadioButton pickSource = new JRadioButton(pickSourceStr);
	pickSource.setActionCommand(pickSourceStr);
	pickSource.addActionListener(this);
	pickSource.setFocusPainted(false);
	clickGroup.add(pickSource);
	clickPanel.add(pickSource);
	
	JRadioButton pickTarget = new JRadioButton(pickTargetStr);
	pickTarget.setActionCommand(pickTargetStr);
	pickTarget.addActionListener(this);
	pickTarget.setFocusPainted(false);
	clickGroup.add(pickTarget);
	clickPanel.add(pickTarget);

	zoomButton.setSelected(true);
	viewport.setClickMode(centerAndZoomStr);

	goButton = new GoButton();
	buttonPanel.add(goButton);
	buttonPanel.add(Box.createVerticalStrut(25));

	clearButton = new ClearButton();
	buttonPanel.add(clearButton);
	buttonPanel.add(Box.createGlue());
    }

    public void setCenterAndZoomMode() {
	zoomButton.setSelected(true);
	viewport.setClickMode(centerAndZoomStr);
    }

    public void actionPerformed (ActionEvent e) {
	viewport.setClickMode(e.getActionCommand());
    }

    private class RowPanel extends JPanel {

	RowPanel(String s, JButton a, JButton b) {
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    add(new JLabel(s));
	    add(Box.createHorizontalStrut(10));
	    add(a);
	    add(Box.createHorizontalStrut(10));
	    add(b);
	    setAlignmentX(0.0f);
	    setMaximumSize(getPreferredSize());
	}
    }
	    
    private class ButtonPanel extends JPanel {

	ButtonPanel() {
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    setOpaque(true);
	}
    }

    private class MyMouseAdapter extends MouseAdapter {
	public void mouseClicked(MouseEvent e) {
	    if (((JButton)e.getSource()).isEnabled()) {
		if (e.getSource() == inButton)
		    viewport.doZoom(1);
		else if (e.getSource() == outButton)
		    viewport.doZoom(-1);
		else if (e.getSource() == moreButton)
		    viewport.doDetail(1);
		else if (e.getSource() == lessButton)
		    viewport.doDetail(-1);
	    }
	}
    }	

    private class GoButton extends JButton implements ActionListener {

	GoButton () {
	    super("Do search");
	    setFocusable(false);
	    setVisible(true);
	    setEnabled(false);
	    setAlignmentX(0.0f);
	    this.addActionListener(this);
	}
	
	public void actionPerformed (ActionEvent e) {
	    viewport.computePath();
	}
    }

    private class ClearButton extends JButton implements ActionListener {

	ClearButton () {
	    super("Clear Route");
	    setFocusable(false);
	    setVisible(true);
	    setEnabled(false);
	    setAlignmentX(0.0f);
	    this.addActionListener(this);
	}
	
	public void actionPerformed (ActionEvent e) {
	    viewport.clearRoute();
	}
    }
}
