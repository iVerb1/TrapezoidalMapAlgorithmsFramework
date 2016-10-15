package com.draw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import com.draw.util.geometry.Point;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 *
 */
public class Canvas extends JPanel implements MouseWheelListener,
		MouseMotionListener, MouseListener
{

	/******************************
	 *           Fields           *
	 ******************************/
	/**
	 * 
	 */
	private static final long serialVersionUID = -937326859965085938L;
	public boolean indicateTrapezoidEndPoints = false;
	public boolean showPolygonBorder = false;
	private static final int DIAMETER = 10;

	private List<Point> points;
	private Set<Trapezoid> trapezoids;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	public double scale = 1;
	public int translateX = 0;
	public int translateY = 0;
	private Point lastLocation;

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * Canvas default constructor
	 */
	public Canvas()
	{
		super();
		this.points = new ArrayList<Point>();
		this.trapezoids = new HashSet<Trapezoid>();

		ToolTipManager.sharedInstance().registerComponent(this);
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		// ((Graphics2D) g).scale(scale, scale);
		this.paintTrapezoids(g);
		this.paintPoints(g);
	}

	/**
	 * Draw the polygon
	 * @param g
	 */
	private void paintPoints(Graphics g)
	{
		int[] xs = new int[this.points.size()];
		int[] ys = new int[this.points.size()];
		Rectangle bounds = this.getBounds();
		double xFactor = (double) (bounds.width - 2 * DIAMETER) * scale
				/ (double) ((long) maxX - minX);
		double yFactor = (double) (bounds.height - 2 * DIAMETER) * scale
				/ (double) ((long) maxY - minY);

		for (int i = 0; i < this.points.size(); i++)
		{
			Point point = this.points.get(i);
			double x = xFactor * ((long) point.x - minX) + DIAMETER
					+ translateX;
			double y = yFactor * ((long) point.y - minY) + DIAMETER
					- translateY;
			// integer constrains (if a screen has more than 1000000 call me impressed) 
			if(x > 1000000)
				xs[i] = 1000000;
			else if(x < -1000000)
				xs[i] = -1000000;
			else
				xs[i] = (int) Math.round(x);
			if(y > 1000000)
				ys[i] = 1000000;
			else if(y < -1000000)
				ys[i] = -1000000;
			else
				ys[i] = (int) Math.round(y);
			
			ys[i] = bounds.height - ys[i];
		}

		if (showPolygonBorder || trapezoids.size() == 0)
		{
			g.setColor(Color.BLACK);
			g.drawPolygon(xs, ys, this.points.size());
		}
		// blueish
		g.setColor(new Color(0, 76, 246));
		for (int i = 0; i < this.points.size(); i++)
		{
			g.fillOval(xs[i] - DIAMETER / 2, ys[i] - DIAMETER / 2, DIAMETER,
					DIAMETER);
		}
	}

	/**
	 * Draw the trapezoids
	 * @param g
	 */
	private void paintTrapezoids(Graphics g)
	{
		int[] xs = new int[4];
		int[] ys = new int[4];

		Rectangle bounds = this.getBounds();
		double xFactor = (double) (bounds.width - 2 * DIAMETER) * scale
				/ (double) ((long) maxX - minX);
		double yFactor = (double) (bounds.height - 2 * DIAMETER) * scale
				/ (double) ((long) maxY - minY);

		// apparently this is valid...
		((Graphics2D) g).setStroke(new BasicStroke(2));

		int counter = 0;
		Map<Point, Integer> endPoints = new HashMap<Point, Integer>();
		Map<Point, Integer> drawnEndPoints = new HashMap<Point, Integer>();
		
		if (indicateTrapezoidEndPoints)
			// to indicate the trapezoid endpoint neatly, we have to do some
			// prepocessing
			for (Trapezoid trapezoid : this.trapezoids)
			{
				xs = getXs(xs, trapezoid, xFactor);
				ys = getYs(ys, trapezoid, yFactor);

				for (int i = 0; i < xs.length; i++)
				{
					Point p = new Point(xs[i], ys[i]);
					endPoints.put(p, endPoints.getOrDefault(p, 0) + 1);
					drawnEndPoints.put(p, 0);
				}
			}

		for (Trapezoid trapezoid : this.trapezoids)
		{
			xs = getXs(xs, trapezoid, xFactor);
			ys = getYs(ys, trapezoid, yFactor);

			// light reddish
			g.setColor(new Color(180, 61, 0, 20));
			g.fillPolygon(xs, ys, 4);
			// dark reddish
			g.setColor(new Color(180, 61, 0));
			g.drawPolygon(xs, ys, 4);

			if (indicateTrapezoidEndPoints)
			{
				counter %= 6;
				counter++;
				switch (counter)
				{
				case 1:
					g.setColor(new Color(255, 0, 0));
					break;
				case 2:
					g.setColor(new Color(255, 0, 255));
					break;
				case 3:
					g.setColor(new Color(0, 255, 0));
					break;
				case 4:
					g.setColor(new Color(255, 255, 0));
					break;
				case 5:
					g.setColor(new Color(0, 0, 255));
					break;
				case 6:
					g.setColor(new Color(0, 255, 255));
					break;
				}
				for (int i = 0; i < xs.length; i++)
				{
					Point p = new Point(xs[i], ys[i]);
					drawnEndPoints.put(p, drawnEndPoints.get(p) + 1);
					// this is where the pre processing comes in handy
					g.fillArc(xs[i] - DIAMETER, ys[i] - DIAMETER, 2 * DIAMETER,
							2 * DIAMETER, 90 + drawnEndPoints.get(p)
									* (360 / endPoints.get(p)),
							360 / endPoints.get(p));
				}
			}
		}
	}
	
	private int[] getXs(int[] xs, Trapezoid trapezoid, double xFactor)
	{
		double xl = xFactor * ((long) trapezoid.left.x - minX) + DIAMETER + translateX;
		double xr = xFactor * ((long) trapezoid.right.x - minX) + DIAMETER + translateX;
		int xli;
		int xri;
					
		if(xl > 1000000)
			xli = 1000000;
		else if(xl < -1000000)
			xli = -1000000;
		else
			xli = (int) Math.round(xl);
		if(xr > 1000000)
			xri = 1000000;
		else if(xr < -1000000)
			xri = -1000000;
		else
			xri = (int) Math.round(xr);
					
		xs[0] = xli;
		xs[1] = xli;
		xs[2] = xri;
		xs[3] = xri;
		return xs;
	}
	
	private int[] getYs(int[] ys, Trapezoid trapezoid, double yFactor)
	{
		Rectangle bounds = this.getBounds();
		double ylb = bounds.height - (yFactor * ((double) trapezoid.bottom.getY(trapezoid.left.x) - minY) + DIAMETER) + translateY;
		double ylt = bounds.height - (yFactor * ((double) trapezoid.top.getY(trapezoid.left.x) - minY) + DIAMETER) + translateY;
		double yrb = bounds.height - (yFactor * ((double) trapezoid.bottom.getY(trapezoid.right.x) - minY) + DIAMETER) + translateY;
		double yrt = bounds.height - (yFactor * ((double) trapezoid.top.getY(trapezoid.right.x) - minY) + DIAMETER) + translateY;
		int ylti;
		int ylbi;
		int yrti;
		int yrbi;
		
		if(ylt > 1000000)
			ylti = 1000000;
		else if(ylt < -1000000)
			ylti = -1000000;
		else
			ylti = (int) Math.round(ylt);
		if(ylb > 1000000)
			ylbi = 1000000;
		else if(ylb < -1000000)
			ylbi = -1000000;
		else
			ylbi = (int) Math.round(ylb);
		if(yrt > 1000000)
			yrti = 1000000;
		else if(yrt < -1000000)
			yrti = -1000000;
		else
			yrti = (int) Math.round(yrt);
		if(yrb > 1000000)
			yrbi = 1000000;
		else if(yrb < -1000000)
			yrbi = -1000000;
		else
			yrbi = (int) Math.round(yrb);
		
		ys[0] = ylbi;
		ys[1] = ylti;
		ys[2] = yrti;
		ys[3] = yrbi;
		return ys;
	}

	/**
	 * Get a tooltip if mouse points to a point of the polygon
	 */
	public String getToolTipText(MouseEvent evt)
	{
		Rectangle panelBounds = this.getBounds();
		double xFactor = (double) (panelBounds.width - 2 * DIAMETER) * scale
				/ (double) ((long) maxX - minX);
		double yFactor = (double) (panelBounds.height - 2 * DIAMETER) * scale
				/ (double) ((long) maxY - minY);

		int counter = 0;

		for (Point p : this.points)
		{
			double x = xFactor * ((long) p.x - minX) + DIAMETER + translateX;
			double y = yFactor * ((long) p.y - minY) + DIAMETER - translateY;
			// integer constrains (if a screen has more than 1000000 call me impressed) 
			int px = 0;
			int py = 0;
			if(x > 1000000)
				px = 1000000;
			else if(x < -1000000)
				px = -1000000;
			else
				px = (int) Math.round(x);
			if(y > 1000000)
				py = 1000000;
			else if(y < -1000000)
				py = -1000000;
			else
				py = (int) Math.round(y);
			
			py = panelBounds.height - py;
			
			// int px = (int) Math.round(x);
			// int py = panelBounds.height - (int) Math.round(y);
			Rectangle bounds = new Rectangle(px - DIAMETER, py - DIAMETER,
					2 * DIAMETER, 2 * DIAMETER);
			if (bounds.contains(evt.getPoint()))
			{
				return counter + ": " + p.x + ", " + p.y;
			}
			counter++;
		}
		return super.getToolTipText(evt);
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/

	/**
	 * @return the points
	 */
	public List<Point> getPoints()
	{
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(List<Point> points)
	{
		if (points == null)
			this.points = new ArrayList<>();
		else
			this.points = points;

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;

		for (Point point : this.points)
		{
			if (minX > point.x)
				minX = point.x;
			if (maxX < point.x)
				maxX = point.x;
			if (minY > point.y)
				minY = point.y;
			if (maxY < point.y)
				maxY = point.y;
		}

		this.setTrapezoids(null);

		this.repaint();
	}

	/**
	 * @param points the points to set
	 */
	public void setTrapezoids(Set<Trapezoid> trapezoids)
	{
		if (trapezoids == null)
			this.trapezoids = new HashSet<>();
		else
			this.trapezoids = trapezoids;

		this.repaint();
	}

	/**
	 * scrolling, lets zoom
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() < 0)
			scale *= 1.1;
		else if (e.getWheelRotation() > 0)
			scale /= 1.1;

		// why zoom out its useless though
		if (scale < 1)
			scale = 1;

		this.repaint();
	}

	/**
	 * Dragging, lets translate the view
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		translateX += e.getX() - lastLocation.x;
		translateY += e.getY() - lastLocation.y;
		lastLocation = new Point(e.getX(), e.getY());

		// only guide lines really
		if (translateX > (this.getBounds().width - DIAMETER * 2) * scale)
			translateX = (int) ((this.getBounds().width - DIAMETER * 2) * scale);
		if (translateY > (this.getBounds().height - DIAMETER * 2) * scale)
			translateY = (int) ((this.getBounds().height - DIAMETER * 2) * scale);

		if (translateX < -(this.getBounds().width - DIAMETER * 2) * scale)
			translateX = (int) (-(this.getBounds().width - DIAMETER * 2) * scale);
		if (translateY < -(this.getBounds().height - DIAMETER * 2) * scale)
			translateY = (int) (-(this.getBounds().height - DIAMETER * 2) * scale);
		
		this.repaint();
	}

	/**
	 * Update the last location of the mouse for dragging
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		 lastLocation = new Point(e.getX(),e.getY());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
