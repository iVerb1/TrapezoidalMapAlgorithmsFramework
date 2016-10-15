package com.draw.algorithms.incrementalconstruction;

import java.util.HashSet;
import java.util.Set;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public abstract class Node {
	
	Set<Node> parents;
	protected static int idGen = 0;
	protected int id;
	
	public Node(Set<Node> parents) {
		this.parents = parents;
		id = Node.idGen++;
	}
	
	public Node(Node parent) {
		this.parents = new HashSet<Node>();
		this.parents.add(parent);
	}
	
	abstract public Node getNextNode(Segment s, Point p);
	
	public Set<Node> getParents() {
		return this.parents;
	}

	public abstract int getRootedDepth();
	
	public abstract int getRootedNodesAmount();
	
	public abstract void print();
	
	public abstract void setChild(Node node);

	protected abstract void setChild(Node oldChild, Node newChild);
}
