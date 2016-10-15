package com.draw.algorithms.incrementalconstruction;

import java.util.HashSet;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class NodeRoot extends Node {
	
	public Node child = null;
	
	public NodeRoot() {
		super(new HashSet<Node>());
	}

	@Override
	public Node getNextNode(Segment s, Point p) {
		return this.child;
	}
	
	@Override
	protected void setChild(Node oldChild, Node newChild) {
		if (oldChild == this.child)
			this.setChild(newChild);
		else
			throw new RuntimeException("invalid child calling");
	}

	@Override
	public void setChild(Node child) {
		this.child = child;
	}

	@Override
	public int getRootedNodesAmount() {
		int amount = 1;
		
		if (child != null)
			amount += child.getRootedNodesAmount();
		
		return amount;
	}

	@Override
	public void print() {
		System.out.println("root " + this.id);
		System.out.println("child");
		if (this.child != null) {
			this.child.print();
		} else {
			System.out.println("null");
		}
		
		
	}

	@Override
	public int getRootedDepth() {
		int depth = 1;
		
		if (this.child != null)
			depth += this.child.getRootedDepth();
		
		return depth;
	}
	
}
