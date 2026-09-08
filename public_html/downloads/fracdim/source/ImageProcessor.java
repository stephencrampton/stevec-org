package org.stevec.fractals;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * @author stevec
 * This class exists primarily to extract a point set from an image.  A simple thresholding
 * algorithm is used so the program can process non-binary images.
 */
public class ImageProcessor {

	private BufferedImage displayImage;
	public static final int COLORDEPTH = 4;
	public static final int ALPHA = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int BLUE = 3;
	public static final int COLORVALUES = 256;
	public static final int MAXCOLOR = COLORVALUES - 1;

	public static int getRed(int rgb)
	{
		return (rgb & 0xFF0000) >> 16;
	}

	public static int getGreen(int rgb)
	{
		return (rgb & 0xFF00) >> 8;
	}

	public static int getBlue(int rgb)
	{
		return (rgb & 0xFF);
	}
	
	public static int computeRGB(int alpha, int red, int green, int blue)
	{
		int rgb = (alpha << 24) + (red << 16) + (green << 8) + blue;
		return rgb;
	}

	int countNumPoints(double threshold)
	{
		int n = 0;
		for (int i = 0; i < displayImage.getWidth(); i++)
		{
			for (int j = 0; j < displayImage.getHeight(); j++)
			{
				int rgb = displayImage.getRGB(i, j);
				double gray = getRed(rgb);
				if (gray > threshold)
					n++;
			}
		}
		return n;
	}
	
	PointSet extractPoints(BufferedImage im)
	{
		displayImage = im;
		int w = displayImage.getWidth();
		int h = displayImage.getHeight();
		/*
		 * First we auto-adjust the image contrast across the color channels to make
		 * it easier to threshold.
		 */
		double minColor = MAXCOLOR;
		double maxColor = 0;
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				int rgb = displayImage.getRGB(i, j);
				double red = getRed(rgb);
				double green = getGreen(rgb);
				double blue = getBlue(rgb);
				if (red < minColor)
					minColor = red;
				if (red > maxColor)
					maxColor = red;
				if (green < minColor)
					minColor = green;
				if (green > maxColor)
					maxColor = green;
				if (blue < minColor)
					minColor = blue;
				if (blue > maxColor)
					maxColor = blue;
			}
		}
		double scale;
		if (minColor == maxColor)
			scale = 1.0;
		else
			scale = MAXCOLOR / (maxColor - minColor);
		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				int rgb = displayImage.getRGB(i, j);
				double red = getRed(rgb);
				double green = getGreen(rgb);
				double blue = getBlue(rgb);
				red = (red - minColor) * scale;
				green = (green - minColor) * scale;
				blue = (blue - minColor) * scale;
				double gray = (red + green + blue) / 3.0;
				rgb = computeRGB(MAXCOLOR, (int)gray, (int)gray, (int)gray);
				displayImage.setRGB(i, j, rgb);
			}
		}
		/*
		 * Next we use a simple adaptive thresholding algorithm.  We start with a threshold in
		 * the middle.  We take whichever half of the image has the fewest points.  If there
		 * are more than the maximum number of points, we subdivide that half of the image and
		 * repeat.
		 */
		PointSet pSet = new PointSet();
		double threshold = MAXCOLOR / 2.0;
		int totPoints = w * h;
		int numPoints = countNumPoints(threshold);
		if (numPoints > PointSet.MAX_POINTS && totPoints - numPoints > PointSet.MAX_POINTS)
		{
			if (numPoints < totPoints - numPoints)
			{
				double slice = threshold / 2.0;
				while (numPoints > PointSet.MAX_POINTS)
				{
					threshold += slice;
					numPoints = countNumPoints(threshold);
					slice /= 2.0;
				}
			}
			else
			{
				double slice = threshold / 2.0;
				while (totPoints - numPoints > PointSet.MAX_POINTS)
				{
					threshold -= slice;
					numPoints = countNumPoints(threshold);
					slice /= 2.0;
				}
			}
		}
		if (numPoints < totPoints - numPoints)
		{
			for (int i = 0; i < w; i++)
			{
				for (int j = 0; j < h; j++)
				{
					int rgb = displayImage.getRGB(i, j);
					double gray = getRed(rgb);
					if (gray > threshold)
					{
						pSet.add(i, j);
						rgb = computeRGB(MAXCOLOR, MAXCOLOR, MAXCOLOR, MAXCOLOR);
						displayImage.setRGB(i, j, rgb);
					}
					else
					{
						rgb = computeRGB(MAXCOLOR, 0, 0, 0);
						displayImage.setRGB(i, j, rgb);
					}
				}
			}
			
		}
		else
		{
			for (int i = 0; i < w; i++)
			{
				for (int j = 0; j < h; j++)
				{
					int rgb = displayImage.getRGB(i, j);
					double gray = getRed(rgb);
					if (gray <= threshold)
					{
						pSet.add(i, j);
						rgb = computeRGB(MAXCOLOR, MAXCOLOR, MAXCOLOR, MAXCOLOR);
						displayImage.setRGB(i, j, rgb);
					}
					else
					{
						rgb = computeRGB(MAXCOLOR, 0, 0, 0);
						displayImage.setRGB(i, j, rgb);
					}
				}
			}
		}
		return pSet;
	}
	
	/**
	 * This is a helper function that converts an Image to a BufferedImage.
	 * @param img the Image
	 * @return the BufferedImage
	 */
	public static BufferedImage imageToBuffered(Image img)
	{
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = bImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bImage;
	}
}