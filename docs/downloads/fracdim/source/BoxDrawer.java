package org.stevec.fractals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.stevec.fractals.FracDim.ImagePanel;

/**
 * @author stevec
 * This class draws a point set or the boxes in a box dimension on a BufferedImage
 * on an ImagePanel.
 */
public class BoxDrawer
{

	PointSet pSet;
	BufferedImage bImage;
	ImagePanel iPanel;

	public BoxDrawer(PointSet pS, BufferedImage bI, ImagePanel iP)
	{
		pSet = pS;
		bImage = bI;
		iPanel = iP;
	}

	public void drawBoxes(Placement placement, double boxDim, double boxSize)
	{
		int wImg = bImage.getWidth();
		int hImg = bImage.getHeight();
		PointSet pRot = pSet.rotatePointSet(placement.angle);
		Point[] rPoints = pRot.getPoints();
		double sinA = Math.sin(-placement.angle);
		double cosA = Math.cos(-placement.angle);
		Rect rBounds = pSet.determineBounds();
		double rMinX = rBounds.minX;
		double rMinY = rBounds.minY;
		double rRangeX = rBounds.maxX - rBounds.minX;
		double rRangeY = rBounds.maxY - rBounds.minY;
		double rScaleX, rScaleY;
		if (rRangeX == 0.0)
			rScaleX = 1.0;
		else
			rScaleX = (double)(wImg - 1) / rRangeX;
		if (rRangeY == 0.0)
			rScaleY = 1.0;
		else
			rScaleY = (double)(hImg - 1) / rRangeY;
		double rScale;
		if (rScaleX < rScaleY)
			rScale = rScaleX;
		else
			rScale = rScaleY;
		int emptyDim = (int)boxDim + 1;
		boolean[][] empty = new boolean[emptyDim][emptyDim];
		for (int r = 0; r < emptyDim; r++)
			for (int c = 0; c < emptyDim; c++)
				empty[r][c] = true;
		Graphics gFractal = bImage.getGraphics();
		for (int p = 0; p < pRot.getNumPoints(); p++)
		{
			int r = (int)((rPoints[p].y - placement.anchor.y) / boxSize);
			int c = (int)((rPoints[p].x - placement.anchor.x) / boxSize);
			if (empty[r][c])
			{
				double r1 = (double)r * boxSize + placement.anchor.y;
				double c1 = (double)c * boxSize + placement.anchor.x;
				double r2 = r1 + boxSize;
				double c2 = c1 + boxSize;
				double x1R = cosA * c1 - sinA * r1; // (c1, r1)
				double y1R = sinA * c1 + cosA * r1;
				double x2R = cosA * c1 - sinA * r2; // (c1, r2)
				double y2R = sinA * c1 + cosA * r2;
				double x3R = cosA * c2 - sinA * r2; // (c2, r2)
				double y3R = sinA * c2 + cosA * r2;
				double x4R = cosA * c2 - sinA * r1; // (c2, r1)
				double y4R = sinA * c2 + cosA * r1;
				int x1 = (int)((x1R - rMinX) * rScale);
				int y1 = (int)((y1R - rMinY) * rScale);
				int x2 = (int)((x2R - rMinX) * rScale);
				int y2 = (int)((y2R - rMinY) * rScale);
				int x3 = (int)((x3R - rMinX) * rScale);
				int y3 = (int)((y3R - rMinY) * rScale);
				int x4 = (int)((x4R - rMinX) * rScale);
				int y4 = (int)((y4R - rMinY) * rScale);
				ClippedLine l = new ClippedLine(x1, y1, x2, y2, 0, 0, wImg - 1, hImg - 1);
				if (l.isClippable())
				{
					gFractal.setColor(Color.yellow);
					gFractal.drawLine((int)l.x1, (int)l.y1, (int)l.x2, (int)l.y2);
				}
				l = new ClippedLine(x2, y2, x3, y3, 0, 0, wImg - 1, hImg - 1);
				if (l.isClippable())
				{
					gFractal.setColor(Color.yellow);
					gFractal.drawLine((int)l.x1, (int)l.y1, (int)l.x2, (int)l.y2);
				}
				l = new ClippedLine(x3, y3, x4, y4, 0, 0, wImg - 1, hImg - 1);
				if (l.isClippable())
				{
					gFractal.setColor(Color.yellow);
					gFractal.drawLine((int)l.x1, (int)l.y1, (int)l.x2, (int)l.y2);
				}
				l = new ClippedLine(x4, y4, x1, y1, 0, 0, wImg - 1, hImg - 1);
				if (l.isClippable())
				{
					gFractal.setColor(Color.yellow);
					gFractal.drawLine((int)l.x1, (int)l.y1, (int)l.x2, (int)l.y2);
				}
			}
			empty[r][c] = false;
		}
		iPanel.paintImmediately(0, 0, iPanel.getWidth(), iPanel.getHeight());
	}

	public void drawPoints()
	{
		int wImg = bImage.getWidth();
		int hImg = bImage.getHeight();
		Point[] points = pSet.getPoints();
		Rect bounds = pSet.determineBounds();
		double minX = bounds.minX;
		double minY = bounds.minY;
		double rangeX = bounds.maxX - bounds.minX;
		double rangeY = bounds.maxY - bounds.minY;
		double scaleX, scaleY;
		if (rangeX == 0.0)
			scaleX = 1.0;
		else
			scaleX = (double)(wImg - 1) / rangeX;
		if (rangeY == 0.0)
			scaleY = 1.0;
		else
			scaleY = (double)(hImg - 1) / rangeY;
		double scale;
		if (scaleX < scaleY)
			scale = scaleX;
		else
			scale = scaleY;
		Graphics gFractal = bImage.getGraphics();
		gFractal.setColor(Color.black);
		gFractal.fillRect(0, 0, wImg, hImg);
		for (int p = 0; p < pSet.getNumPoints(); p++)
		{
			gFractal.setColor(Color.white);
			double x = (points[p].x - minX) * scale;
			double y = (points[p].y - minY) * scale;
			gFractal.drawRect((int)x, (int)y, 0, 0);
		}
		iPanel.paintImmediately(0, 0, iPanel.getWidth(), iPanel.getHeight());
	}
}
