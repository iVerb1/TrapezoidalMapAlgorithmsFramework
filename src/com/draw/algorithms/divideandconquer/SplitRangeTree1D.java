package com.draw.algorithms.divideandconquer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.draw.util.geometry.Interval;
import com.draw.util.geometry.Segment;
import com.draw.util.search.BalancedRangeTree1D;
import com.draw.util.search.RedBlackNode;

public class SplitRangeTree1D extends BalancedRangeTree1D<Split> {

	private int minX;
	private boolean foundX;
	private List<Split> result;
	
	public SplitRangeTree1D() {
		super(new Comparator<Split>()
		{
			@Override
			public int compare(Split p1, Split p2)
			{
				if (p1.top.x == p2.top.x)
					return Integer.compare(p1.top.y, p2.top.y);
				return Integer.compare(p1.top.x, p2.top.x);
			}
		});
	}

	public List<Split> findAllLeftMostInBetweenSegments(Segment top, Segment bottom, Interval i) {
		minX = Integer.MAX_VALUE;
		foundX = false;
		result = new ArrayList<Split>();
		
		RedBlackNode<Split> splitNode = findSplitNode(i, root);
		findAllLeftMostInBetweenSegments(top, bottom, i, splitNode);
		
		return result;
	}
	
	private void findAllLeftMostInBetweenSegments(Segment top, Segment bottom, Interval i, RedBlackNode<Split> t) {
		if (t == null)
			return;		
		
		findAllLeftMostInBetweenSegments(top, bottom, i, t.left);
		checkSplit(top, bottom, i, t.element);
		findAllLeftMostInBetweenSegments(top, bottom, i, t.right); 
	}
	
	private void checkSplit(Segment top, Segment bottom, Interval i, Split s) {
		if (i.fits(getInt(s)) && (foundX == false || s.top.x == minX)) {
			if (top.isAbove(s.top, true) && bottom.isBelow(s.bottom, true)) {
				result.add(s);
				foundX = true;
				minX = s.top.x;
			}
		}
	}

	@Override
	public int getInt(Split t) {
		return t.top.x;
	}
	
}
