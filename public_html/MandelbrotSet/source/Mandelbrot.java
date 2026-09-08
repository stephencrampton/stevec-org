package org.stevec.fractals;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.Math;

import javax.swing.JApplet;
import javax.swing.event.MouseInputAdapter;

/**
 * @author stevec
 * This was one of my first Java applets.  It looks like C code translated to Java, which
 * is what happened in my head in the course of developing it.  It's not very object-
 * oriented.  Oh, well.
 * 
 * This class computes and draws the Mandelbrot set on an applet.  By clicking and dragging
 * the mouse, a user can zoom in and examine different parts of the set.
 */
public class Mandelbrot extends JApplet {

	private static final long serialVersionUID = -7748976827770978468L;

	int _width, _height;	
	int _clipX, _clipY, _clipW, _clipH;

	double minX, maxX, minY, maxY;
	int xDim, yDim;
	double aspectRatio;
	BufferedImage image;
	int startX, startY, lastX, lastY;
	boolean _drawSelection;
	Rectangle _selectionRect = new Rectangle();


	public void init() {
		MyListener myListener = new MyListener();
		addMouseListener(myListener);
		addMouseMotionListener(myListener);
		//
		// Initialize drawable parameters
		//
		_width = this.getSize().width;
		_height = this.getSize().height;
		xDim = _width;
		yDim = _height;
		image = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_RGB);
		_clipX = 0;
		_clipY = 0;
		_clipW = xDim;
		_clipH = yDim;
		_selectionRect.x = 0;
		_selectionRect.y = 0;
		_selectionRect.width = xDim;
		_selectionRect.height = yDim;
		_drawSelection = false;
		aspectRatio = (double)xDim / (double)yDim;
		if (aspectRatio > 1)
		{
			minY = -1.6;
			maxY = 1.6;
			minX = -2.1 * aspectRatio;
			maxX = 1.1 * aspectRatio;
		}
		else
		{
			minX = -2.1;
			maxX = 1.1;
			minY = -1.6 / aspectRatio;
			maxY = 1.6 / aspectRatio;
		}
		createMandelbrotSet(minX, maxX, minY, maxY, 255);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		if (_drawSelection)
		{
			g.setColor(Color.yellow);
			g.drawRect(_selectionRect.x, _selectionRect.y,
					_selectionRect.width - 1, _selectionRect.height - 1);
		}
	}

	public void updateIt(int x, int y, int w, int h, boolean drO)
	{
		_clipX = x;
		_clipY = y;
		_clipW = w;
		_clipH = h;
		_drawSelection = drO;
		repaint(_clipX, _clipY, _clipW, _clipH);
	}

	public static int computeRGB(int red, int green, int blue)
	{
		int alpha = 255;
		int rgb = (alpha << 24) + (red << 16) + (green << 8) + blue;
		return rgb;
	}

	void colorImage(double[][] dImage, double xDim, double yDim,
			BufferedImage image)
	{
		int r, c;
		double minV, maxV;
		double scale;
		int p;
		int i, j, k;

		minV = maxV = dImage[0][0];
		for (r = 0; r < yDim; r++)
		{
			for (c = 0; c < xDim; c++)
			{
				if (dImage[r][c] < minV) minV = dImage[r][c];
				if (dImage[r][c] > maxV) maxV = dImage[r][c];
			}
		}
		scale = (double)0xFFFFFF / (maxV - minV);
		for (r = 0; r < yDim; r++)
		{
			for (c = 0; c < xDim; c++)
			{
				int v;
				v = (int)(scale * dImage[r][c]); // between 0 and 0xFFFFFF
				i = 0;
				j = 0;
				k = 0;
				for (p = 1; p < 0x81; p = p << 1)
				{
					i += (v & 0x1) * p;
					v = v >> 1;
				j += (v & 0x1) * p;
				v = v >> 1;
				k += (v & 0x1) * p;
				v = v >> 1;
				}
				if (i < 0)
					i = 0;
				else if (i > 255)
					i = 255;
				if (j < 0)
					j = 0;
				else if (j > 255)
					j = 255;
				if (k < 0)
					k = 0;
				else if (k > 255)
					k = 255;
				int rgb = computeRGB(i, j, k);
				image.setRGB(c, r, rgb);
			}
		}
	}


	void createMandelbrotSet(double minX, double maxX, double minY, double maxY,
			int maxIterations)
	{
		double x, y, xx, oldxx, yy, a, b;
		int r, c;
		int i;
		double stepX, stepY, maxStep;
		int iters;
		double outOfBounds = 4.0;
		double[][] dImage = new double[yDim][xDim];

		stepX = (maxX - minX) / (double)xDim;
		stepY = (maxY - minY) / (double)yDim;
		if (stepX > stepY)
			maxStep = stepX;
		else
			maxStep = stepY;
		iters = (int)((double)maxIterations * 0.08 / Math.sqrt(maxStep));
		for (r = 0, y = minY; r < yDim; r++, y += stepY)
		{
			for (c = 0, x = minX; c < xDim; c++, x += stepX)
			{
				a = x;
				b = y;
				xx = a;
				yy = b;
				for (i = 1; i < iters; i++)
				{
					oldxx = xx;
					xx = xx * xx - yy * yy + a;
					yy = 2 * oldxx * yy + b;
					if (xx * xx + yy * yy > outOfBounds) break;
				}
				if (i == iters)
					i = 0;
				dImage[r][c] = (double)i;
			}
		}
		colorImage(dImage, xDim, yDim, image);
	}


	private class MyListener extends MouseInputAdapter {

		public void mouseDragged(MouseEvent e) {
			int x, y;
			int x1, x2, y1, y2;
			Rectangle cleanUp = new Rectangle();

			//
			// Determine area to clean up (from last selection)
			//
			if (lastX > startX)
			{
				x1 = startX;
				x2 = lastX;
			}
			else
			{
				x1 = lastX;
				x2 = startX;
			}
			if (lastY > startY)
			{
				y1 = startY;
				y2 = lastY;
			}
			else
			{
				y1 = lastY;
				y2 = startY;
			}
			cleanUp.x = x1;
			cleanUp.y = y1;
			cleanUp.width = x2 - x1 + 1;
			cleanUp.height = y2 - y1 + 1;
			//
			// Determine new selection area
			//
			x = e.getX();
			y = e.getY();
			if (startX > x)
			{
				x1 = x;
				x2 = startX;
			}
			else
			{
				x1 = startX;
				x2 = x;
			}
			if (startY > y)
			{
				y1 = y;
				y2 = startY;
			}
			else
			{
				y1 = startY;
				y2 = y;
			}
			_selectionRect.x = x1;
			_selectionRect.y = y1;
			_selectionRect.width = x2 - x1 + 1;
			_selectionRect.height = y2 - y1 + 1;
			//
			// Total area to be repainted is the union of both previously
			// computed rectangles.
			//
			Rectangle totalRepaint = cleanUp.union(_selectionRect);
			updateIt(totalRepaint.x, totalRepaint.y, totalRepaint.width, totalRepaint.height, true);
			//
			// Update last position
			//
			lastX = x;
			lastY = y;
		}


		public void mouseReleased(MouseEvent e) {
			int x, y;
			double scaleX, scaleY;
			double scale;
			double diffX, diffY;

			x = e.getX();
			y = e.getY();
			if (x != startX && y != startY)
			{
				double endX, endY;
				if (x > startX)
				{
					endX = x;
				}
				else
				{
					endX = startX;
					startX = x;
				}
				if (y > startY)
				{
					endY = y;
				}
				else
				{
					endY = startY;
				}
				scaleX = (double)(endX - startX) / (double)xDim;
				scaleY = (double)(endY - startY) / (double)yDim;
				if (scaleX > scaleY)
					scale = scaleX;
				else
					scale = scaleY;
				diffX = (maxX - minX) * scale;
				diffY = (maxY - minY) * scale;
				minX = minX + (double)startX * (maxX - minX) / (double)xDim;
				maxY = maxY - (double)(yDim - endY) * (maxY - minY) / (double)yDim;
				maxX = minX + diffX;
				minY = maxY - diffY;
				createMandelbrotSet(minX, maxX, minY, maxY, 45);
				updateIt(0, 0, xDim, yDim, false);
			}
		}


		public void mousePressed(MouseEvent e) { 
			startX = e.getX(); 
			startY = e.getY(); 
			lastX = startX;
			lastY = startY;
		} 
	}
}
