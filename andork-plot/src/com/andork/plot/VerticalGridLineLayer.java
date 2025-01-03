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
package com.andork.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.andork.plot.PlotAxis.Orientation;

public class VerticalGridLineLayer implements IPlotLayer {
	private LinearAxisConversion axisConversion = new LinearAxisConversion();
	private int minMinorGridLineSpacing = 30;

	public VerticalGridLineLayer() {

	}

	public VerticalGridLineLayer(PlotAxis axis) {
		if (axis.getOrientation() != Orientation.HORIZONTAL) {
			throw new IllegalArgumentException("axis must be HORIZONTAL");
		}
		axisConversion = axis.getAxisConversion();
	}

	public LinearAxisConversion getAxisConversion() {
		return axisConversion;
	}

	public int getMinMinorGridLineSpacing() {
		return minMinorGridLineSpacing;
	}

	@Override
	public void render(Graphics2D g2, Rectangle bounds) {
		Object prevAntialiasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Paint prevPaint = g2.getPaint();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double axisStart = axisConversion.invert(bounds.getMinX());
		double axisEnd = axisConversion.invert(bounds.getMaxX());

		double minorVerticalGridLineSpacing = GridMath
				.niceCeiling(axisConversion.invert(bounds.getMinX() + minMinorGridLineSpacing)
						- axisConversion.invert(bounds.getMinX()));
		double majorVerticalGridLineSpacing = minorVerticalGridLineSpacing * 2;

		g2.setColor(Color.LIGHT_GRAY);
		PlotUtils.drawVerticalGridLines(g2, bounds, axisStart, axisEnd, minorVerticalGridLineSpacing);
		g2.setColor(Color.GRAY);
		PlotUtils.drawVerticalGridLines(g2, bounds, axisStart, axisEnd, majorVerticalGridLineSpacing);

		g2.setPaint(prevPaint);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, prevAntialiasing);
	}

	public void setAxisConversion(LinearAxisConversion axisConversion) {
		if (axisConversion == null) {
			throw new IllegalArgumentException("axisConversion must be non-null");
		}
		this.axisConversion = axisConversion;
	}

	public void setMinMinorGridLineSpacing(int minMinorGridLineSpacing) {
		this.minMinorGridLineSpacing = minMinorGridLineSpacing;
	}
}
