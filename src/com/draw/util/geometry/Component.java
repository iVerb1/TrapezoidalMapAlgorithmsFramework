package com.draw.util.geometry;

public class Component {
	
	private Segment top;
	
	private Segment bottom;
	
	private Point lastPoint;
	
	public Component(Segment top, Segment bottom, Point lastPoint) {
		this.top = top;
		this.bottom = bottom;
		this.lastPoint = lastPoint;
	}
	
	public void setBottom(Segment s) {
		lastPoint = s.leftPoint();
		bottom = s;		
	}
	
	public void setTop(Segment s) {
		lastPoint = s.leftPoint();
		top = s;	
	}
	
	public Segment getTop() {
		return top;
	}
	
	public Segment getBottom() {
		return bottom;
	}
	
	public Point getLastPoint() {
		return lastPoint;
	}
	
	@Override
	public String toString() {
		return "[top: " + top + ", bottom: " + bottom + ", lastPoint: " + lastPoint + "]";
	}
}
