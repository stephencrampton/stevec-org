package org.stevec.fractals;

/**
 * @author stevec
 * This class holds a point set.  It can also return scaled and rotated versions
 * of the point set.
 */
public class PointSet {
	static final int MAX_POINTS = 100000;
	
	private Point[] points;
	private int numPoints;
	private double scale;
	
	public double getScale() {
		return scale;
	}
	
	public void setScale(double s) {
		scale = s;
	}

	PointSet() {
		points = new Point[MAX_POINTS];
		numPoints = 0;
	}
	
	Point[] getPoints() {
		return points;
	}
	
	int getNumPoints() {
		return numPoints;
	}
	
	void add(double x, double y) {
		if (numPoints < MAX_POINTS)
		{
			points[numPoints] = new Point(x, y);
			numPoints++;
		}
	}

	//
	// Determine minimum and maximum X and Y values for the point set.
	//
	Rect determineBounds() {
		double minX = points[0].x;
		double minY = points[0].y;
		double maxX = points[0].x;
		double maxY = points[0].y;
		int i;
		for (i = 1; i < numPoints; i++)
		{
			if (points[i].x < minX)
				minX = points[i].x;
			if (points[i].y < minY)
				minY = points[i].y;
			if (points[i].x > maxX)
				maxX = points[i].x;
			if (points[i].y > maxY)
				maxY = points[i].y;
		}
		Rect bounds = new Rect(minX, minY, maxX, maxY);
		return bounds;
	}

	
	PointSet getScaled(double maxW, double maxH) {
		Rect bounds = determineBounds();
		double minX = bounds.minX;
		double minY = bounds.minY;
		double maxX = bounds.maxX;
		double maxY = bounds.maxY;
		double scaleW;
		if (minX == maxX)
			scaleW = -1.0;
		else
			scaleW = maxW / (maxX - minX);
		double scaleH;
		if (minY == maxY)
			scaleH = -1.0;
		else
			scaleH = maxH / (maxY - minY);
		if (scaleH < 0 && scaleW < 0)
			scale = 1.0;
		else if (scaleH < 0)
			scale = scaleW;
		else if (scaleW < 0)
			scale = scaleH;
		else if (scaleW < scaleH)
			scale = scaleW;
		else
			scale = scaleH;
		PointSet sPoints = new PointSet();
		for (int i = 0; i < numPoints; i++) {
			double x = (points[i].x - minX) * scale;
			double y = (points[i].y - minY) * scale;
			sPoints.add(x, y);
		}
		sPoints.setScale(scale);
		return sPoints;
	}

	//
	// Rotate point set by angle theta.
	//
	PointSet rotatePointSet(double theta) {
		PointSet pRot = new PointSet();
		double cosTheta, sinTheta;
		int i;
		
		cosTheta = Math.cos(theta);
		sinTheta = Math.sin(theta);
		for (i = 0; i < numPoints; i++)
		{
			double x = points[i].x;
			double y = points[i].y;
			pRot.add(cosTheta * x - sinTheta * y, sinTheta * x + cosTheta * y);
		}
		pRot.numPoints = numPoints;
		return pRot;
	}
}
