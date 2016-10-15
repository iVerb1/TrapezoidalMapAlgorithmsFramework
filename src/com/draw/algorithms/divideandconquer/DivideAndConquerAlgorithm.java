package com.draw.algorithms.divideandconquer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.draw.algorithms.Algorithm;
import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.Pair;
import com.draw.util.geometry.Component;
import com.draw.util.geometry.Interval;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;
import com.draw.util.geometry.Trapezoid;


public class DivideAndConquerAlgorithm implements Algorithm {

	CircularList<Point> simplePolygon;
	Publisher console;
	
	Map<Point, Point> nextPoint;
	Map<Point, Point> prevPoint;

	Queue<Component> componentQueue;

	SplitRangeTree1D splitTree;
	MergeRangeTree1D mergeTree;
	

	@Override
	public Set<Trapezoid> execute(Publisher console, CircularList<Point> simplePolygon) {

		this.console = console;
		this.simplePolygon = simplePolygon;
		this.nextPoint = new HashMap<Point, Point>();
		this.prevPoint = new HashMap<Point, Point>();

		this.componentQueue = new LinkedList<Component>();

		this.splitTree = new SplitRangeTree1D();
		this.mergeTree = new MergeRangeTree1D();

		// give each point references to its previous and next neighbour in the polygon
		buildPointReferences();		
		
		// reduce vertically colinear point sequences
		Point start = removeRedundantPoints();

		// creating components for start vertices and inserting split/merge vertices into 1D range trees
		findRelevantVertices(start);

		// creating components for merge vertices
		createMergeComponents(start);

		// Handling components in componentQueue. This includes identifying trapezoids and when a component needs to split
		Set<Trapezoid> result = new HashSet<Trapezoid>();
		while (!componentQueue.isEmpty()) {
			result.addAll(extendComponent(componentQueue.poll()));			
		}
		return result;
	}

	private void buildPointReferences() {
		Point prev = simplePolygon.get(simplePolygon.size() - 1);
		for (Point p : simplePolygon) {
			nextPoint.put(prev, p);
			prevPoint.put(p, prev);
			prev = p;
		}
	}

	private Point removeRedundantPoints() {
		Point pr = null;
		Point ne = null;
		for (Point p : simplePolygon) {
			pr = prevPoint.get(p);
			ne = nextPoint.get(p);
			if (p.x == pr.x && p.x == ne.x) {
				nextPoint.put(pr, ne);
				prevPoint.put(ne, pr);
			}
		}
		return ne;
	}
	
	private Interval getIntervalFromComponent(Component c) {
		if (c.getBottom().maxX() < c.getTop().maxX())
			return new Interval(c.getLastPoint().x, c.getBottom().rightPoint().x, true, true);
		else
			return new Interval(c.getLastPoint().x, c.getTop().rightPoint().x, true, true);
	}
	
	private Set<Component> getSplitComponents(Component c, List<Split> splitVertices) {
		Set<Component> result = new HashSet<>();
		Segment bottom = c.getBottom();
		Segment top = c.getTop();
		Segment newTop, newBottom;
		Split prevSplit = null;
		
		for (int i = 0; i < splitVertices.size(); i++) {
			Split split = splitVertices.get(i);
			if (bottom.isBelow(split.bottom, true) && top.isAbove(split.top, true)) {
				newTop = new Segment(split.bottom, nextPoint.get(split.bottom));

				if (prevSplit == null)
					newBottom = bottom; // lowest split
				else
					newBottom = new Segment(prevSplit.top, prevPoint.get(prevSplit.top)); // non-lowest split

				result.add(new Component(newTop, newBottom, split.bottom));
				prevSplit = split;
			}
		}

		newTop = top;
		newBottom = new Segment(prevSplit.top, prevPoint.get(prevSplit.top)); // top split
		result.add(new Component(newTop, newBottom, prevSplit.top));

		return result;
	}

	private void findRelevantVertices(Point start) {
		Point p = start;
		do {
			Point prev = prevPoint.get(p);
			Point next1 = nextPoint.get(p);
			Point next2 = nextPoint.get(next1);
			
			Segment behind = new Segment(prev, p);
			Segment ahead = new Segment(p, next1);
			Segment ahead2 = new Segment(next1, next2);
			
			if (prev.x > p.x) {
				if (next1.x > p.x) {
					if (behind.isBelow(ahead, false)) // sharp start
						componentQueue.add(new Component(ahead, behind, p));
					else // sharp split
						splitTree.insert(new Split(p, p));
				} else if (next1.x == p.x && next2.x > p.x) {
					if (behind.isBelow(ahead2, false)) // rectangular start
						componentQueue.add(new Component(ahead2, behind, p));
					else // rectangular split
						splitTree.insert(new Split(p, next1));
				}
			} else if (prev.x < p.x) {				
				if (next1.x < p.x && behind.isBelow(ahead, false)) {  
						mergeTree.insert(new Merge(p, p)); // sharp merge
				}
				else if (next1.x == p.x && next2.x < p.x && behind.isBelow(ahead2, false)) { 
						mergeTree.insert(new Merge(next1, p)); // rectangular merge
				}
			}
			p = next1;
		} while (!p.equals(start));
	}
	
	private void createMergeComponents(Point start) {
		Point p = start;
		Map<Merge, Segment> topMap = new HashMap<>();
		Map<Merge, Segment> bottomMap = new HashMap<>();
		Map<Pair<Segment, Segment>, Merge> mergeMap = new HashMap<>();
		
		do {
			Point next = nextPoint.get(p);
			Segment s = new Segment(p, next);
			if (next.x > p.x) { //upper segment				
				Set<Merge> merges = mergeTree.findAll(new Interval(p.x, next.x, true, false));
				for (Merge m : merges) {
					if (s.isAbove(m.top, true)) {
						if (topMap.get(m) == null)
							topMap.put(m, s);
						else {
							Segment other = topMap.get(m);
							if (s.isBelow(other, false))
								topMap.put(m, s);
						}
					}
				}
			}				
			else if (next.x < p.x) { //lower segment
				Set<Merge> merges = mergeTree.findAll(new Interval(next.x, p.x, true, false));
				for (Merge m : merges) {
					if (s.isBelow(m.bottom, true)) {
						if (bottomMap.get(m) == null)
							bottomMap.put(m, s);
						else {
							Segment other = bottomMap.get(m);
							if (s.isAbove(other, false))
								bottomMap.put(m, s);
						}
					}
				}
			}	
			p = next;
		} while (!p.equals(start));
		
		for (Merge m : mergeTree.getAll()) {
			mergeMap.put(new Pair<Segment, Segment>(topMap.get(m), bottomMap.get(m)), m);
		}		
		
		for (Map.Entry<Pair<Segment, Segment>, Merge> entry : mergeMap.entrySet()) {
			componentQueue.add(new Component(entry.getKey().item1, entry.getKey().item2, entry.getValue().top));
		}
	}

	private Set<Trapezoid> extendComponent(Component component) {
		
		boolean continueExtending = true;
		Set<Trapezoid> result = new HashSet<>();

		while (continueExtending) {
			Interval interval = getIntervalFromComponent(component);
			Point left = component.getLastPoint();
			Point right;
			Segment top = component.getTop();
			Segment bottom = component.getBottom();
			List<Split> splits = splitTree.findAllLeftMostInBetweenSegments(top, bottom, interval);

			if (top.equals(new Segment(new Point(28, 27), new Point(31, 27))))
				System.out.println("");
			
			if (splits.size() > 0) { // splitting
				Set<Component> splitComponents = getSplitComponents(component, splits);
				componentQueue.addAll(splitComponents);
				right = splitComponents.iterator().next().getLastPoint();
				continueExtending = false;
			} 
			else { 
				if (component.getBottom().maxX() < component.getTop().maxX()) { // extending bottom of component
					right = component.getBottom().rightPoint();

					Point newBottomEndPoint = prevPoint.get(right);
					if (newBottomEndPoint.x > right.x)
						component.setBottom(new Segment(right, newBottomEndPoint)); // stump progression
					else if (newBottomEndPoint.x == right.x && prevPoint.get(newBottomEndPoint).x > right.x)
						component.setBottom(new Segment(newBottomEndPoint, prevPoint.get(newBottomEndPoint))); // rectangular progression
					else
						continueExtending = false; // merge or end
				}
				else { // extending top of component
					right = component.getTop().rightPoint();

					Point newTopEndPoint = nextPoint.get(right);
					if (newTopEndPoint.x > right.x)
						component.setTop(new Segment(right, newTopEndPoint)); // stump progression
					else if (newTopEndPoint.x == right.x && nextPoint.get(newTopEndPoint).x > right.x)
						component.setTop(new Segment(newTopEndPoint, nextPoint.get(newTopEndPoint))); // rectangular progression
					else
						continueExtending = false; // merge or end
				}
			}
			if (right.x > left.x)
				result.add(new Trapezoid(left, right, top, bottom));
		}
		return result;
	}
	
}
