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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ImagePlotLayer implements IPlotLayer {
	LinearAxisConversion xAxisConversion;
	LinearAxisConversion yAxisConversion;

	Rectangle2D src;
	Rectangle2D dest;

	Image image;

	public ImagePlotLayer(LinearAxisConversion xAxisConversion, LinearAxisConversion yAxisConversion) {
		super();
		this.xAxisConversion = xAxisConversion;
		this.yAxisConversion = yAxisConversion;
	}

	public Rectangle2D getDest() {
		return dest;
	}

	public Image getImage() {
		return image;
	}

	public Rectangle2D getSrc() {
		return src;
	}

	public LinearAxisConversion getxAxisConversion() {
		return xAxisConversion;
	}

	public LinearAxisConversion getyAxisConversion() {
		return yAxisConversion;
	}

	@Override
	public void render(Graphics2D g2, Rectangle bounds) {
		if (image == null || xAxisConversion == null || yAxisConversion == null || src == null || dest == null) {
			return;
		}

		double dx = xAxisConversion.convert(dest.getX());
		double dy = yAxisConversion.convert(dest.getY());
		double dw = xAxisConversion.convert(dest.getMaxX()) - dx;
		double dh = yAxisConversion.convert(dest.getMaxY()) - dy;

		int sx1 = (int) Math.floor(src.getX());
		int sx2 = (int) Math.ceil(src.getMaxX());
		int sy1 = (int) Math.floor(src.getY());
		int sy2 = (int) Math.ceil(src.getMaxY());

		AffineTransform prevXform = g2.getTransform();

		g2.translate(dx - src.getX(), dy - src.getY());
		g2.scale(dw / src.getWidth(), dh / src.getHeight());
		g2.drawImage(image, sx1, sy1, sx2, sy2, sx1, sy1, sx2, sy2, null);

		g2.setTransform(prevXform);
	}

	public void setDest(Rectangle2D dest) {
		this.dest = dest;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setSrc(Rectangle2D src) {
		this.src = src;
	}

	public void setxAxisConversion(LinearAxisConversion xAxisConversion) {
		this.xAxisConversion = xAxisConversion;
	}

	public void setyAxisConversion(LinearAxisConversion yAxisConversion) {
		this.yAxisConversion = yAxisConversion;
	}
}
