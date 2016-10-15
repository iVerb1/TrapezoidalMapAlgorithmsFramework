package com.create;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Polygon {
	  
  private Point[] points;
  
  public Polygon(Point[] points) {
    
    this.points = points;

    if(!isClockwise())
    {
    	Point[] reversedPoints = new Point[points.length];
    	for(int i = 0; i < reversedPoints.length; i++)
    		reversedPoints[points.length -1 - i] = points[i];
    	this.points = reversedPoints;
    }
  }
  
  private boolean isClockwise()
  {
	  int result = 0;
	  for(int i = 0; i < this.points.length; i++)
	  {
		  Point p1 = this.points[i];
		  Point p2 = this.points[(i + 1) % this.points.length];
		  result += (p2.getX() - p1.getX()) * (p2.getY() + p1.getY());
	  }
	  return result >= 0;
  }
  
  public String output() {
    
    String output = this.points.length + "\r\n";
    
    for (int i = 0; i < this.points.length; i++) {
      
      Point point = this.points[i];
      
      output += point.getX() + " " + point.getY();
      
      if (i < this.points.length - 1) {
        output += "\r\n";
      }
    }
    
    return output;
  }
  
  public File toFile(String path)
  {
	  if(!path.endsWith(".txt"))
		  path += ".txt";
	  
	  File file = new File(path);
	  
	  PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(file, "UTF-8");
			writer.print(this.output());
		}
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		finally
		{
			writer.close();
		}
	  return file;
  }
  
}
