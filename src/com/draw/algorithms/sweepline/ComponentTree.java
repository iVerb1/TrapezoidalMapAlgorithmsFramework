package com.draw.algorithms.sweepline;

import java.util.Comparator;

import com.draw.util.search.*;
import com.draw.util.geometry.Component;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class ComponentTree extends RedBlackBST<Component> {

	public ComponentTree() {
		super(new Comparator<Component>()
		{
			@Override
			public int compare(Component c1, Component c2)
			{
				if (c1.getTop().isAbove(c2.getTop(), true))
					return 1;
				if (c1.getTop().isBelow(c2.getTop(), true))
					return -1;
				return 0;
			}
		});
	}
	
	public Component findUpperIncidentComponent(Point p) {
		return  elementAt(findUpperIncidentComponent(p, root));
	}	
	
	public Component findLowerIncidentComponent(Point p) {
		return elementAt(findLowerIncidentComponent(p, root));
	}
		
	public Component findSplitComponent(Point p) {
		return elementAt(findSplitComponent(p, root));
	}
	
	private RedBlackNode<Component> findUpperIncidentComponent(Point p, RedBlackNode<Component> t) {
		if (t == null)
			return null;		
		if (t.element.getBottom().isAbove(p, true))
			return findUpperIncidentComponent(p, t.left);
		else if (t.element.getBottom().isBelow(p, true))
			return findUpperIncidentComponent(p, t.right);
		else
			return t;
	}
	
	private RedBlackNode<Component> findLowerIncidentComponent(Point p, RedBlackNode<Component> t) {
		if (t == null)
			return null;		
		if (t.element.getTop().isAbove(p, true))
			return findLowerIncidentComponent(p, t.left);
		else if (t.element.getTop().isBelow(p, true))
			return findLowerIncidentComponent(p, t.right);
		else
			return t;
	}
	
	private RedBlackNode<Component> findSplitComponent(Point p, RedBlackNode<Component> t) {
		if (t == null)
			return null;
		if (t.element.getBottom().isAbove(p, true))
			return findSplitComponent(p, t.left);
		else if (t.element.getTop().isBelow(p, true))
			return findSplitComponent(p, t.right);
		else {
			if (!p.equals(t.element.getTop().rightPoint()) && !p.equals(t.element.getBottom().rightPoint()))
				return t;
			else
				return null;
		}	
	}

	public void mergeComponents(Component upper, Component lower) {
		delete(upper);
		delete(lower);
		insert(new Component(upper.getTop(), lower.getBottom(), upper.getBottom().rightPoint()));
	}
	
	public void splitComponent(Component c, Segment top, Segment bottom) {
		delete(c);
		insert(new Component(c.getTop(), top, top.leftPoint())); //can also be bottom
		insert(new Component(bottom, c.getBottom(), top.leftPoint())); //can also be bottom
	}

}
