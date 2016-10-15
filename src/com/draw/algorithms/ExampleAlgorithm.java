package com.draw.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 * Example Algorithm that is tested only for a type of input as describe in example input.txt
 */
public class ExampleAlgorithm implements Algorithm
{

	/******************************
	 *           Fields           *
	 ******************************/

	private boolean extraStuff;
	
	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * ExmapleAlgorithm default constructor
	 */
	public ExampleAlgorithm(boolean createExtraStuff)
	{
		this.extraStuff = createExtraStuff;
	}

	/******************************
	 *       Business Logic       *
	 ******************************/
	
	@Override
	public Set<Trapezoid> execute(Publisher console, CircularList<Point> simplePolygon)
	{	
		try
		{
			Thread.sleep(1800 + new Random().nextInt(400));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		List<Segment> segments = new ArrayList<>(simplePolygon.size());
		
		for(int i = 0; i < simplePolygon.size(); i++)
		{
			segments.add(new Segment(simplePolygon.get(i), simplePolygon.get(i+1)));
		}
		
		Set<Trapezoid> result = new HashSet<>();
		result.add(new Trapezoid(simplePolygon.get(0), simplePolygon.get(1), segments.get(0), segments.get(3)));
		result.add(new Trapezoid(simplePolygon.get(1), simplePolygon.get(2), segments.get(1), segments.get(3)));
		result.add(new Trapezoid(simplePolygon.get(2), simplePolygon.get(3), segments.get(2), segments.get(3)));
		
		console.println("printing from algorithm");
		
		if(extraStuff)
			for(int i = 0; i < 100000; i++)
				new Point(new Random().nextInt(2000), new Random().nextInt(2000));
		
		try
		{
			Thread.sleep(1800 + new Random().nextInt(400));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/
}
