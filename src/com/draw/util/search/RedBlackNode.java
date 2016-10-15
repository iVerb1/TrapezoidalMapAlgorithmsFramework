package com.draw.util.search;

public class RedBlackNode<T> {
    
    public T element;
    public RedBlackNode<T> left, right;
    public boolean color;
    public int N;

    public RedBlackNode(T element, boolean color, int N) {
        this.element = element;
        this.color = color;
        this.N = N;
    }
   
}
