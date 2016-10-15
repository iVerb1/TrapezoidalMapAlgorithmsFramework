package com.draw.algorithms.incrementalconstruction;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class NodeLeaf extends Node {

	private TrapezoidalMapElement element;
	
	public NodeLeaf(Node parent, TrapezoidalMapElement element) {
		super(parent);	
		
		this.element = element;
		element.setLeaf(this);
	}
	
	public Node getNextNode(Segment s, Point p) {
		return null;
	}
	
	@Override
	protected void setChild(Node oldChild, Node newChild) {
		throw new RuntimeException("leafs shouldnt have childs set");
	}
	
	public void replaceWith(Node node) {
		for (Node parent : parents) {
			parent.setChild(this, node);
		}
	}
	
	public TrapezoidalMapElement getTrapezoidalMapElement() {
		return this.element;
	}

	@Override
	public int getRootedNodesAmount() {
		return 1;
	}

	@Override
	public void print() {
		System.out.println("leaf " + this.id);
		this.element.print();
	}

	@Override
	public void setChild(Node node) {
		throw new RuntimeException("leaf can't have children");
	}

	@Override
	public int getRootedDepth() {
		// 
		return 1;
	}
}