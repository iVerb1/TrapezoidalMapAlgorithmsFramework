package com.draw.util.geometry;

/**
 * @author ABF Ampt
 *
 * Simple class for points. It has an x and y coordinate
 */
public class Point
{

	/******************************
	 *           Fields           *
	 ******************************/

	/** contains the x-coordinate*/
	public final int x;
	/** contains the y-coordinate*/
	public final int y;
	
	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * Constructor for Point
	 * @param x
	 * @param y
	 */
	public Point(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}

	/******************************
	 *       Business Logic       *
	 ******************************/
	
	/**
	 * Translates a point with respect to its current position 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point translate(final int x, final int y)
	{
		return new Point(this.x + x, this.y + y);
	}
	
	@Override
	public int hashCode()
	{
		// is allowed to go over the limit
		return Integer.hashCode(x) + Integer.hashCode(y);
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Point))
			return false;
		return equals((Point)obj);
	}
	
	public boolean equals(Point p)
	{
		if(this == p)
			return true;
		return x == p.x && y == p.y;
	}
	
	@Override
	public String toString()
	{
		return "[" + x + ", " + y + "]";
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/

	/**
	 * @return the x coordinate
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y coordinate
	 */
	public int getY()
	{
		return y;
	}
}
