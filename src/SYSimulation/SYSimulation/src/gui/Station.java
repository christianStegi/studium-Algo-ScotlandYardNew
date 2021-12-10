package gui;

import java.awt.Color;
import java.awt.Point;

public class Station implements MapEntry {
	private Point pos = new Point();
	private Color color;
	
	public Station(Point pos, Color color) {
		this.pos = pos;
		this.color = color;
	}
	
	public Station(Point pos) {
		this.pos = pos;
		this.color = Color.BLACK;
	}
	
	public Station(int x, int y, Color color) {
		this.pos.x = x;
		this.pos.y = y;
		this.color = color;
	}
	
	public Station(int x, int y) {
		this.pos.x = x;
		this.pos.y = y;
		this.color = Color.BLACK;
	}
	
	public Point getPos() {
		return this.pos;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String type() {
		return "station";
	}
}
