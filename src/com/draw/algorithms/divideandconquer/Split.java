package com.draw.algorithms.divideandconquer;

import com.draw.util.geometry.Point;

public class Split {

	public Point top;
	
	public Point bottom;
	
	public Split(Point top, Point bottom) {
		this.top = top;
		this.bottom = bottom;
	}
	
	@Override
	public String toString() {
		return "top: " + top + ", bottom: " + bottom;
	}
	
}
