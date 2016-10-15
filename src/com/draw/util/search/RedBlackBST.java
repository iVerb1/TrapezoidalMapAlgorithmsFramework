package com.draw.util.search;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class RedBlackBST<T> {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    protected RedBlackNode<T> root;     // root of the BST
	protected Comparator<T> comparator;

    
    public RedBlackBST(Comparator<T> comparator) {
    	this.comparator = comparator;
    }

   /*************************************************************************
    *  RedBlackNode<T> helper methods
    *************************************************************************/
    
    // is node x red; false if x is null ?
    private boolean isRed(RedBlackNode<T> x) {
        if (x == null) return false;
        return (x.color == RED);
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(RedBlackNode<T> x) {
        if (x == null) return 0;
        return x.N;
    } 


   /*************************************************************************
    *  Size methods
    *************************************************************************/

    // return number of element-value pairs in this symbol table
    public int size() { return size(root); }

    // is this symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

   /*************************************************************************
    *  Standard BST search
    *************************************************************************/

    // value associated with the given element; null if no such element
    public T find(T element) { return elementAt(find(root, element)); }

    // value associated with the given element in subtree rooted at x; null if no such element
    private RedBlackNode<T> find(RedBlackNode<T> x, T element) {
        while (x != null) {
            int cmp = comparator.compare(element, x.element);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else              return x;
        }
        return x;
    }

    // is there a element-value pair with the given element?
    public boolean contains(T element) {
        return find(element) != null;
    }

	protected T elementAt(RedBlackNode<T> t) {
		return t == null ? null : t.element;
	}
	
	protected List<T> allElementsRootedAt(RedBlackNode<T> t) {
		List<T> result = new ArrayList<T>();
		
		if (t.left != null)
			result.addAll(allElementsRootedAt(t.left));
		
		result.add(t.element);
		
		if (t.right != null)
			result.addAll(allElementsRootedAt(t.right));
		
		return result;
	}


    // is there a element-value pair with the given element in the subtree rooted at x?
    // private boolean contains(RedBlackNode<T> x, T element) {
    //    return (get(x, element) != null);
    // }

   /*************************************************************************
    *  Red-black insertion
    *************************************************************************/

    // insert the element-value pair; overwrite the old value with the new value
    // if the element is already present
    public void insert(T element) {
        root = insert(root, element);
        root.color = BLACK;
        // assert check();
    }

    // insert the element-value pair in the subtree rooted at h
    private RedBlackNode<T> insert(RedBlackNode<T> t, T element) { 
        if (t == null) 
        	return new RedBlackNode<T>(element, RED, 1);

        int cmp = comparator.compare(element, t.element);
        if      (cmp < 0) t.left  = insert(t.left,  element); 
        else if (cmp > 0) t.right = insert(t.right, element); 
        

        // fix-up any right-leaning links
        if (isRed(t.right) && !isRed(t.left))      t = rotateLeft(t);
        if (isRed(t.left)  &&  isRed(t.left.left)) t = rotateRight(t);
        if (isRed(t.left)  &&  isRed(t.right))     flipColors(t);
        t.N = size(t.left) + size(t.right) + 1;

        return t;
    }

   /*************************************************************************
    *  Red-black deletion
    *************************************************************************/

    // delete the element-value pair with the minimum element
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if (!isEmpty()) root.color = BLACK;
        // assert check();
    }

    // delete the element-value pair with the minimum element rooted at h
    private RedBlackNode<T> deleteMin(RedBlackNode<T> t) { 
        if (t.left == null)
            return null;

        if (!isRed(t.left) && !isRed(t.left.left))
            t = moveRedLeft(t);

        t.left = deleteMin(t.left);
        return balance(t);
    }


    // delete the element-value pair with the maximum element
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if (!isEmpty()) root.color = BLACK;
        // assert check();
    }

    // delete the element-value pair with the maximum element rooted at h
    private RedBlackNode<T> deleteMax(RedBlackNode<T> t) { 
        if (isRed(t.left))
            t = rotateRight(t);

        if (t.right == null)
            return null;

        if (!isRed(t.right) && !isRed(t.right.left))
            t = moveRedRight(t);

        t.right = deleteMax(t.right);

        return balance(t);
    }

    // delete the element-value pair with the given element
    public void delete(T element) { 
        if (!contains(element)) {
            System.err.println("symbol table does not contain " + element);
            return;
        }

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, element);
        if (!isEmpty()) root.color = BLACK;
        // assert check();
    }

    // delete the element-value pair with the given element rooted at h
    private RedBlackNode<T> delete(RedBlackNode<T> t, T element) { 
        // assert get(t, element) != null;

        if (comparator.compare(element, t.element) < 0)  {
            if (!isRed(t.left) && !isRed(t.left.left))
                t = moveRedLeft(t);
            t.left = delete(t.left, element);
        }
        else {
            if (isRed(t.left))
                t = rotateRight(t);
            if (comparator.compare(element, t.element) == 0 && (t.right == null))
                return null;
            if (!isRed(t.right) && !isRed(t.right.left))
                t = moveRedRight(t);
            if (comparator.compare(element, t.element) == 0) {
                RedBlackNode<T> x = min(t.right);
                t.element = x.element;
                // t.val = get(t.right, min(t.right).element);
                // t.element = min(t.right).element;
                t.right = deleteMin(t.right);
            }
            else t.right = delete(t.right, element);
        }
        return balance(t);
    }

   /*************************************************************************
    *  red-black tree helper functions
    *************************************************************************/

    // make a left-leaning link lean to the right
    private RedBlackNode<T> rotateRight(RedBlackNode<T> t) {
        // assert (h != null) && isRed(t.left);
        RedBlackNode<T> x = t.left;
        t.left = x.right;
        x.right = t;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = t.N;
        t.N = size(t.left) + size(t.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private RedBlackNode<T> rotateLeft(RedBlackNode<T> t) {
        // assert (h != null) && isRed(t.right);
        RedBlackNode<T> x = t.right;
        t.right = x.left;
        x.left = t;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = t.N;
        t.N = size(t.left) + size(t.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(RedBlackNode<T> t) {
        // t must have opposite color of its two children
        // assert (h != null) && (t.left != null) && (t.right != null);
        // assert (!isRed(t) &&  isRed(t.left) &&  isRed(t.right))
        //    || (isRed(t)  && !isRed(t.left) && !isRed(t.right));
        t.color = !t.color;
        t.left.color = !t.left.color;
        t.right.color = !t.right.color;
    }

    // Assuming that t is red and both t.left and t.left.left
    // are black, make t.left or one of its children red.
    private RedBlackNode<T> moveRedLeft(RedBlackNode<T> t) {
        // assert (h != null);
        // assert isRed(t) && !isRed(t.left) && !isRed(t.left.left);

        flipColors(t);
        if (isRed(t.right.left)) { 
            t.right = rotateRight(t.right);
            t = rotateLeft(t);
            flipColors(t);
        }
        return t;
    }

    // Assuming that t is red and both t.right and t.right.left
    // are black, make t.right or one of its children red.
    private RedBlackNode<T> moveRedRight(RedBlackNode<T> t) {
        // assert (h != null);
        // assert isRed(t) && !isRed(t.right) && !isRed(t.right.left);
        flipColors(t);
        if (isRed(t.left.left)) { 
            t = rotateRight(t);
            flipColors(t);
        }
        return t;
    }

    // restore red-black tree invariant
    private RedBlackNode<T> balance(RedBlackNode<T> t) {
        // assert (h != null);

        if (isRed(t.right))                      t = rotateLeft(t);
        if (isRed(t.left) && isRed(t.left.left)) t = rotateRight(t);
        if (isRed(t.left) && isRed(t.right))     flipColors(t);

        t.N = size(t.left) + size(t.right) + 1;
        return t;
    }


   /*************************************************************************
    *  Utility functions
    *************************************************************************/

    // height of tree (1-node tree has height 0)
    public int height() { return height(root); }
    private int height(RedBlackNode<T> x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

   /*************************************************************************
    *  Ordered symbol table methods.
    *************************************************************************/

    // the smallest element; null if no such element
    public T getMin() {
        if (isEmpty()) return null;
        return min(root).element;
    } 

    // the smallest element in subtree rooted at x; null if no such element
    private RedBlackNode<T> min(RedBlackNode<T> x) { 
        // assert x != null;
        if (x.left == null) return x; 
        else                return min(x.left); 
    } 

    // the largest element; null if no such element
    public T getMax() {
        if (isEmpty()) return null;
        return max(root).element;
    } 

    // the largest element in the subtree rooted at x; null if no such element
    private RedBlackNode<T> max(RedBlackNode<T> x) { 
        // assert x != null;
        if (x.right == null) return x; 
        else                 return max(x.right); 
    } 

    // the largest element less than or equal to the given element
    public T floor(T element) {
        RedBlackNode<T> x = floor(root, element);
        if (x == null) return null;
        else           return x.element;
    }    

    // the largest element in the subtree rooted at x less than or equal to the given element
    private RedBlackNode<T> floor(RedBlackNode<T> x, T element) {
        if (x == null) return null;
        int cmp = comparator.compare(element, x.element);
        if (cmp == 0) return x;
        if (cmp < 0)  return floor(x.left, element);
        RedBlackNode<T> t = floor(x.right, element);
        if (t != null) return t; 
        else           return x;
    }

    // the smallest element greater than or equal to the given element
    public T ceiling(T element) {  
        RedBlackNode<T> x = ceiling(root, element);
        if (x == null) return null;
        else           return x.element;  
    }

    // the smallest element in the subtree rooted at x greater than or equal to the given element
    private RedBlackNode<T> ceiling(RedBlackNode<T> x, T element) {  
        if (x == null) return null;
        int cmp = comparator.compare(element, x.element);
        if (cmp == 0) return x;
        if (cmp > 0)  return ceiling(x.right, element);
        RedBlackNode<T> t = ceiling(x.left, element);
        if (t != null) return t; 
        else           return x;
    }


    // the element of rank k
    public T select(int k) {
        if (k < 0 || k >= size())  return null;
        RedBlackNode<T> x = select(root, k);
        return x.element;
    }

    // the element of rank k in the subtree rooted at x
    private RedBlackNode<T> select(RedBlackNode<T> x, int k) {
        // assert x != null;
        // assert k >= 0 && k < size(x);
        int t = size(x.left); 
        if      (t > k) return select(x.left,  k); 
        else if (t < k) return select(x.right, k-t-1); 
        else            return x; 
    } 

    // number of elements less than element
    public int rank(T element) {
        return rank(element, root);
    } 

    // number of elements less than element in the subtree rooted at x
    private int rank(T element, RedBlackNode<T> x) {
        if (x == null) return 0; 
        int cmp = comparator.compare(element, x.element); 
        if      (cmp < 0) return rank(element, x.left); 
        else if (cmp > 0) return 1 + size(x.left) + rank(element, x.right); 
        else              return size(x.left); 
    } 

   /***********************************************************************
    *  Range count and range search.
    ***********************************************************************/

    public Iterable<T> getAll() {
        return getAll(getMin(), getMax());
    }

    // the elements between lo and hi, as an Iterable
    public Iterable<T> getAll(T lo, T hi) {
        Queue<T> queue = new LinkedList<T>();
        // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
        elements(root, queue, lo, hi);
        return queue;
    } 

    // add the elements between lo and hi in the subtree rooted at x
    // to the queue
    private void elements(RedBlackNode<T> x, Queue<T> queue, T lo, T hi) { 
        if (x == null) return; 
        int cmplo = comparator.compare(lo, x.element); 
        int cmphi = comparator.compare(hi, x.element); 
        if (cmplo < 0) elements(x.left, queue, lo, hi); 
        if (cmplo <= 0 && cmphi >= 0) queue.add(x.element); 
        if (cmphi > 0) elements(x.right, queue, lo, hi); 
    } 

    // number elements between lo and hi
    public int size(T lo, T hi) {
        if (comparator.compare(lo, hi) > 0) return 0;
        if (contains(hi)) return rank(hi) - rank(lo) + 1;
        else              return rank(hi) - rank(lo);
    }


   /*************************************************************************
    *  Check integrity of red-black BST data structure
    *************************************************************************/
    private boolean check() {
        return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all elements strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(RedBlackNode<T> x, T min, T max) {
        if (x == null) return true;
        if (min != null && comparator.compare(x.element, min) <= 0) return false;
        if (max != null && comparator.compare(x.element, max) >= 0) return false;
        return isBST(x.left, min, x.element) && isBST(x.right, x.element, max);
    } 

    // are the size fields correct?
    private boolean isSizeConsistent() { return isSizeConsistent(root); }
    private boolean isSizeConsistent(RedBlackNode<T> x) {
        if (x == null) return true;
        if (x.N != size(x.left) + size(x.right) + 1) return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    } 

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (T element : getAll())
            if (comparator.compare(element,select(rank(element))) != 0) return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() { return is23(root); }
    private boolean is23(RedBlackNode<T> x) {
        if (x == null) return true;
        if (isRed(x.right)) return false;
        if (x != root && isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    } 

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() { 
        int black = 0;     // number of black links on path from root to min
        RedBlackNode<T> x = root;
        while (x != null) {
            if (!isRed(x)) black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(RedBlackNode<T> x, int black) {
        if (x == null) return black == 0;
        if (!isRed(x)) black--;
        return isBalanced(x.left, black) && isBalanced(x.right, black);
    } 
}
