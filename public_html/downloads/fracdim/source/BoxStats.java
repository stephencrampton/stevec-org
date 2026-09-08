package org.stevec.fractals;

/**
 * @author stevec
 * Class to maintain the data accumulated by the fractal-dimension estimator at each
 * box size tested.
 */
public class BoxStats {

	private int maxDim;
	private int curDim;
	private double[] boxDim;
	private int[] boxesFilled;
	private Placement[] placement;
	private double[] boxSize;
	private double fracDim;

	BoxStats(int maxD)
	{
		maxDim = maxD;
		curDim = -1;
		boxDim = new double[maxD];
		boxesFilled = new int[maxD];
		placement = new Placement[maxD];
		boxSize = new double[maxD];
		fracDim = 1.0;
	}
	
	public void add(double bD, int bF, Placement pl, double bS) {
		if (curDim < maxDim - 1) {
			curDim++;
			boxDim[curDim] = bD;
			boxesFilled[curDim] = bF;
			placement[curDim] = pl.copy();
			boxSize[curDim] = bS;
			fracDim = logRegression(boxDim, boxesFilled, curDim + 1);
			fracDim = Math.round(fracDim * 100.0) / 100.0;
		}
	}
	
	public int getNumDims() {
		return curDim;
	}
	
	public double getFracDim() {
		return fracDim;
	}

	public Placement getPlacement(int i) {
		if (i < 0 || i > curDim)
			return null;
		else
			return placement[i];
	}
	
	public double getBoxDim(int i) {
		if (i < 0 || i > curDim)
			return -1.0;
		else
			return boxDim[i];
	}
	
	public double getBoxSize(int i) {
		if (i < 0 || i > curDim)
			return -1.0;
		else
			return boxSize[i];
	}
	
	public int getBoxesFilled(int i) {
		if (i < 0 || i > curDim)
			return -1;
		else
			return boxesFilled[i];
	}

	//
	// Return the slope component of a linear regression on log(X) and log(Y).
	//
	private double logRegression(double[] X, int[] Y, int N)
	{
		int i;
		double mX, mY, vX, vXY; // means, (co)variances
		double dX, dY; // used in computing variances
		
		//
		// Dispose of trivial cases.
		//
		if (N < 2) return 0.0;
		else if (N < 3) return 1.0;
		//
		// Find means.
		//
		mX = mY = 0.0;
		for (i = 0; i < N; i++)
		{
			mX += Math.log(X[i]);
			mY += Math.log((double)Y[i]);
		}
		mX /= (double)N;
		mY /= (double)N;
		//
		// Compute x variance and covariance.
		//
		vX = vXY = 0.0;
		for (i = 0; i < N; i++)
		{
			dX = Math.log(X[i]) - mX;
			vX += dX * dX;
			dY = Math.log(Y[i]) - mY;
			vXY += dX * dY;
		}
		//
		// Return slope of regression line.
		//
		return vXY / vX;
	}

}
