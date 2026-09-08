package org.stevec.fractals;

/**
 * @author stevec
 * This class is treated like a C struct.  It holds the dimensions of a rectangle.
 */
public class Rect {
	double minX, minY, maxX, maxY;
	
	Rect(double minXX, double minYY, double maxXX, double maxYY) {
		minX = minXX;
		minY = minYY;
		maxX = maxXX;
		maxY = maxYY;
	}
	
	Rect() {
		minX = 0.0;
		minY = 0.0;
		maxX = 0.0;
		maxY = 0.0;
	}
}
