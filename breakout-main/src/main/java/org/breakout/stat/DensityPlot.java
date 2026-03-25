package org.breakout.stat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.function.DoubleFunction;

import javax.swing.JComponent;

import com.andork.plot.LinearAxisConversion;
import com.andork.plot.PlotAxis;
import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;

@SuppressWarnings("serial")
public class DensityPlot extends JComponent {
	private double rangeMin = Double.NaN;
	private double rangeMax = Double.NaN;
	private DoubleFunction<Double> densityFn;
	private MainPlot mainPlot;
	private PlotAxis plotAxis;
	private LinearAxisConversion axisConversion = new LinearAxisConversion(0, 0, 1, 1);

	public DensityPlot() {
		mainPlot = new MainPlot();
		mainPlot.setForeground(Color.BLUE);
		plotAxis = new PlotAxis(Orientation.HORIZONTAL, LabelPosition.BOTTOM);
		plotAxis.setMinorTickSize(1);
		plotAxis.setMajorTickSize(3);

		setLayout(new BorderLayout());
		add(mainPlot, BorderLayout.CENTER);
		add(plotAxis, BorderLayout.SOUTH);
	}

	public DensityPlot setRange(double rangeMin, double rangeMax) {
		if (this.rangeMin != rangeMin || this.rangeMax != rangeMax) {
			this.rangeMin = rangeMin;
			this.rangeMax = rangeMax;
			repaint();
		}
		return this;
	}

	public DensityPlot setDensityFn(DoubleFunction<Double> densityFn) {
		if (this.densityFn != densityFn) {
			this.densityFn = densityFn;
			repaint();
		}
		this.setBounds(new Rectangle());
		return this;
	}

	public class MainPlot extends JComponent {
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (Double.isNaN(rangeMin) || Double.isNaN(rangeMax)) {
				axisConversion = null;
			}
			else {
				axisConversion = new LinearAxisConversion(rangeMin, 0, rangeMax, this.getWidth());
				plotAxis.setAxisConversion(axisConversion);
			}

			if (axisConversion == null || densityFn == null)
				return;

			Rectangle bounds = getBounds();
			bounds.x = 0;
			bounds.y = 0;

			Graphics2D g2 = (Graphics2D) g;

			Object origAntialiasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			Paint origPaint = g2.getPaint();
			Stroke origStroke = g2.getStroke();
			AffineTransform origTransform = g2.getTransform();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Path2D.Double path = new Path2D.Double();

			double maxDensity = 0.001;

			path.moveTo(0, 0);
			double x;
			for (x = 0; x <= bounds.width; x++) {
				double density = densityFn.apply(axisConversion.invert(x));
				if (density > maxDensity)
					maxDensity = density;
				path.lineTo(x, density);
			}
			path.lineTo(x, 0);
			path.closePath();

			g2.translate(0, bounds.height);
			g2.scale(1, -bounds.height / maxDensity);

			g2.fill(path);

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, origAntialiasing);
			g2.setPaint(origPaint);
			g2.setStroke(origStroke);
			g2.setTransform(origTransform);

		}
	}
}
