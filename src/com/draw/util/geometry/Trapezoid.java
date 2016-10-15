package com.draw.util.geometry;

/**
 * @author ABF Ampt
 *
 * Simple class for trapezoids. 
 * It has a left and right {@link Point} 
 * and a top and bottom {@link Segment}
 */
public class Trapezoid
{

	/******************************
	 *           Fields           *
	 ******************************/

	/** contains the left point*/
	public final Point left;
	/** contains the right point*/
	public final Point right;
	/** contains the top point*/
	public final Segment top;
	/** contains the bottom point*/
	public final Segment bottom;

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * Trapezoid default constructor
	 * if any of the arguments are null, they are filled in with a bounding box variant, 
	 * such that you never get a null pointer
	 */
	public Trapezoid(Point left, Point right, Segment top, Segment bottom)
	{
		// check if the top segment is long enough in the x direction
		if (left.x < top.minX() || right.x > top.maxX())
			throw new IllegalArgumentException(
					"top segment should contain left and right in the x direction: "
							+ left.x + " <= " + top.minX() + " and "
							+ right.x + " => " + top.maxX() + " does not hold");

		// check if the bottom segment is long enough in the x direction
		if (left.x < bottom.minX() || right.x > bottom.maxX())
			throw new IllegalArgumentException(
					"bottom segment should contain left and right in the x direction: "
							+ left.x + " <= " + bottom.minX() + " and "
							+ right.x + " => " + bottom.maxX() + " does not hold");

		// check if the left point is located correctly in the y direction
		if ((double) left.y < bottom.getY(left.x) || (double)left.y > top.getY(left.x))
			throw new IllegalArgumentException(
					"left point in the y direction should be between bottom and top segment: "
							+ bottom.getY(left.x) + " <= " + (double)left.y + " <= "
							+ top.getY(left.x) + " does not hold");

		// check if the right point is located correctly in the y direction
		if ((double) right.y < bottom.getY(right.x)
				|| (double) right.y > top.getY(right.x))
			throw new IllegalArgumentException(
					"right point in the y direction should be between bottom and top segment: "
							+ bottom.getY(right.x) + " <= " + (double)right.y + " <= "
							+ top.getY(right.x) + " does not hold");

		// check if left is to the left of right
		if (left.x > right.x)
			throw new IllegalArgumentException(
					"left point should be to the left of the right point");

		// check if the top is above bottom
		if (!top.isAbove(bottom, false))
			throw new IllegalArgumentException("top should be above bottom");

		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	/******************************
	 *      Getters & Setters     *
	 ******************************/

	/**
	 * @return the left point
	 */
	public Point getLeft()
	{
		return left;
	}

	/**
	 * @return the right point
	 */
	public Point getRight()
	{
		return right;
	}

	/**
	 * @return the top segment
	 */
	public Segment getTop()
	{
		return top;
	}

	/**
	 * @return the bottom segment
	 */
	public Segment getBottom()
	{
		return bottom;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String result = "";
		result += "left: " + left.toString() + "\n";
		result += "top: " + top.toString() + "\n";
		result += "right: " + right.toString() + "\n";
		result += "bottom: " + bottom.toString();
		return result;
	}
}
