package gui;

import java.awt.Color;

public class Route implements MapEntry {
	private Station start;
	private Station stop;
	private Color color;
	
	public Route (Station start, Station stop, Color color) {
		this.start = start;
		this.stop = stop;
		this.color = color;
	}
	
	public Route (Station start, Station stop) {
		this.start = start;
		this.stop = stop;
		this.color = Color.BLACK;
	}
	
	public Station start() {
		return this.start;
	}
	
	public Station stop() {
		return this.stop;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String type() {
		return "route";
	}
	
}
