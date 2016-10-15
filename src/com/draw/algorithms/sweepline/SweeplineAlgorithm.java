package com.draw.algorithms.sweepline;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.draw.algorithms.Algorithm;
import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.MultiMap;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 *
 */
public class SweeplineAlgorithm implements Algorithm
{

	/******************************
	 *           Fields           *
	 ******************************/

	private Comparator<Point> pointComparator;
	private Comparator<Segment> segmentComparator;
	private TreeSet<Segment> statusStructure;
	private Queue<Point> eventQueue;

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * SweeplineAlgorithm default constructor
	 */
	public SweeplineAlgorithm()
	{
		this.pointComparator = new Comparator<Point>()
		{
			@Override
			public int compare(Point p1, Point p2)
			{
				if (p1.x == p2.x)
					return Integer.compare(p1.y, p2.y);
				return Integer.compare(p1.x, p2.x);
			}
		};

		this.segmentComparator = new Comparator<Segment>()
		{
			@Override
			public int compare(Segment s1, Segment s2)
			{
				if (s1.isAbove(s2, true))
					return 1;
				if (s1.isBelow(s2, true))
					return -1;
				return 0;
			}
		};
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	/**
	 * It is terrible but it works I think
	 * Improvements to come
	 */
	@Override
	public Set<Trapezoid> execute(Publisher console,
			CircularList<Point> simplePolygon)
	{
		Set<Trapezoid> result = new HashSet<Trapezoid>();
		MultiMap<Point, Segment> segmentMap = new MultiMap<Point, Segment>();
		for (int i = 0; i < simplePolygon.size(); i++)
		{
			Segment s = new Segment(simplePolygon.get(i),
					simplePolygon.get(i + 1));
			segmentMap.add(simplePolygon.get(i), s);
			segmentMap.add(simplePolygon.get(i + 1), s);
		}

		// sort on x secondary on y
		simplePolygon.sort(pointComparator);

		eventQueue = new LinkedList<>();

		// create the event queue
		for (Point point : simplePolygon)
		{
			eventQueue.add(point);
		}

		this.statusStructure = new TreeSet<Segment>(segmentComparator);
		Map<Segment, Boolean> upperMap = new HashMap<Segment, Boolean>();
		
		try
		{
			while (!eventQueue.isEmpty())
			{
				Point p = eventQueue.poll();
				for (Segment s : segmentMap.get(p))
				{
					// left point means add
					if (s.rightPoint().equals(p))
						statusStructure.remove(s);
				}
				for (Segment s : segmentMap.get(p))
				{
					// left point means add
					if (s.leftPoint().equals(p))
					{
						statusStructure.add(s);
						console.println(p);
						// find first strictly higher segment (above)
						Segment above = statusStructure.higher(s);
						console.println(Arrays.toString(statusStructure
								.toArray()));
						if (above != null && !Boolean.FALSE.equals(upperMap.get(above)))
						{
							for (Point r : eventQueue)
							// Point r = eventQueue.peek();
							{
								if (r.y < s.getY(r.x))
									continue;
								result.add(new Trapezoid(p, r, above, s));
								break;
							}
							upperMap.put(above, true);
							upperMap.put(s, false);
							continue;
						}

						Segment below = statusStructure.lower(s);
						if (below != null && !Boolean.TRUE.equals(upperMap.get(above)))
						{
							for (Point r : eventQueue)
							// Point r = eventQueue.peek();
							{
								if (r.y > s.getY(r.x))
									continue;
								result.add(new Trapezoid(p, r, s, below));
								break;
							}
							upperMap.put(above, false);
							upperMap.put(s, true);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			console.printStackTrace(e);
		}

		for (Trapezoid t : result)
			console.println("\n" + t.toString());

		return result;
	}
	/******************************
	 *      Getters & Setters     *
	 ******************************/
}
