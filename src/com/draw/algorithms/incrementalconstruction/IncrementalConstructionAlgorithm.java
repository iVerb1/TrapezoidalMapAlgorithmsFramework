package com.draw.algorithms.incrementalconstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.draw.algorithms.Algorithm;
import com.draw.execution.Publisher;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;
import com.draw.util.geometry.Trapezoid;

/**
 * @author iVerb
 *
 */
public class IncrementalConstructionAlgorithm implements Algorithm {
	
	public enum MODE {random, insertion, left_to_right, widest_first}
	
	SearchStructure searchStructure;
	private MODE mode;
	private boolean coinFlip;
	
	public IncrementalConstructionAlgorithm(MODE mode, boolean coinFlip) {
		this.mode = mode;
		this.coinFlip = coinFlip;
	}

	private List<Segment> flipCoin(List<Segment> segments) {
		
		if (!this.coinFlip) {
			this.orderSegments(segments);
			return segments;
		}
		
		Set<Segment> set = new HashSet();
		
		for (Segment segment : segments) {
			set.add(segment);
		}
		
		List<Segment> tier1 = new ArrayList();
		List<Segment> tier2 = new ArrayList();
		List<Segment> tier3 = new ArrayList();
		List<Segment> tier4 = new ArrayList();
		
		for (Segment segment : set) {
			if (Math.random() > 0.5)
				tier1.add(segment);
		}
		
		set.removeAll(tier1);
		
		for (Segment segment : set) {
			if (Math.random() > 0.5)
				tier2.add(segment);
		}
		
		set.removeAll(tier2);
		
		for (Segment segment : set) {
			if (Math.random() > 0.5)
				tier3.add(segment);
		}
		
		set.removeAll(tier3);
		
		for (Segment segment : set) {
			tier4.add(segment);
		}
		
		this.orderSegments(tier1);
		this.orderSegments(tier2);
		this.orderSegments(tier3);
		this.orderSegments(tier4);
		
		tier1.addAll(tier2);
		tier1.addAll(tier3);
		tier1.addAll(tier4);
		
		return tier1;
	}
	
	private void orderSegments(List<Segment> segments) {
		switch(mode) {
			case random:
				Collections.shuffle(segments);
				break;
			case insertion:
				break;
			case left_to_right:
				Collections.sort(segments, new Comparator<Segment>() {
					@Override
					public int compare(Segment arg0, Segment arg1) {
						
						//assuming no vertical segments
						
						if (arg0.rightPoint().x <= arg1.leftPoint().x)
							return -1;
						else if (arg0.leftPoint().x >= arg1.rightPoint().x)
							return 1;
						else if (arg0.bottomPoint().y >= arg1.topPoint().y || arg0.topPoint().y <= arg1.bottomPoint().y)
							return 0;
						else if (arg0.isRightOf(arg1, true))
							return 1;
						else
							return -1;
					}
				});
				break;
			case widest_first:
				Collections.sort(segments, new Comparator<Segment>() {
					@Override
					public int compare(Segment arg0, Segment arg1) {
						if (arg0.width() < arg1.width())
							return 1;
						else if (arg0.width() > arg1.width())
							return -1;
						else
							return 0;
					}
				});
				break;
		}
	}

	@Override
	public Set<Trapezoid> execute(Publisher console, CircularList<Point> simplePolygon) {
		TrapezoidalMapElement.idGen = 1;
		List<Segment> segments = new ArrayList<>();

		for(int i = 0; i < simplePolygon.size(); i++) {
			 segments.add(new Segment(simplePolygon.get(i), simplePolygon.get(i+1)));
		}
		
		segments = this.flipCoin(segments);		
		
		searchStructure = new SearchStructure();

		TrapezoidalMapElement boundingRectangle = new TrapezoidalMapElement(new Trapezoid(new Point(Integer.MIN_VALUE, 0),
				new Point(Integer.MAX_VALUE, 0), new Segment(new Point(Integer.MIN_VALUE, Integer.MAX_VALUE), new Point(
						Integer.MAX_VALUE, Integer.MAX_VALUE)), new Segment(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE),
						new Point(Integer.MAX_VALUE, Integer.MIN_VALUE))));

		// the initial leaf for the bounding rectangle
		NodeLeaf leafBoundingRectangle = new NodeLeaf(searchStructure.root, boundingRectangle);
		searchStructure.root.child = leafBoundingRectangle;

		for (Segment s : segments) {
			List<TrapezoidalMapElement> elements = searchStructure.findElements(s);

			boolean cutsLeftmostTrapezoid = s.leftPoint().x == elements.get(0).getTrapezoid().left.x;
			boolean cutsRightmostTrapezoid = s.rightPoint().x == elements.get(elements.size() - 1).getTrapezoid().right.x;

			TrapezoidalMapElement prevTop = null;
			TrapezoidalMapElement lastBottom = null;
			boolean mergeTop = false;
			boolean mergeBottom = false;

			for (TrapezoidalMapElement element : elements) {

				boolean elementIsFirst = element.equals(elements.get(0));
				boolean elementIsLast = element.equals(elements.get(elements.size() - 1));

				Trapezoid trapezoid = element.getTrapezoid();
				TrapezoidalMapElement top, bottom;

				Set<Node> elementParents = element.getLeaf().getParents();
				Node lastNode = null;
				Node root = null;

				// determining top
				if (!mergeTop) {
					Point topLeft, topRight;

					if (elementIsFirst)
						topLeft = s.leftPoint();
					else
						topLeft = trapezoid.left;

					if (elementIsLast)
						topRight = s.rightPoint();
					else
						topRight = findTopRight(elements, element, s);

					top = new TrapezoidalMapElement(new Trapezoid(topLeft, topRight, trapezoid.top, s));
				} 
				else {
					top = prevTop;
				}

				// determining bottom
				if (!mergeBottom) {
					Point bottomLeft, bottomRight;

					if (elementIsFirst)
						bottomLeft = s.leftPoint();
					else
						bottomLeft = trapezoid.left;

					if (elementIsLast)
						bottomRight = s.rightPoint();
					else
						bottomRight = findBottomRight(elements, element, s);

					bottom = new TrapezoidalMapElement(new Trapezoid(bottomLeft, bottomRight, s, trapezoid.bottom));
				} else {
					bottom = lastBottom;
				}

				// Delta_0
				if (elementIsFirst) {

					if (!cutsLeftmostTrapezoid) {
						TrapezoidalMapElement left = new TrapezoidalMapElement(new Trapezoid(trapezoid.left, s.leftPoint(),
								trapezoid.top, trapezoid.bottom));

						element.giveLeftTopTo(left);
						element.giveLeftBottomTo(left);

						left.setNeighborRightBottom(bottom);
						left.setNeighborRightTop(top);

						top.setNeighborLeftBottom(left);
						top.setNeighborLeftTop(left);
						bottom.setNeighborLeftBottom(left);
						bottom.setNeighborLeftTop(left);

						NodeX nodeX1 = new NodeX(elementParents, s.leftPoint());
						nodeX1.childLeft = new NodeLeaf(nodeX1, left);
						nodeX1.setNextChildRight();
						
						root = nodeX1;
						lastNode = nodeX1;
					} 
					else {
						boolean topTriangle = s.leftPoint().equals(element.getTrapezoid().top.leftPoint());
						boolean bottomTriangle = s.leftPoint().equals(element.getTrapezoid().bottom.leftPoint());

						if (!topTriangle) {
							top.setNeighborLeftBottom(element.getNeighborLeftTop());
							element.giveLeftTopTo(top);
						}

						if (!bottomTriangle) {
							bottom.setNeighborLeftTop(element.getNeighborLeftBottom());
							element.giveLeftBottomTo(bottom);
						}
					}

				}

				// Delta_k
				if (elementIsLast) {

					if (!cutsRightmostTrapezoid) {
						TrapezoidalMapElement right = new TrapezoidalMapElement(new Trapezoid(s.rightPoint(), trapezoid.right,
								trapezoid.top, trapezoid.bottom));

						right.setNeighborLeftBottom(bottom);
						right.setNeighborLeftTop(top);
						element.giveRightTopTo(right);
						element.giveRightBottomTo(right);

						top.setNeighborRightBottom(right);
						top.setNeighborRightTop(right);
						bottom.setNeighborRightBottom(right);
						bottom.setNeighborRightTop(right);

						NodeX nodeX2;
						if (root == null) {
							nodeX2 = new NodeX(elementParents, s.rightPoint());
							root = nodeX2;
						}
						else {
							nodeX2 = new NodeX(lastNode, s.rightPoint());
							lastNode.setChild(nodeX2);
						}
						
						nodeX2.childRight = new NodeLeaf(nodeX2, right);
						nodeX2.setNextChildLeft();
						lastNode = nodeX2;
					} 
					else {

						boolean topTriangle = s.rightPoint().equals(element.getTrapezoid().top.rightPoint());
						boolean bottomTriangle = s.rightPoint().equals(element.getTrapezoid().bottom.rightPoint());

						if (!topTriangle) {
							top.setNeighborRightBottom(element.getNeighborRightTop());
							element.giveRightTopTo(top);
						}

						if (!bottomTriangle) {
							bottom.setNeighborRightTop(element.getNeighborRightBottom());
							element.giveRightBottomTo(bottom);
						}
					}
				}
				
				
				Trapezoid topTrapezoid = top.getTrapezoid();
				Trapezoid bottomTrapezoid = bottom.getTrapezoid();

				if (!elementIsFirst) {
					if (!mergeTop) {
						prevTop.setNeighborRightBottom(top);
						top.setNeighborLeftBottom(prevTop);

						Trapezoid prevTopTrapezoid = prevTop.getTrapezoid();

						if (prevTopTrapezoid.right.equals(prevTopTrapezoid.top.rightPoint()))
							prevTop.setNeighborRightTop(top);
							
						if (topTrapezoid.left.equals(topTrapezoid.top.leftPoint()))
							top.setNeighborLeftTop(prevTop);
					}

					if (!mergeBottom) {
						lastBottom.setNeighborRightTop(bottom);
						bottom.setNeighborLeftTop(lastBottom);

						Trapezoid prevBottomTrapezoid = lastBottom.getTrapezoid();

						if (prevBottomTrapezoid.right.equals(prevBottomTrapezoid.bottom.rightPoint()))
							lastBottom.setNeighborRightBottom(bottom);

						if (bottomTrapezoid.left.equals(bottomTrapezoid.bottom.leftPoint()))
							bottom.setNeighborLeftBottom(lastBottom);
					}
					
					if (!topTrapezoid.left.equals(topTrapezoid.top.leftPoint()) && topTrapezoid.left.equals(trapezoid.left))
						element.giveLeftTopTo(top);
					
					if (!bottomTrapezoid.left.equals(bottomTrapezoid.bottom.leftPoint()) && bottomTrapezoid.left.equals(trapezoid.left))
						element.giveLeftBottomTo(bottom);
					
				}
				
				if (!elementIsLast) {
					if (!topTrapezoid.right.equals(topTrapezoid.top.rightPoint()) && topTrapezoid.right.equals(trapezoid.right))
						element.giveRightTopTo(top);

					if (!bottomTrapezoid.right.equals(bottomTrapezoid.bottom.rightPoint()) && bottomTrapezoid.right.equals(trapezoid.right))
						element.giveRightBottomTo(bottom);
				}

				NodeY nodeY;
				if (root == null) {
					nodeY = new NodeY(elementParents, s);
					root = nodeY;
				}
				else {
					nodeY = new NodeY(lastNode, s);
					lastNode.setChild(nodeY);
				}

				if (mergeTop)
					nodeY.setChildTop(top.getLeaf());
				else
					nodeY.createChildTop(top);
				
				if (mergeBottom)
					nodeY.setChildBottom(bottom.getLeaf());
				else
					nodeY.createChildBottom(bottom);

				
				element.getLeaf().replaceWith(root);
				
				
				prevTop = top;
				lastBottom = bottom;

				// determining whether top/bottom should merge for the next
				// trapezoid
				if (trapezoid.right.equals(top.getTrapezoid().right))
					mergeTop = false;
				else
					mergeTop = true;

				if (trapezoid.right.equals(bottom.getTrapezoid().right))
					mergeBottom = false;
				else
					mergeBottom = true;
			}

		}
		
		Set<Trapezoid> solution = getTrapezoidsInPolygon();
		
		int dSize = searchStructure.size();
		int dDepth = searchStructure.depth();
		
		double pObsoleteTrapezoids = (100 * (1 - ((double)solution.size()) / ((double)TrapezoidalMapElement.idGen)));
		
		return solution;
	}

	private Point findTopRight(List<TrapezoidalMapElement> elements, TrapezoidalMapElement element, Segment s) {
		int index = elements.indexOf(element);
		
		for (int i = index; i < elements.size(); i++) {
			TrapezoidalMapElement e = elements.get(i);
			Trapezoid t = e.getTrapezoid();

			if (i != index) {
				if (s.isBelow(t.left, true))
					return t.left;
			}
			
			if (t.right.x <= s.rightPoint().x) {
				if (s.isBelow(t.right, true))
					return t.right;
			}

		}

		return s.rightPoint();
	}

	private Point findBottomRight(List<TrapezoidalMapElement> elements, TrapezoidalMapElement element, Segment s) {
		int index = elements.indexOf(element);

		for (int i = index; i < elements.size(); i++) {
			TrapezoidalMapElement e = elements.get(i);
			Trapezoid t = e.getTrapezoid();
			
			if (i != index) {
				if (s.isAbove(t.left, true))
					return t.left;
			}
			
			if (t.right.x <= s.rightPoint().x) {
				if (s.isAbove(t.right, true))
					return t.right;
			}
		}

		return s.rightPoint();
	}

	private Set<Trapezoid> getTrapezoidsInPolygon() {
		return getTrapezoidsInPolygon(searchStructure.root.child);
	}

	private Set<Trapezoid> getTrapezoidsInPolygon(Node n) {
		Set<Trapezoid> result = new HashSet<>();

		if (n instanceof NodeLeaf) {
			Trapezoid t = ((NodeLeaf) n).getTrapezoidalMapElement().getTrapezoid();
			if (t.left.x != Integer.MIN_VALUE && t.right.x != Integer.MAX_VALUE && t.top.leftPoint().x != Integer.MIN_VALUE
					&& t.bottom.leftPoint().x != Integer.MIN_VALUE && t.top.begin.x < t.top.end.x
					&& t.bottom.begin.x > t.bottom.end.x)
				result.add(t);
		} else if (n instanceof NodeX) {
			result.addAll(getTrapezoidsInPolygon(((NodeX) n).childLeft));
			result.addAll(getTrapezoidsInPolygon(((NodeX) n).childRight));
		} else if (n instanceof NodeY) {
			result.addAll(getTrapezoidsInPolygon(((NodeY) n).childTop));
			result.addAll(getTrapezoidsInPolygon(((NodeY) n).childBottom));
		}

		return result;
	}
}
