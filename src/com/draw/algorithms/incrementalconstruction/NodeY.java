package com.draw.algorithms.incrementalconstruction;

import java.util.Set;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class NodeY extends Node {

	public Node childTop = null;
	public Node childBottom = null;
	
	private Segment segment;
	private boolean nextChildTop = true;
	
	public NodeY(Set<Node> parents, Segment segment) {
		super(parents);
		this.segment = segment;
	}
	
	public NodeY(Node parent, Segment segment) {
		super(parent);
		this.segment = segment;
	}
	
	@Override
	public Node getNextNode(Segment s_i, Point p) {
		if (this.segment.isAbove(p, true))
			return this.childBottom;
		else if (this.segment.isBelow(p, true))
			return this.childTop;
		else {
			// " ... whenever p lies on a segment s of a y-node [...] we compare the slopes of s and s_i;
			// if the slope of s_i is larger, we decide that p lies above s, otherwise we decide that is below s."
			
			Segment s = this.segment;
			boolean slopeS_iLarger;
			
			
			double slopeS = (double)(s.rightPoint().y - s.leftPoint().y) / (double)(s.rightPoint().x - s.leftPoint().x);
			double slopeS_i = (double)(s_i.rightPoint().y - s_i.leftPoint().y) / (double)(s_i.rightPoint().x - s_i.leftPoint().x);
			
			if (slopeS > slopeS_i)
				slopeS_iLarger = false;
			else if (slopeS < slopeS_i)
				slopeS_iLarger = true;
			else
				throw new RuntimeException("Degenerate case");
			
			if (slopeS_iLarger)
				return this.childTop;
			else
				return this.childBottom;
		}
	}
	
	@Override
	protected void setChild(Node oldChild, Node newChild) {
		if (oldChild == this.childTop)
			this.childTop = newChild;
		else if (oldChild == this.childBottom)
			this.childBottom = newChild;
		else
			throw new RuntimeException("invalid child calling");
	}

	@Override
	public int getRootedNodesAmount() {
		int amount = 1;
		
		if (this.childTop != null)
			amount += this.childTop.getRootedNodesAmount();
		
		if (this.childBottom != null)
			amount += this.childBottom.getRootedNodesAmount();
		
		return amount;
	}
	
	@Override
	public void print() {
		System.out.println("y " + this.id);
		System.out.println(this.segment.toString());
		System.out.println("top");
		if (this.childTop != null) {
			this.childTop.print();
		} else {
			System.out.println("null");
		}
		System.out.println(this.id + " bottom");
		if (this.childBottom != null) {
			this.childBottom.print();
		} else {
			System.out.println("null");
		}
	}
	
	public void setNextChildTop() {
		this.nextChildTop = true;
	}
	
	public void setNextChildBottom() {
		this.nextChildTop = false;
	}
	
	public void setChild(Node child) {
		if (this.nextChildTop){
			this.childTop = child;
		} else {
			this.childBottom = child;
		}
	}

	public void setChildBottom(NodeLeaf leaf) {
		this.childBottom = leaf;
		leaf.parents.add(this);
	}

	public void createChildBottom(TrapezoidalMapElement bottom) {
		NodeLeaf leaf = new NodeLeaf(this, bottom);
		this.childBottom = leaf;
	}

	public void createChildTop(TrapezoidalMapElement top) {
		NodeLeaf leaf = new NodeLeaf(this, top);
		this.childTop = leaf;
	}

	public void setChildTop(NodeLeaf leaf) {
		this.childTop = leaf;
		leaf.parents.add(this);
	}
	
	@Override
	public int getRootedDepth() {
		int depth = 1;
		int depthTop = 0;
		int depthBottom = 0;
		
		if (this.childTop != null)
			depthTop = this.childTop.getRootedDepth();
		
		if (this.childBottom != null)
			depthBottom = this.childBottom.getRootedDepth();
		
		return depth + Math.max(depthTop, depthBottom);
	}
}
