package com.draw.util.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implements an unbalanced binary search tree.
 * 
 * @author iVerb
 * 
 */
public class SimpleBST<T> {	

    /** The tree root. */
    protected SimpleBinaryNode<T> root;
    
    protected Comparator<T> comparator;
  
	/**
	 * Construct the tree.
	 */
	public SimpleBST(Comparator<T> comparator) {
		root = null;
		this.comparator = comparator;
	}
	
	public Comparator<T> getComparator() {
		return comparator;
	}

	/**
	 * Insert into the tree; duplicates are ignored.
	 * 
	 * @param x
	 *            the item to insert.
	 */
	public void insert(T x) {
		root = insert(x, root);
	}

	/**
	 * Remove from the tree. Nothing is done if x is not found.
	 * 
	 * @param x
	 *            the item to remove.
	 */
	public void delete(T x) {
		root = delete(x, root);
	}

	/**
	 * Find the smallest item in the tree.
	 * 
	 * @return smallest item or null if empty.
	 */
	public T getMin() {
		return elementAt(getMin(root));
	}

	/**
	 * Find the largest item in the tree.
	 * 
	 * @return the largest item of null if empty.
	 */ 
	public T getMax() {
		return elementAt(getMax(root));
	}

	/**
	 * Find an item in the tree.
	 * 
	 * @param x
	 *            the item to search for.
	 * @return the matching item or null if not found.
	 */
	public T find(T x) {
		return elementAt(find(x, root));
	}

	/**
	 * Make the tree logically empty.
	 */
	public void makeEmpty() {
		root = null;
	}

	/**
	 * Test if the tree is logically empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * Print the tree contents in sorted order.
	 */
	@Override
	public String toString() {
		if (isEmpty())
			return "Empty tree";
		else
			return toString(root);
	}
	
	public List<T> getAll() {
		return isEmpty() ? new ArrayList<T>() : allElementsRootedAt(root);
	}
	
	protected List<T> allElementsRootedAt(SimpleBinaryNode<T> t) {
		List<T> result = new ArrayList<T>();
		
		if (t.left != null)
			result.addAll(allElementsRootedAt(t.left));
		
		result.add(t.element);
		
		if (t.right != null)
			result.addAll(allElementsRootedAt(t.right));
		
		return result;
	}

	/**
	 * Internal method to get element field.
	 * 
	 * @param t
	 *            the node.
	 * @return the element field or null if t is null.
	 */
	protected T elementAt(SimpleBinaryNode<T> t) {
		return t == null ? null : t.element;
	}

	/**
	 * Internal method to insert into a subtree.
	 * 
	 * @param x
	 *            the item to insert.
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root.
	 */
	private SimpleBinaryNode<T> insert(T x, SimpleBinaryNode<T> t) {
		if (t == null)
			t = new SimpleBinaryNode<T>(x, null, null);
		else if (comparator.compare(x, t.element) < 0)
			t.left = insert(x, t.left);
		else if (comparator.compare(x, t.element) > 0)
			t.right = insert(x, t.right);
		else
			; // Duplicate; do nothing
		return t;
	}

	/**
	 * Internal method to remove from a subtree.
	 * 
	 * @param x
	 *            the item to remove.
	 * @param t
	 *            the node that roots the tree.
	 * @return the new root.
	 */
	private SimpleBinaryNode<T> delete(T x, SimpleBinaryNode<T> t) {
		if (t == null)
			return t; // Item not found; do nothing
		if (comparator.compare(x, t.element) < 0)
			t.left = delete(x, t.left);
		else if (comparator.compare(x, t.element) > 0)
			t.right = delete(x, t.right);
		else if (t.left != null && t.right != null) // Two children
		{
			t.element = getMin(t.right).element;
			t.right = delete(t.element, t.right);
		} else
			t = (t.left != null) ? t.left : t.right;
		return t;
	}

	/**
	 * Internal method to find the smallest item in a subtree.
	 * 
	 * @param t
	 *            the node that roots the tree.
	 * @return node containing the smallest item.
	 */
	private SimpleBinaryNode<T> getMin(SimpleBinaryNode<T> t) {
		if (t == null)
			return null;
		else if (t.left == null)
			return t;
		return getMin(t.left);
	}

	/**
	 * Internal method to find the largest item in a subtree.
	 * 
	 * @param t
	 *            the node that roots the tree.
	 * @return node containing the largest item.
	 */
	private SimpleBinaryNode<T> getMax(SimpleBinaryNode<T> t) {
		if (t != null)
			while (t.right != null)
				t = t.right;

		return t;
	}

	/**
	 * Internal method to find an item in a subtree.
	 * 
	 * @param x
	 *            is item to search for.
	 * @param t
	 *            the node that roots the tree.
	 * @return node containing the matched item.
	 */
	private SimpleBinaryNode<T> find(T x, SimpleBinaryNode<T> t) {
		if (t == null)
			return null;
		if (comparator.compare(x, t.element) < 0)
			return find(x, t.left);
		else if (comparator.compare(x, t.element) > 0)
			return find(x, t.right);
		else
			return t; // Match
	}

	/**
	 * Internal method to print a subtree in sorted order.
	 * 
	 * @param t
	 *            the node that roots the tree.
	 */
	private String toString(SimpleBinaryNode<T> t) {
		StringBuilder sb = new StringBuilder();
		if (t != null) {
			sb.append(toString(t.left));
			sb.append(t.element.toString());
			sb.append(toString(t.right));
		}
		return sb.toString();
	}
}