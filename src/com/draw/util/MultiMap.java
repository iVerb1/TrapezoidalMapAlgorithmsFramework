package com.draw.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ABF Ampt
 *
 */
public class MultiMap<K,V> extends HashMap<K, Set<V>>
{

	/******************************
	 *           Fields           *
	 ******************************/

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * 
	 */
	private static final long serialVersionUID = 9173902759531500732L;

	/**
	 * MultiList default constructor
	 */
	public MultiMap()
	{
		super();
	}

	/******************************
	 *       Business Logic       *
	 ******************************/
	
	public Set<V> add(K key, V value)
	{
		Set<V> values = this.get(key);
		if(values == null)
			values = new HashSet<>();
		values.add(value);
		this.put(key, values);
		return values;
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/
}
