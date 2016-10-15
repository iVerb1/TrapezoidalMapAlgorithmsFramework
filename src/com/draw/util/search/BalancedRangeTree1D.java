package com.draw.util.search;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.draw.util.geometry.Interval;

public abstract class BalancedRangeTree1D<T> extends RedBlackBST<T> {

	public BalancedRangeTree1D(Comparator<T> comparator) {
		super(comparator);
	}
	
	public Set<T> findAll(Interval i) {
		RedBlackNode<T> splitNode = findSplitNode(i, root);
		Set<T> result = new HashSet<T>();
		
		if (splitNode == null)
			return result;
		else {
			result.add(splitNode.element);
			
			RedBlackNode<T> v = splitNode.left;
			while (v != null) {
				if (i.fits(getInt(v.element))) {	
					if (v.right != null)
						result.addAll(allElementsRootedAt(v.right));
					
					result.add(v.element);
					v = v.left;
				}
				else
					v = v.right;
			}
			
			v = splitNode.right;
			while (v != null) {
				if (i.fits(getInt(v.element))) {
					if (v.left != null)
						result.addAll(allElementsRootedAt(v.left));
					
					result.add(v.element);
					v = v.right;
				}
				else
					v = v.left;
			}
			
		}
		return result;
	}
	
	public RedBlackNode<T> findSplitNode(Interval i, RedBlackNode<T> t) {
		if (t == null)
			return null;
		if (i.liesLeftOf(getInt(t.element)))
			return findSplitNode(i, t.left);
		else if (i.liesRightOf(getInt(t.element)))
			return findSplitNode(i, t.right);
		else
			return t;
	}
	
	abstract public int getInt(T t);
	
}
