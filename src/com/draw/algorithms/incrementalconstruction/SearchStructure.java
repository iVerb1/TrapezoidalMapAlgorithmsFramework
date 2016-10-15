package com.draw.algorithms.incrementalconstruction;

import java.util.ArrayList;
import java.util.List;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Segment;

public class SearchStructure {
	
	public NodeRoot root = new NodeRoot();

	public List<TrapezoidalMapElement> findElements(Segment s) {
		// Delta_0 ... Delta_k
		List<TrapezoidalMapElement> elements = new ArrayList<>();
				
		// Delta_0
		elements.add(findElement(s, s.leftPoint()));
		
		TrapezoidalMapElement lastAdded = elements.get(0);
		
		// "while q lies to the right of rightp(Delta_j)"
		while (s.rightPoint().x > lastAdded.getTrapezoid().right.x) {
			
			if (s.isBelow(lastAdded.getTrapezoid().right, true)) {
				// the new element is the lower right neighbor of the latest added trapezoidal map element
				lastAdded = lastAdded.getNeighborRightBottom();
			} else {//if (s.isAbove(lastAdded.getTrapezoid().right, true)) {
				// the new element is the upper right neighbor of the latest added trapezoidal map element
				lastAdded = lastAdded.getNeighborRightTop();
			} //else {
				//throw new RuntimeException("degenerate case: point lies on segment");
			//}
			
			elements.add(lastAdded);
		}
		
		return elements;
	}
	
	public int depth() {
		return this.root.getRootedDepth();
	}
	
	private TrapezoidalMapElement findElement(Segment s, Point p) {
		Node node = this.root;
		
		while (!(node instanceof NodeLeaf)) {
			node = node.getNextNode(s, p);
		}
		
		return ((NodeLeaf) node).getTrapezoidalMapElement();
	}

	
	public int size() {
		return this.root.getRootedNodesAmount();
	}
	
	public void print() {
		this.root.print();
	}
}
