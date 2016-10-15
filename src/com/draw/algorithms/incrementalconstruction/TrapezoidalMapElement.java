package com.draw.algorithms.incrementalconstruction;

import com.draw.util.geometry.Trapezoid;

// if an instance of this class is a null object it is meant to be the trapezoid for the bounding rectangle
public class TrapezoidalMapElement {
	
	public static int idGen = 0;
	public int id;
	
	private Trapezoid trapezoid;
	private NodeLeaf leaf = null;
	private TrapezoidalMapElement neighborLeftTop = null;
	private TrapezoidalMapElement neighborLeftBottom = null;
	private TrapezoidalMapElement neighborRightTop = null;
	private TrapezoidalMapElement neighborRightBottom = null;
	
	public TrapezoidalMapElement(Trapezoid trapezoid) {
		this.trapezoid = trapezoid;
		this.id = TrapezoidalMapElement.idGen++;
		
		//System.out.println("new map element: " + this.id);
		//System.out.println(this.trapezoid.toString());
	}

	public void print() {
		System.out.println("TME " + this.id);
		System.out.println(this.trapezoid.toString());
		System.out.println("left top: " + (this.neighborLeftTop == null ? "null" : this.neighborLeftTop.id));
		System.out.println("left bottom: " + (this.neighborLeftBottom == null ? "null" : this.neighborLeftBottom.id));
		System.out.println("right top: " + (this.neighborRightTop == null ? "null" : this.neighborRightTop.id));
		System.out.println("right bottom: " + (this.neighborRightBottom == null ? "null" : this.neighborRightBottom.id));
	}
	
	public Trapezoid getTrapezoid() {
		return this.trapezoid;
	}
	
	public NodeLeaf getLeaf() {
		return this.leaf;
	}
	
	public void setLeaf(NodeLeaf leaf) {
		this.leaf = leaf;
	}
	
	public TrapezoidalMapElement getNeighborLeftTop() {
		return neighborLeftTop;
	}

	public TrapezoidalMapElement getNeighborLeftBottom() {
		return neighborLeftBottom;
	}

	public TrapezoidalMapElement getNeighborRightTop() {
		return neighborRightTop;
	}

	public TrapezoidalMapElement getNeighborRightBottom() {
		return neighborRightBottom;
	}

	public void setNeighborLeftTop(TrapezoidalMapElement neighborLeftTop) {
		this.neighborLeftTop = neighborLeftTop;
	}

	public void setNeighborLeftBottom(TrapezoidalMapElement neighborLeftBottom) {
		this.neighborLeftBottom = neighborLeftBottom;
	}

	public void setNeighborRightTop(TrapezoidalMapElement neighborRightTop) {
		this.neighborRightTop = neighborRightTop;
	}

	public void setNeighborRightBottom(TrapezoidalMapElement neighborRightBottom) {
		this.neighborRightBottom = neighborRightBottom;
	}
	
	public void giveLeftTopTo(TrapezoidalMapElement t) {
		t.setNeighborLeftTop(neighborLeftTop);
		
		if (this.neighborLeftTop != null) {
			if (this.neighborLeftTop.getNeighborRightTop() == this) {
				this.neighborLeftTop.setNeighborRightTop(t);
			}
			
			if (this.neighborLeftTop.getNeighborRightBottom() == this) {
				this.neighborLeftTop.setNeighborRightBottom(t);
			}
		}
	}
	
	public void giveLeftBottomTo(TrapezoidalMapElement t) {
		t.setNeighborLeftBottom(neighborLeftBottom);
		
		if (this.neighborLeftBottom != null) {
			if (this.neighborLeftBottom.getNeighborRightTop() == this) {
				this.neighborLeftBottom.setNeighborRightTop(t);
			}
			
			if (this.neighborLeftBottom.getNeighborRightBottom() == this) {
				this.neighborLeftBottom.setNeighborRightBottom(t);
			}
		}
	}
	
	public void giveRightTopTo(TrapezoidalMapElement t) {
		t.setNeighborRightTop(neighborRightTop);
		
		if (this.neighborRightTop != null) {
			if (this.neighborRightTop.getNeighborLeftTop() == this) {
				this.neighborRightTop.setNeighborLeftTop(t);
			}
			
			if (this.neighborRightTop.getNeighborLeftBottom() == this) {
				this.neighborRightTop.setNeighborLeftBottom(t);
			}
		}
	}
	
	public void giveRightBottomTo(TrapezoidalMapElement t) {
		t.setNeighborRightBottom(neighborRightBottom);
		
		if (this.neighborRightBottom != null) {
			if (this.neighborRightBottom.getNeighborLeftTop() == this) {
				this.neighborRightBottom.setNeighborLeftTop(t);
			}
			
			if (this.neighborRightBottom.getNeighborLeftBottom() == this) {
				this.neighborRightBottom.setNeighborLeftBottom(t);
			}
		}
	}
	
	
}
