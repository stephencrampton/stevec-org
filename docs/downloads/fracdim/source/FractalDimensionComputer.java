package org.stevec.fractals;

import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import org.stevec.fractals.FracDim.ImagePanel;

public class FractalDimensionComputer {

	static final int ANGLE_TRIALS = 100;
	static final int PLACEMENT_TRIALS = 100;
	static final int MAX_BOX_DIM = 10;
	static final double STARTING_DIM = 8.0;
	static final double PI = 3.1415926537;
	static final double SMALL_FLOAT = 0.00001;
	
	private PointSet pSet;
	private int boxesFilled;
	
	/**
	 * Given a grid anchor point and box size, compute the number of boxes covering
	 * points in the data set.  Return the number of boxes required to cover the point
	 * set.
	 * @param pSet the point set
	 * @param anchor the anchor point
	 * @param boxDim the box dimension tested
	 * @param boxSize the box size
	 * @return the number of boxes filled
	 */
	int computeBoxesFilled(PointSet pSet, Point anchor, double boxDim, double boxSize)
	{
		int pointsInBoxDim = (int)boxDim + 1;
		int[][] pointsInBox = new int[pointsInBoxDim][pointsInBoxDim];

		//
		// Initialize record-keeping variables to 0.
		//
		for (int r = 0; r < pointsInBoxDim; r++)
			for (int c = 0; c < pointsInBoxDim; c++)
				pointsInBox[r][c] = 0;
		boxesFilled = 0;
		//
		// For each point, check to see which box it inhabits; increment the
		// corresponding element in the pointsInBox array and increment
		// (total) boxesFilled.
		//
		Point[] points = pSet.getPoints();
		for (int p = 0; p < pSet.getNumPoints(); p++)
		{
			int r = (int)((points[p].y - anchor.y) / boxSize);
			int c = (int)((points[p].x - anchor.x) / boxSize);
			if (pointsInBox[r][c] == 0)
				boxesFilled = boxesFilled + 1;
			pointsInBox[r][c] = pointsInBox[r][c] + 1;
		}
		return boxesFilled;
	}


	//
	// Get a random angle between 0 and 90 degrees.
	//
	double getRandomAngle()
	{
		return Math.random() * Math.PI / 2.0;
	}


	//
	// Get a random anchor point for the grid.  It must be below and to the left
	// of every point in the point set.  Because the grid is periodic, the range
	// random numbers considered is simply the size of one grid box.
	//
	Point getRandomAnchorPoint(Rect bounds, double boxSize)
	{
		Point anchor = new Point();
		anchor.x = bounds.minX - Math.random() * boxSize;
		anchor.y = bounds.minY - Math.random() * boxSize;
		return anchor;
	}


	//
	// This function returns the size of a box that will cover the entire data set
	// regardless of how it is eventually rotated.
	//
	double getInitialBoxSize()
	{
		double boxSize;
		Rect bounds = new Rect();

		bounds = pSet.determineBounds();
		boxSize = bounds.maxX - bounds.minX;
		if (boxSize < bounds.maxY - bounds.minY)
			boxSize = bounds.maxY - bounds.minY;
		boxSize *= 1.414213562373096; // enlarge box size to account for point-set rotations
		return boxSize;
	}

	//
	// Use a Monte Carlo approach to estimate the minimum boxes filled by the point
	// set.  The parameters varied are the angle of the point set with respect to the
	// grid and the placement of the grid anchor point.
	//
	int estimateMinBoxesFilled(double boxDim, double boxSize, Placement placement)
	{
		int i, j; // loop variables
		double theta; // angle of rotation for the point set with respect to the grid
		Point anchor = new Point(); // grid anchor point
		int boxesFilled; // this is what we are trying to estimate
		int minBoxesFilled;
	
		minBoxesFilled = pSet.getNumPoints() + 1;
		for (i = 0; i < ANGLE_TRIALS; i++)
		{
			theta = getRandomAngle();
			PointSet pRot = pSet.rotatePointSet(theta);
			Rect bounds = pRot.determineBounds();
			for (j = 0; j < PLACEMENT_TRIALS; j++)
			{
				anchor = getRandomAnchorPoint(bounds, boxSize);
				boxesFilled = computeBoxesFilled(pRot, anchor, boxDim, boxSize);
				if (boxesFilled < minBoxesFilled)
				{
					minBoxesFilled = boxesFilled;
					placement.angle = theta;
					placement.anchor.x = anchor.x;
					placement.anchor.y = anchor.y;
				}
			}
		}
		return minBoxesFilled;
	}


	/**
	 * Sleep for a number of seconds.
	 * @param seconds
	 */
	private void sleep(int seconds)
	{
		try
		{
			Thread.currentThread();
			Thread.sleep(1000 * seconds);
		}
		catch(InterruptedException ie)
		{
			;
		}
	}


	//
	// Estimate the fractal dimension as follows:
	// 1. Use getInitialBoxSize above to set the unit box size to be the smallest
	//	    that will contain the point set under any rotation.
	//
	// 2. boxDim represents the number of boxes that will fit into the unit box in
	//	    any dimension.  For instance, boxDim = 2.0 means that 2 boxes will fit into
	//	    the unit box going lengthwise and 2 boxes stacked, for a total of 4 boxes.
	//	    Note that boxDim need not be an integer.
	//
	// 3. For boxDims beginning with STARTING_DIM and ending with ENDING_DIM, estimate
	//	    the minimum boxes it takes to cover the data set.  A typical series of boxDims
	//	    might be 8, 16, 32, 64, and 128, although the ENDING_DIM is limited by the size
	//	    of the data set.
	//
	// 4. The fractional dimension is estimated as the slope of the linear regression
	//	    on the logarithms of the boxDims and the minimum number of boxes at each boxDim.
	//
	// The program prints out the minimum box covering at each boxDim as it is computed.
	//
	
	BoxStats estimateFractalDimension(PointSet pointSet, BufferedImage bImage, ImagePanel iPanel,
			JLabel statusLabel)
	{
		Placement placement = new Placement();
		pSet = pointSet;
		BoxStats bStats = new BoxStats(MAX_BOX_DIM);		
		if (pSet.getNumPoints() == 0)
			return bStats;
		double boxDim = STARTING_DIM;
		double boxSize = getInitialBoxSize() / boxDim;
		double maxDim = STARTING_DIM * Math.pow(2.0, Math.log10(pSet.getNumPoints())) / 2;
		BoxDrawer bDrawer = new BoxDrawer(pSet, bImage, iPanel);
		System.out.printf("\t\tBox\t\t\t\t\tBox\n");
		System.out.printf("Boxes\t\tDim\t\t(X0, Y0)\t\tSize\t\tTheta\n");
		do // estimate the minimum boxes filled at each boxDim
		{
			boxesFilled = estimateMinBoxesFilled(boxDim, boxSize, placement);
			System.out.printf("%d\t\t1/%5.2f\t\t(%6.2f,%6.2f)\t%9.2f\t%9.2f\n", boxesFilled,
					boxDim, placement.anchor.x, placement.anchor.y, boxSize, 180.0 / PI * placement.angle);
			bStats.add(boxDim, boxesFilled, placement, boxSize);
			bDrawer.drawPoints();
			bDrawer.drawBoxes(placement, boxDim, boxSize);
			statusLabel.setText(boxesFilled + " boxes -- Computing . . .");
			statusLabel.paintImmediately(0, 0, statusLabel.getWidth(), statusLabel.getHeight());
			boxDim = boxDim * 2.0;
			boxSize = boxSize / 2.0;
		}
		while (boxDim <= maxDim + SMALL_FLOAT);
		System.out.printf("Estimated Fractal Dimension = %f\n", bStats.getFracDim());
		sleep(5);
		bDrawer.drawPoints();
		return bStats;
	}
}
