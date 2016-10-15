package com.draw.util.geometry;

/**
 * @author ABF Ampt
 *
 * Simple class for segments. It has a begin and end {@link Point}
 */
public class Segment
{

	/******************************
	 *           Fields           *
	 ******************************/

	/** contains the begin point*/
	public final Point begin;
	/** contains the end point*/
	public final Point end;

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * Segment default constructor
	 * @param begin
	 * @param end
	 */
	public Segment(final Point begin, final Point end)
	{
		if (begin == null)
			throw new IllegalArgumentException("Begin point cannot be null");
		if (end == null)
			throw new IllegalArgumentException("End point cannot be null");
		this.begin = begin;
		this.end = end;
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	/**
	 * Translates the segment with respect to its current position 
	 * @param beginX
	 * @param beginY
	 * @param endX
	 * @param endY
	 * @return
	 */
	public Segment translate(final int beginX, final int beginY,
			final int endX, final int endY)
	{
		Point begin = this.begin.translate(beginX, beginY);
		Point end = this.begin.translate(endX, endY);
		return new Segment(begin, end);
	}

	/**
	 * Translates the segments begin {@link Point} with respect to its current position 
	 * @param x
	 * @param y
	 * @return
	 */
	public Segment translateBeginPoint(final int x, final int y)
	{
		Point begin = this.begin.translate(x, y);
		return new Segment(begin, this.end);
	}

	/**
	 * Translates the segments end {@link Point} with respect to its current position 
	 * @param x
	 * @param y
	 * @return
	 */
	public Segment translateEndPoint(final int x, final int y)
	{
		Point end = this.end.translate(x, y);
		return new Segment(this.begin, end);
	}

	/**
	 * Returns the maximum x-coordinate of this segment
	 * @return
	 */
	public int maxX()
	{
		return Math.max(this.begin.x, this.end.x);
	}

	/**
	 * Returns the minimum x-coordinate of this segment
	 * @return
	 */
	public int minX()
	{
		return Math.min(this.begin.x, this.end.x);
	}

	/**
	 * Returns the maximum y-coordinate of this segment
	 * @return
	 */
	public int maxY()
	{
		return Math.max(this.begin.y, this.end.y);
	}

	/**
	 * Returns the minimum y-coordinate of this segment
	 * @return
	 */
	public int minY()
	{
		return Math.min(this.begin.y, this.end.y);
	}

	/**
	 * return the y-value of this segment at a given x coordinate
	 * @param x
	 * @return
	 */
	public double getY(int x)
	{
		if (x < this.minX() || x > this.maxX())
			throw new IllegalArgumentException(
					x < this.minX() ? "x is smaller than minX: " + x + " < "
							+ this.minX() : "x is greater than maxX: " + x
							+ " > " + this.maxX());
		return begin.y + (end.y - begin.y) * (double) (x - begin.x)
				/ (double) (end.x - begin.x);
	}

	/**
	 * return the x-value of this segment at a given y coordinate
	 * @param y
	 * @return
	 */
	public double getX(int y)
	{
		if (y < this.minY() || y > this.maxY())
			throw new IllegalArgumentException(
					y < this.minY() ? "y is smaller than minY: " + y + " < "
							+ this.minY() : "y is greater than maxY: " + y
							+ " > " + this.maxY());
		return begin.x + (end.x - begin.x) * (double) (y - begin.y)
				/ (double) (end.y - begin.y);
	}	
	
	/**
	 * Determines whether this segment touches point p
	 *  
	 * @param p
	 * @return
	 */
	public boolean touches(Point p) {
		return this.getY(p.x) == p.y;
	}
	
	public boolean inDomain(Point p) {
		return p.x >= this.leftPoint().x && p.x <= this.rightPoint().x;
	}

	public int width() {
		return this.rightPoint().x - this.leftPoint().x;
	}
	
	public double length() {
		return Math.sqrt(Math.pow(leftPoint().x - rightPoint().x ,2) + Math.pow(leftPoint().y - rightPoint().y , 2));
	}
	
	
	/**
	 * Returns true if this segment is above the other segment
	 * This segment assumes the segments are overlapping in the x direction and are non crossing
	 *  
	 * @param other
	 * @param strict
	 * @return
	 */
	public boolean isAbove(Segment other, boolean strict)
	{	
		boolean leftPointsTouch = this.leftPoint().equals(other.leftPoint());
		boolean rightPointsTouch  = this.rightPoint().equals(other.rightPoint());
		
		if ((strict && (leftPointsTouch || rightPointsTouch)) || (leftPointsTouch && rightPointsTouch))
			return false;		
		if (this.leftPoint().x < other.leftPoint().x) 
			return isAbove(other.leftPoint(), strict);
		if (this.rightPoint().x > other.rightPoint().x)
			return isAbove(other.rightPoint(), strict);
		if (!leftPointsTouch)
			return other.isBelow(this.leftPoint(), strict);
		if (!rightPointsTouch)
			return other.isBelow(this.rightPoint(), strict);
		return false;
	}
	
	/**
	 * Determines whether this segment is above p. Assumes p lies in the x range of this 
	 * segment and that it cannot lie anywhere on the segment, except for the endpoints.
	 * 
	 * @param p
	 * @param strict
	 * @return
	 */
	public boolean isAbove(Point p, boolean strict) {
		if (this.leftPoint() == p) {
			if (strict)
				return false;
			else
				return this.rightPoint().y > p.y;
		}
		else if (this.rightPoint() == p) {
			if (strict)
				return false;
			else
				return this.leftPoint().y > p.y;
		}
		else	
			return this.getY(p.x) > p.y;
	}

	public boolean isBelow(Segment other, boolean strict)
	{
		boolean leftPointsTouch = this.leftPoint().equals(other.leftPoint());
		boolean rightPointsTouch  = this.rightPoint().equals(other.rightPoint());
		
		if ((strict && (leftPointsTouch || rightPointsTouch)) || (leftPointsTouch && rightPointsTouch))
			return false;		
		if (this.leftPoint().x < other.leftPoint().x) 
			return isBelow(other.leftPoint(), strict);
		if (this.rightPoint().x > other.rightPoint().x)
			return isBelow(other.rightPoint(), strict);
		if (!leftPointsTouch)
			return other.isAbove(this.leftPoint(), strict);
		if (!rightPointsTouch)
			return other.isAbove(this.rightPoint(), strict);
		return false;
	}

	public boolean isBelow(Point p, boolean strict) {
		if (this.leftPoint() == p) {
			if (strict)
				return false;
			else
				return this.rightPoint().y < p.y;
		}
		else if (this.rightPoint() == p) {
			if (strict)
				return false;
			else
				return this.leftPoint().y < p.y;
		}
		else	
			return this.getY(p.x) < p.y;
	}
	
	public boolean isRightOf(Segment other, boolean strict) {
		boolean topPointsTouch = this.topPoint().equals(other.topPoint());
		boolean bottomPointsTouch  = this.bottomPoint().equals(other.bottomPoint());
		
		if ((strict && (topPointsTouch || bottomPointsTouch)) || (topPointsTouch && bottomPointsTouch))
			return false;		
		if (this.bottomPoint().y < other.bottomPoint().y)  
			return isRightOf(other.bottomPoint(), strict);
		if (this.topPoint().y > other.topPoint().y)
			return isRightOf(other.topPoint(), strict);
		if (!bottomPointsTouch)
			return other.isLeftOf(this.bottomPoint(), strict);
		if (!topPointsTouch)
			return other.isLeftOf(this.topPoint(), strict);
		return false;
	}
	
	public boolean isRightOf(Point p, boolean strict) {
		if (this.bottomPoint() == p) {
			if (strict)
				return false;
			else
				return this.topPoint().x > p.x;
		}
		else if (this.topPoint() == p) {
			if (strict)
				return false;
			else
				return this.bottomPoint().x > p.x;
		}
		else	
			return this.getX(p.y) > p.x;
	}
	
	public boolean isLeftOf(Point p, boolean strict) {
		if (this.topPoint() == p) {
			if (strict)
				return false;
			else
				return this.bottomPoint().x < p.x;
		}
		else if (this.bottomPoint() == p) {
			if (strict)
				return false;
			else
				return this.topPoint().x < p.x;
		}
		else	
			return this.getX(p.y) < p.x;
	}
	
	
	/**
	 * Returns the left point, that is the point with the smallest x
	 * @return
	 */
	public Point leftPoint()
	{
		return this.begin.x < this.end.x ? begin : end;
	}

	/**
	 * Returns the right point, that is the point with the largest x
	 * @return
	 */
	public Point rightPoint()
	{
		return this.begin.x < this.end.x ? end : begin;
	}

	/**
	 * Returns the bottom point, that is the point with the smallest y
	 * @return
	 */
	public Point bottomPoint()
	{
		return this.begin.y < this.end.y ? begin : end;
	}

	/**
	 * Returns the top point, that is the point with the largest y
	 * @return
	 */
	public Point topPoint()
	{
		return this.begin.y < this.end.y ? end : begin;
	}

	@Override
	public int hashCode()
	{
		// is allowed to go over the limit
		return begin.hashCode() + end.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Segment))
			return false;
		return equals((Segment) obj);
	}

	public boolean equals(Segment s)
	{
		if (this == s)
			return true;
		return begin.equals(s.begin) && end.equals(s.end);
	}

	@Override
	public String toString()
	{
		return "(" + begin.toString() + ", " + end.toString() + ")";
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/

	/**
	 * @return the begin {@link Point}
	 */
	public Point getBegin()
	{
		return begin;
	}

	/**
	 * @return the end {@link Point}
	 */
	public Point getEnd()
	{
		return end;
	}
}
