package com.draw.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ABF Ampt
 *
 * Implements a circular form of the regular array list we all know and love. 
 * This means no index out of bounds exception can occur in the get method.
 */
public class CircularList<E> extends ArrayList<E>
{

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public CircularList()
	{
		super();
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * @param size
	 */
	public CircularList(int size)
	{
		super(size);
	}

	/**
	 * Constructs a list containing the elements of the specified collection, 
	 * in the order they are returned by the collection's iterator.
	 * @param collection
	 */
	public CircularList(Collection<? extends E> collection)
	{
		super(collection);
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	/** 
	 * Returns the element at the specified position in this list for index % size().
	 *  
	 * @param index - index of the element to return 
	 * @return the element at the specified position in this list for index % size()
	 */
	@Override
	public E get(int index)
	{
		while (index < 0)
			index -= size();

		while (index >= size())
			index -= size();

		return super.get(index);
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/
}
