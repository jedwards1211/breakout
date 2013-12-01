package org.andork.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.border.Border;

import org.andork.awt.layout.Corner;
import org.andork.awt.layout.RectangleUtils;
import org.andork.awt.layout.Side;

public class InnerGradientBorder implements Border {
	Insets	insets;
	Color	outerColor;
	Color	innerColor;

	int[]	xpoints	= new int[4];
	int[]	ypoints	= new int[4];

	public InnerGradientBorder(Insets insets, Color outerColor, Color innerColor) {
		super();
		this.insets = insets;
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}

	public InnerGradientBorder(Insets insets, Color outerColor) {
		super();
		this.insets = insets;
		this.outerColor = outerColor;
		innerColor = ColorUtils.alphaColor(outerColor, 0);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Rectangle outerBounds = new Rectangle(x, y, width, height);
		Rectangle innerBounds = RectangleUtils.insetCopy(outerBounds, insets);

		Graphics2D g2 = (Graphics2D) g;

		Paint prevPaint = g2.getPaint();

		for (Side side : Side.values()) {
			if (side.get(insets) <= 0) {
				continue;
			}

			Corner c1 = Corner.fromSides(side, side.nextCounterClockwise());
			Corner c2 = Corner.fromSides(side, side.nextClockwise());

			int k = 0;
			Point p;
			p = c1.location(outerBounds);
			xpoints[k] = p.x;
			ypoints[k++] = p.y;
			p = c2.location(outerBounds);
			xpoints[k] = p.x;
			ypoints[k++] = p.y;
			p = c2.location(innerBounds);
			xpoints[k] = p.x;
			ypoints[k++] = p.y;
			p = c1.location(innerBounds);
			xpoints[k] = p.x;
			ypoints[k++] = p.y;

			Point2D.Float outerPoint = Point2D(side.center(outerBounds));
			Point2D.Float innerPoint = Point2D(side.center(innerBounds));

			side.axis().opposite().set(innerPoint, side.axis().opposite().get(outerPoint));

			g2.setPaint(new GradientPaint(
					outerPoint, outerColor,
					innerPoint, innerColor));

			g2.fillPolygon(xpoints, ypoints, 4);
		}

		g2.setPaint(prevPaint);
	}

	private static Point2D.Float Point2D(Point p) {
		return new Point2D.Float(p.x, p.y);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return (Insets) insets.clone();
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}
}
