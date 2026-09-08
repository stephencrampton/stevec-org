//
// To use the SimpleUI elements, click on "Project ... Properties."
// Then click on "Java Build Path" on the left and then on the
// "Libraries" tab on the right.  Click on "Add External Jars" and
// add the file SimpleUI.jar
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Random;

import simpleUI.*;

//Declare your class as follows:

public class QuadGrapher extends SimpleApplet {

	// Eclipse adds the serial number:
	private static final long serialVersionUID = -4504364543408153118L;

	// Declare your user interface elements, in this case,
	// a vertical scroll bar, a horizontal scroll bar, a picture box,
	// and a label.
	VScrollBar v;
	HScrollBar h;
	PicBox p;
	SimpleLabel l;
	SimpleButton b;

	//
	// This is where you set up your user interface:
	public void start() {

		// The following line allows us to use absolute
		// positioning:
		setLayout(null);

		// Set the size of the applet
		setSize(new Dimension(400, 400));

		// Set the background color of the applet:
		setBackground(Color.BLACK); // Color name

		// Here's how you add a horizontal scroll bar that is
		// positioned at (105, 10) on your applet:
		h = new HScrollBar(105, 10);
		h.Scale(-2, 2, 0); // from -2 to 2 with initial value 0
		h.SetBackground(new Color(0, 100, 0)); // RGB values
		h.SetForeground(Color.WHITE); // font color for label
		add(h);

		// Here's how you add a vertical scroll bar that is
		// positioned at (5, 105) on your applet:
		v = new VScrollBar(5, 105);
		v.Scale(-2, 2, 0); // from -2 to 2 with initial value 0
		v.SetBackground(new Color(0, 100, 0)); // RGB values
		v.SetForeground(Color.WHITE); // font color for label
		add(v);

		// Here's how you add a picture box that is
		// positioned at (60, 35) on your applet and
		// is 325 pixels wide by 325 pixels high:
		p = new PicBox(65, 35, 325, 325);
		p.Scale(-2, -2, 2, 2);
		p.SetBackground(new Color(0xf0f8ff)); // color hex code
		add(p);

		// Here's how you add a label that is positioned
		// at (120, 370) on your applet and is 200 pixels
		// wide by 20 pixels high:
		l = new SimpleLabel(190, 370, 200, 20);
		l.SetBackground(new Color(0, 100, 0));
		l.SetFont("Courier", Font.PLAIN, 16);
		l.SetForeground(Color.YELLOW);
		add(l);
		
		// Here's a simmple button at (5, 365) on the applet.
		b = new SimpleButton(5, 365, "Random Parameters");
		add(b);
		
		// The following line starts automatic redrawing:
		(new Thread(this)).start();
	}

	//
	// The following method controls the redrawing:
	//
	public void run() {
		double x, y;
		double A, B;

		// "while (true)" means keep redrawing forever:
		while (true) {

			// The following code limits the redraw to once
			// every 100 milliseconds (0.1 seconds):
			try {
				Thread.sleep(100);
			}
			catch (Exception e) { }

			//
			// The following code draws your graph:
			//
			p.Cls();
			p.Line(-2, 0, 2, 0, Color.BLACK);
			p.Line(0, -2, 0, 2, Color.BLACK);
			
			if (b.WasPressed()) {
				h.SetValue((new Random()).nextDouble() * 4.0 - 2.0);
				v.SetValue((new Random()).nextDouble() * 4.0 - 2.0);
			}
				A = h.GetValue();
				B = v.GetValue();
			for (x = -2; x <= 2; x = x + 0.01) {
				y = A * x * x + B;
				p.PSet(x, y, Color.RED);
			}

			// This code updates your label:
			if (B >= 0.0) {
				l.SetText("y = " + String.format("%.2f", A) + " x^2 + " + String.format("%.2f", B));
			} else {
				l.SetText("y = " + String.format("%.2f", A) + " x^2 - " + String.format("%.2f", -B));
			}

			// You need the following line to tell Java to
			// do the redraw:
			p.repaint();
		}
	}
}
