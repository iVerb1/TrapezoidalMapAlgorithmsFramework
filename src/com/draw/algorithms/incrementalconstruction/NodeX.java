package com.draw.algorithms.incrementalconstruction;

import java.util.Set;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class NodeX extends Node {
	
	public Node childLeft = null;
	public Node childRight = null;
	
	private Point endpoint;
	private boolean nextChildLeft = true;
	
	public NodeX(Set<Node> parents, Point endpoint) {
		super(parents);
		this.endpoint = endpoint;
	}
	
	public NodeX(Node parent, Point endpoint) {
		super(parent);
		this.endpoint = endpoint;
	}
	
	public Node getNextNode(Segment s, Point p) {
		if (p.x < this.endpoint.x)
			return this.childLeft;
		else {// if (p.x >= this.endpoint.x)
			// "Whenever a query point p lies on the vertical line an x-node, we decide it lies to the right."
			return this.childRight;
		}
	}
	
	@Override
	protected void setChild(Node oldChild, Node newChild) {
		if (oldChild == this.childLeft)
			this.childLeft = newChild;
		else if (oldChild == this.childRight)
			this.childRight = newChild;
		else
			throw new RuntimeException("invalid child calling");
	}

	@Override
	public int getRootedNodesAmount() {
		int amount = 1;
		
		if (this.childLeft != null)
			amount += this.childLeft.getRootedNodesAmount();
		
		if (this.childRight != null)
			amount += this.childRight.getRootedNodesAmount();
		
		return amount;
	}

	@Override
	public void print() {
		System.out.println("x " + this.id);
		System.out.println(this.endpoint.toString());
		System.out.println("left");
		if (this.childLeft != null) {
			this.childLeft.print();
		} else {
			System.out.println("null");
		}
		System.out.println(this.id + " right");
		if (this.childRight != null) {
			this.childRight.print();
		} else {
			System.out.println("null");
		}
	}
	
	public void setNextChildLeft() {
		this.nextChildLeft = true;
	}
	
	public void setNextChildRight() {
		this.nextChildLeft = false;
	}
	
	public void setChild(Node child) {
		if (this.nextChildLeft){
			this.childLeft = child;
		} else {
			this.childRight = child;
		}
	}
	
	@Override
	public int getRootedDepth() {
		int depth = 1;
		int depthLeft = 0;
		int depthRight = 0;
		
		if (this.childLeft != null)
			depthLeft = this.childLeft.getRootedDepth();
		
		if (this.childRight != null)
			depthRight = this.childRight.getRootedDepth();
		
		return depth + Math.max(depthLeft, depthRight);
	}
}
