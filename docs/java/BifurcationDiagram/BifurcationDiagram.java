/*
 * @(#)Test.java 1.0 05/10/25
 *
 * You can modify the template of this file in the
 * directory ..\JCreator\Templates\Template_2\Project_Name.java
 *
 * You can also create your own project template by making a new
 * folder in the directory ..\JCreator\Template\. Use the other
 * templates as examples.
 *
 */

import java.awt.*;
import java.applet.*;
import java.util.Random;


public class BifurcationDiagram extends Applet {
	
	int _w, _h;
	double _minX, _minY;
	double _scaleX, _scaleY;
	double _drawW;
	Random generator = new Random();

//
//*****************************************************************************
//
// pseudo-VB graphics functions
//
//*****************************************************************************
//	
	public void Scale(double mX, double mY, double maxX, double maxY) {
		_minX = mX;
		_minY = mY;
		_scaleX = maxX - _minX;
		_scaleY = maxY - _minY;
	}
	
	public void setDrawWidth(double dW) {
		_drawW = dW;
	}

	public void Pset(Graphics g, double x, double y) {
		int i, j, dW;
		i = (int)((x - _minX) / _scaleX * (double)_w - _drawW / 2.0);
		j = (int)((y - _minY) / _scaleY * (double)_h - _drawW / 2.0);
		dW = (int)_drawW;
		g.fillOval(i, j, dW, dW);
	}
//
//*****************************************************************************
//	
	
	public void init() {
		//
		// Initialize drawable parameters
		//
		_w = this.getSize().width;
		_h = this.getSize().height;
		Scale(0.0, 1.0, 1.0, 0.0);
		setDrawWidth(1.0);
		//
		// Set applet color (optional)
		//
		setBackground(Color.gray);
	}

	public void paint(Graphics g) {
		// we are plotting R versus X (X is the dependent variable):
		double minR, maxR, minX, maxX;
		double R, x;
		double stepSize; // for drawing the graph
		int i; // loop variable
		int maxIters; // iterations
		//
		stepSize = 0.0001;
		maxIters = 1000;
		//
		// Scale the applet (the size is determined in the HTML code):
		//
		minR = 0.0;
		maxR = 4.0;
		minX = -0.1;
		maxX = 1.1;
		Scale(minR, maxX, maxR, minX);
		//
		// Set draw width and color (optional):
		//
		setDrawWidth(1.0);
		g.setColor(Color.red);
		for (R = minR; R <= maxR; R += stepSize)
		{
			x = generator.nextDouble();
			for (i = 0; i < maxIters; i++) {
				x = R * x * (1 - x);
			}
			Pset(g, R, x);
		}
	}
}
