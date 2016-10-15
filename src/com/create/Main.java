package com.create;

public class Main {
	
	public static void main(String args[])
	{
		new PolygonBuilder().buildRandomPolygon(1000, 10000000, 10000000, 60).toFile("D:/Desktop/random4.txt");
	}
}
