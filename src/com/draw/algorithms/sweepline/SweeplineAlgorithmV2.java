package com.draw.algorithms.sweepline;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.draw.algorithms.Algorithm;
import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.MultiMap;
import com.draw.util.geometry.Component;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;
import com.draw.util.geometry.Trapezoid;

/**
 * @author iVerb
 *
 */
public class SweeplineAlgorithmV2 implements Algorithm {

	private Comparator<Point> pointComparator;
	private ComponentTree statusStructure;
	private Queue<Point> eventQueue;

	/**
	 * SweeplineAlgorithm default constructor
	 */
	public SweeplineAlgorithmV2() {
		this.pointComparator = new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				if (p1.x == p2.x)
					return Integer.compare(p1.y, p2.y);
				return Integer.compare(p1.x, p2.x);
			}
		};
	}

	/**
	 * 
	 */
	@Override
	public Set<Trapezoid> execute(Publisher console, CircularList<Point> simplePolygon) {
		Set<Trapezoid> result = new HashSet<Trapezoid>();
		MultiMap<Point, Segment> segmentMap = new MultiMap<Point, Segment>();
		for (int i = 0; i < simplePolygon.size(); i++) {
			Point p1 = simplePolygon.get(i);
			Point p2 = simplePolygon.get(i + 1);
			Segment s = new Segment(p1, p2);
			segmentMap.add(p1, s);
			segmentMap.add(p2, s);
		}

		// sort on x secondary on y
		simplePolygon.sort(pointComparator);

		// create the event queue
		eventQueue = new LinkedList<>(simplePolygon);

		this.statusStructure = new ComponentTree();
		
		while (!eventQueue.isEmpty()) {
			Point p = eventQueue.poll();
			Iterator<Segment> it = segmentMap.get(p).iterator();
			Segment s1 = it.next();
			Segment s2 = it.next();

			Component upper = statusStructure.findUpperIncidentComponent(p);
			Component lower = statusStructure.findLowerIncidentComponent(p);
			Component split = statusStructure.findSplitComponent(p);

			if (upper == null && lower == null && split == null) { // start
				if (s1.isAbove(s2, false))
					statusStructure.insert(new Component(s1, s2, s1.leftPoint()));
				else
					statusStructure.insert(new Component(s2, s1, s1.leftPoint()));
			} 
			else {
				if (split != null) { // split
					Point left = split.getLastPoint();
					
					if (left.x != p.x)
						result.add(new Trapezoid(left, p, split.getTop(), split.getBottom()));
					
					if (s1.isAbove(s2, false))
						statusStructure.splitComponent(split, s1, s2);
					else
						statusStructure.splitComponent(split, s2, s1);
				} 
				else if (upper != null && lower != null && upper != lower) { // merge
					Point upperLeft = upper.getLastPoint();
					Point lowerLeft = lower.getLastPoint();
					
					if (upperLeft.x != p.x)
						result.add(new Trapezoid(upperLeft, p, upper.getTop(), upper.getBottom()));
					if (lowerLeft.x != p.x)
						result.add(new Trapezoid(lowerLeft, p, lower.getTop(), lower.getBottom()));
					
					statusStructure.mergeComponents(upper, lower);
				} 
				else if (upper != null) { // p connected to bottom segment of upper component
					Point left = upper.getLastPoint();
					
					if (left.x != p.x)
						result.add(new Trapezoid(left, p, upper.getTop(), upper.getBottom()));
					
					if (s1.equals(upper.getBottom()))
						upper.setBottom(s2);
					else
						upper.setBottom(s1);
				} 
				else { // p connected to top segment of lower component
					Point left = lower.getLastPoint();
					
					if (left.x != p.x)
						result.add(new Trapezoid(left, p, lower.getTop(), lower.getBottom()));

					if (s1.equals(lower.getTop()))
						lower.setTop(s2);
					else
						lower.setTop(s1);
				}

				if (upper != null && lower != null && upper == lower) // end
					statusStructure.delete(upper);
			}
		}
		

		return result;
	}
}
