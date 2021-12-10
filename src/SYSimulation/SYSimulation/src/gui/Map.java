package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Map extends JPanel {
	
	private BufferedImage bimage;
	private Dimension size;
	private Graphics2D g2d;
	private BasicStroke bs;
	private static final int brushSize = 3;
	
	public Map() throws IOException {
		size = new Dimension();
		
		// create a buffered image and copy image data from file to buffer
		bimage = ImageIO.read(new File(this.getClass().getResource("london.jpg").getPath()));
		g2d = bimage.createGraphics();
		size.width = bimage.getWidth(null);
		size.height = bimage.getHeight(null);
		
		setPreferredSize(size);
		setBackground(Color.black);
		bs = new BasicStroke(brushSize);
	}
	
	// callback for repaint event
	@Override
	public void paint(Graphics g) {
		g.drawImage(bimage, 0, 0, this.getWidth(), this.getHeight(), 0, 0, bimage.getWidth(), bimage.getHeight(), null);
	}
	
	// callback for reading the preferred window size
	@Override
	public Dimension getPreferredSize() {
		return size;
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, Color color) {
		g2d.setColor(color);
		g2d.setStroke(bs);
		g2d.drawLine(x1, y1, x2, y2);
		repaint();
	}
	
	public void drawCircle(int x, int y, Color color) {
		g2d.setColor(color);
		g2d.setStroke(bs);
		g2d.drawArc(x - 15, y -15, 30, 30, 0, 360);
		repaint();
	}
	
	public void clearMap() throws IOException {
		g2d.dispose();
		bimage = ImageIO.read(new File(this.getClass().getResource("london.jpg").getPath()));
		g2d = bimage.createGraphics();
		repaint();
	}
	
}
