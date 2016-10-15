package com.draw.execution;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

import com.draw.algorithms.Algorithm;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 *
 */
public class ExecutionEnvironment
{

	/******************************
	 *           Fields           *
	 ******************************/
	
	private long executionTime;
	private long executionMemory;

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * ExecutionEnvironment default constructor
	 */
	public ExecutionEnvironment()
	{
	}

	/******************************
	 *       Business Logic       *
	 ******************************/
	
	public CircularList<Point> readInput(File file) throws Exception
	{
		CircularList<Point> points = new CircularList<>();

		Scanner scanner = null;
		try
		{
			// create new Polygon
			scanner = new Scanner(file);

			int length = 0;

			if (scanner.hasNextLine())
				length = Integer.valueOf(scanner.nextLine().trim());
			else
				throw new IllegalArgumentException(
						"Cannot open this file, not the right format");

			// auxilary String that represents a point
			String auxString;
			// array containing coordinates of the point
			String[] coordinates;

			int lineNumber = 2;
			int redundantPoints = 0;
			Point prevPoint = null;
			while (scanner.hasNextLine())
			{
				// remove spaces around the line
				auxString = scanner.nextLine().trim();
				// split the string by removing all spaces it can find
				coordinates = auxString.split("\\s+");
				// if the previous row has not same length as the
				// current
				// row and
				// the previous row was not empty:
				if (coordinates.length != 2)
				{
					throw new IllegalArgumentException("Coordinates"
							+ Arrays.toString(coordinates)
							+ "are not represented correctly at line: "
							+ lineNumber);
				}
				Point point = new Point(
						Integer.valueOf(coordinates[0]),
						Integer.valueOf(coordinates[1]));
				
				if (prevPoint != null ? !point.equals(prevPoint) : true) //avoid adding duplicate points
					points.add(point);
				else
					redundantPoints++;
				
				prevPoint = point;
				lineNumber++;
			}
						
			if (points.size() + redundantPoints != length)
			{
				throw new IllegalArgumentException(
						"Length at line 1: "
								+ length
								+ " does not reflect the number of points in the file: "
								+ (points.size() + redundantPoints));
			}
		}
		finally
		{
			scanner.close();
		}
		return points;
	}

	public Set<Trapezoid> executeAlgorithm(Algorithm algorithm, Publisher console, CircularList<Point> simplePolygon) throws Exception
	{
		// Get the Java runtime
		Runtime runtime = Runtime.getRuntime();

		// Run the garbage collector
		runtime.gc();
		
		// use clone so the algorithm can do whatever it likes with the input
		CircularList<Point> input = (CircularList<Point>) simplePolygon.clone();

		// used to calculate memory
		long startMemory = runtime.totalMemory() - runtime.freeMemory();
		long startTime = System.nanoTime();

		// execute the algorithm
		Set<Trapezoid> result = algorithm.execute(console, input);

		long stopTime = System.nanoTime();
		long stopMemory = runtime.totalMemory() - runtime.freeMemory();

		this.executionTime = stopTime - startTime;
		this.executionMemory = stopMemory - startMemory;
		
		return result;
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/

	/**
	 * @return the executionTime
	 */
	public long getExecutionTime()
	{
		return executionTime;
	}

	/**
	 * @return the executionMemory
	 */
	public long getExecutionMemory()
	{
		return executionMemory;
	}
}
