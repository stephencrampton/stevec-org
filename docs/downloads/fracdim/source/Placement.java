package org.stevec.fractals;

/**
 * @author stevec
 * This class is treated more like a C struct.  It holds the point which is the
 * origin of a grid of boxes (the anchor) and also the angle at which the box
 * set is rotated.
 */
public class Placement {
	public double angle;
	public Point anchor;
	
	Placement() {
		anchor = new Point();
	}
	
	public Placement copy() {
		Placement p = new Placement();
		p.angle = angle;
		p.anchor.x = anchor.x;
		p.anchor.y = anchor.y;
		return p;
	}
}
