package com.draw.algorithms.divideandconquer;

import java.util.Comparator;

import com.draw.util.search.BalancedRangeTree1D;

public class MergeRangeTree1D extends BalancedRangeTree1D<Merge> {
	
	public MergeRangeTree1D() {
		super(new Comparator<Merge>()
		{
			@Override
			public int compare(Merge p1, Merge p2)
			{
				if (p1.top.x == p2.top.x)
					return Integer.compare(p1.top.y, p2.top.y);
				return Integer.compare(p1.top.x, p2.top.x);
			}
		});
	}


	@Override
	public int getInt(Merge t) {
		return t.top.x;
	}
	
}
