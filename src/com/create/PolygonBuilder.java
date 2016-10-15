package com.create;

import java.util.*;

public class PolygonBuilder {

	private Random random = new Random();
	// is used in the old method of building the polygon
	private Point[] largestValidPointSetYet;

	public PolygonBuilder() {
	}
	
	public Polygon buildRandomPolygon(int n, int minX, int maxX, int minY, int maxY)
	{
		//if (n > Math.min(maxX - minX, maxY - minY))
		//	throw new IllegalArgumentException(
		//			"n too large, n <= min(maxX - minX, maxY - minY) should hold");

		Map<Integer, Set<Integer>> map = new HashMap<>();
		List<Point> points = new ArrayList<>(n);
		
		long rangeX = (long)maxX - minX;
		long rangeY = (long)maxX - minY;
		
		while (points.size() < n)
		{
			int x;
			int y;
			
			while (true) {

				x = (int) (minX + (long)(random.nextDouble() * rangeX));
				y = (int) (minY + (long)(random.nextDouble() * rangeY));

				if (map.containsKey(x))
					continue;
				
				if (!map.containsKey(x)) {

					map.put(x, new HashSet<Integer>());
					map.get(x).add(y);
					break;
				} else if (!map.get(x).contains(y)) {

					map.get(x).add(y);
					break;
				}
			}
			
			tryToAddPoint(points, new Point(x, y));
		}
		
		return new Polygon(points.toArray(new Point[points.size()]));

		// The old method
		
		/*for (int i = 0; i < n; i++) {

			int x;
			int y;

			while (true) {

				x = (int) (minX + (long)(random.nextDouble() * rangeX));
				y = (int) (minY + (long)(random.nextDouble() * rangeY));

				if (!map.containsKey(x)) {

					map.put(x, new HashSet<Integer>());
					map.get(x).add(y);
					break;
				} else if (!map.get(x).contains(y)) {

					map.get(x).add(y);
					break;
				}
			}

			points.add(new Point(x, y));
		}

		while (this.hasCrossingLines(points.toArray(new Point[points.size()]))) {
			Collections.shuffle(points);
		}

		return new Polygon(points.toArray(new Point[points.size()]));*/
	}

	// n <= min(rangeX, rangeY)
	public Polygon buildRandomPolygon(int n, int rangeX, int rangeY) {
		try{
			return buildRandomPolygon(n, 0, rangeX, 0, rangeY);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException(
					"n too large, n <= min(rangeX, rangeY) should hold");
		}
	}
	
	public Polygon buildRandomPolygon(int n, int minX, int maxX, int minY, int maxY, int timeInSeconds)
	{
		//if (n > Math.min(maxX - minX, maxY - minY))
		//	throw new IllegalArgumentException(
		//			"n too large, n <= min(maxX - minX, maxY - minY) should hold");

		largestValidPointSetYet = new Point[0];
		Map<Integer, Set<Integer>> map = new HashMap<>();
		List<Point> points = new ArrayList<>(n);
		
		long rangeX = (long)maxX - minX;
		long rangeY = (long)maxY - minY;
		
		long startTime = System.nanoTime();
		long timeToFinish = timeInSeconds * 1000000000L;
		boolean toFinish = false;
		
		while (!toFinish && points.size() < n)
		{
			int x;
			int y;
			
			while (true) {

				x = (int) (minX + (long)(random.nextDouble() * rangeX));
				y = (int) (minY + (long)(random.nextDouble() * rangeY));

				if (map.containsKey(x))
					continue;
				if (!map.containsKey(x)) {

					map.put(x, new HashSet<Integer>());
					map.get(x).add(y);
					break;
				} else if (!map.get(x).contains(y)) {

					map.get(x).add(y);
					break;
				}
			}
			
			tryToAddPoint(points, new Point(x, y));
		    toFinish = System.nanoTime() - startTime >= timeToFinish;
		}
		
		return new Polygon(points.toArray(new Point[points.size()]));
		
		// old Method

		/*for (int i = 0; i < n; i++) {

			int x;
			int y;

			while (true) {

				x = (int) (minX + (long)(random.nextDouble() * rangeX));
				y = (int) (minY + (long)(random.nextDouble() * rangeY));

				if (!map.containsKey(x)) {

					map.put(x, new HashSet<Integer>());
					map.get(x).add(y);
					break;
				} else if (!map.get(x).contains(y)) {

					map.get(x).add(y);
					break;
				}
			}

			points.add(new Point(x, y));
		}
		
		long startTime = System.nanoTime();
		long timeToFinish = timeInSeconds * 1000000000L;
		boolean toFinish = false;
		
		while (!toFinish)
		{
			// clone basically because we are removing stuff
			Point[] result = this.nonCrossingLines(new ArrayList<>(points));
			if(result.length > this.largestValidPointSetYet.length)
				this.largestValidPointSetYet = result;
			
			if(this.largestValidPointSetYet.length == n)
				break;
			
		    toFinish = System.nanoTime() - startTime >= timeToFinish;
			Collections.shuffle(points);
		}

		return new Polygon(this.largestValidPointSetYet);*/
	}

	// n <= min(rangeX, rangeY)
	public Polygon buildRandomPolygon(int n, int rangeX, int rangeY, int timeInSeconds) {
		try{
			return buildRandomPolygon(n, 0, rangeX, 0, rangeY, timeInSeconds);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException(
					"n too large, n <= min(rangeX, rangeY) should hold");
		}
	}

	public Point[] nonCrossingLines(List<Point> points) {

		List<Point> result = new ArrayList<Point>(points.size());
		
		for (int i = 0; i < points.size() - 1; i++) {
			Point a = points.get(i);
			Point b = points.get(i + 1);

			for (int j = i + 1; j < points.size(); j++) {
				Point c = points.get(j);
				Point d = points.get((j + 1) % points.size());

				if (linesIntersect(a, b, c, d))
				{
					points.remove(a);
					return nonCrossingLines(points);
				}
			}
			result.add(a);
		}
		
		// always add the last point (its the home bringer)
		result.add(points.get(points.size() - 1));

		return result.toArray(new Point[result.size()]);
	}
	
	public List<Point> tryToAddPoint(List<Point> points, Point p)
	{
		int i = points.size();
		points.add(p);
		try{
			while(hasCrossingLines(points.toArray(new Point[points.size()])))
			{
				points.add(i - 1, points.remove(i));
				i--;
			}
		}
		// might have index out of bounce so just do not add it
		catch (IndexOutOfBoundsException e){}
		return points;
	}

	public boolean hasCrossingLines(Point[] points) {

		for (int i = 0; i < points.length - 1; i++) {
			Point a = points[i];
			Point b = points[i + 1];

			for (int j = i + 1; j < points.length; j++) {
				Point c = points[j];
				Point d = points[(j + 1) % points.length];

				if (linesIntersect(a, b, c, d))
					return true;
			}
		}

		return false;
	}

	public boolean linesIntersect(Point a, Point b, Point c, Point d) {
		double x1 = (double) a.getX();
		double y1 = (double) a.getY();
		double x2 = (double) b.getX();
		double y2 = (double) b.getY();
		double x3 = (double) c.getX();
		double y3 = (double) c.getY();
		double x4 = (double) d.getX();
		double y4 = (double) d.getY();

		double delta = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		// lines are parallel
		if (delta == 0) {

			if (x2 == x1) { // vertical lines
				if (x1 == x3
						&& ((y1 < y3 && y3 < y2) || (y1 < y4 && y4 < y2)
								|| (y3 < y1 && y1 < y4) || (y3 < y2 && y2 < y4))) {
					return true;
				}
			} else {
				double slope = (y2 - y1) / (x2 - x1);

				if (slope == 0) {
					if (y1 == y3
							&& ((x1 < x3 && x3 < x2) || (x1 < x4 && x4 < x2)
									|| (x3 < x1 && x1 < x4) || (x3 < x2 && x2 < x4))) {
						return true;
					}
				} else {
					double ix1 = x1 - y1 / slope;
					double ix2 = x3 - y3 / slope;

					if (ix1 != ix2) {
						return false;
					} else {
						if ((x1 < x3 && x3 < x2) || (x1 < x4 && x4 < x2)
								|| (x3 < x1 && x1 < x4) || (x3 < x2 && x2 < x4)) {
							return true;
						}
					}
				}
			}

			return false;
		}

		double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2)
				* (x3 * y4 - y3 * x4))
				/ delta;
		double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2)
				* (x3 * y4 - y3 * x4))
				/ delta;

		if (x3 == x4) {
			if (yi < Math.min(y1, y2) || yi > Math.max(y1, y2)) {
				return false;
			}
		}

		if (xi < Math.min(x1, x2) || xi > Math.max(x1, x2)) {
			return false;
		}

		if (xi < Math.min(x3, x4) || xi > Math.max(x3, x4)) {
			return false;
		}

		if ((xi == x1 && yi == y1 && x1 == x3 && y1 == y3)
				|| (xi == x1 && yi == y1 && x1 == x4 && y1 == y4)
				|| (xi == x2 && yi == y2 && x2 == x3 && y2 == y3)
				|| (xi == x2 && yi == y2 && x2 == x4 && y2 == y4)) {
			return false;
		}

		return true;
	}

}
