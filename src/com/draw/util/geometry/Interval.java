package com.draw.util.geometry;

public class Interval {
	
	public final int start;
	
	public final int end;
	
	public final boolean leftInclusive;
	
	public final boolean rightInclusive;
	
	public Interval(int start, int end, boolean leftInclusive, boolean rightInclusive) {
		this.start = start;
		this.end = end;
		this.leftInclusive = leftInclusive;
		this.rightInclusive = rightInclusive;
	}
	
	public boolean fits(int x) {
		boolean b1, b2;	
		
		if (leftInclusive)
			b1 = start <= x;
		else
			b1 = start < x;
			
		if (rightInclusive)
			b2 = x <= end;
		else
			b2 = x < end;
		
		return b1 && b2;
	}
	
	public boolean liesRightOf(int x) {
		if (leftInclusive)
			return x < start;
		else
			return x <= start;
	}
	
	public boolean liesLeftOf(int x) {
		if (rightInclusive)
			return x > end;
		else
			return x >= end;
	}
	
	@Override
	public String toString() {
		return "start: " + start + ", end: " + end;
	}
	
}
