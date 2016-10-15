package com.draw.util.search;

public class SimpleBinaryNode<T> {
	
	public SimpleBinaryNode<T> left;
	
	public SimpleBinaryNode<T> right;
	
	public T element;
	
	public SimpleBinaryNode(T element, SimpleBinaryNode<T> left, SimpleBinaryNode<T> right) {
		this.element = element;
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeaf() {
		return left == null && right == null;
	}
	
	@Override
	public String toString() {
		return element.toString();
	}

}
