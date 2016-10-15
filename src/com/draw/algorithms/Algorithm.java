package com.draw.algorithms;

import java.util.Set;

import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 *
 */
public interface Algorithm
{

	/**
	 * the method called when executing an algorithm. 
	 * All the work including setup should be done in this method.
	 * Constructor may only be used to set algorithm parameters if any exist
	 * 
	 * @param simplePolygon - a {@link CircularList} of points
	 */
	public Set<Trapezoid> execute(Publisher console, CircularList<Point> simplePolygon);
}
