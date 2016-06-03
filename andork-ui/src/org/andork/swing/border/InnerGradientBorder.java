/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.border;

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

import org.andork.awt.ColorUtils;
import org.andork.awt.layout.Corner;
import org.andork.awt.layout.RectangleUtils;
import org.andork.awt.layout.Side;

public class InnerGradientBorder implements Border {
	private static Point2D.Float Point2D(Point p) {
		return new Point2D.Float(p.x, p.y);
	}

	Insets insets;
	Color outerColor;

	Color innerColor;
	int[] xpoints = new int[4];

	int[] ypoints = new int[4];

	public InnerGradientBorder(Insets insets, Color outerColor) {
		super();
		this.insets = insets;
		this.outerColor = outerColor;
		innerColor = ColorUtils.alphaColor(outerColor, 0);
	}

	public InnerGradientBorder(Insets insets, Color outerColor, Color innerColor) {
		super();
		this.insets = insets;
		this.outerColor = outerColor;
		this.innerColor = innerColor;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return (Insets) insets.clone();
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
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
}
