package org.stevec.fractals;

/**
 * @author stevec
 * A fairly self-explanatory class.  This class is treated like a C struct.  We
 * do not use getters and setters for efficiency.
 */
public class Point {
	public double x, y;
	
	Point(double newX, double newY) {
		x = newX;
		y = newY;
	}
	
	Point() {
		x = 0.0;
		y = 0.0;
	}
}
