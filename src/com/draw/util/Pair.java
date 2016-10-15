package com.draw.util;


public class Pair<S, T> {
	
	public S item1;
	
	public T item2;
	
	public Pair(S item1, T item2) {
		this.item1 = item1;
		this.item2 = item2;
	}
	
	@Override
	public int hashCode()
	{
		return item1.hashCode() + item2.hashCode();
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Pair<?, ?>))
			return super.equals(obj);
		
		if (this == obj)
			return true;
		
		return item1.equals(((Pair)obj).item1) && item2.equals(((Pair)obj).item2);
	}

	
}
