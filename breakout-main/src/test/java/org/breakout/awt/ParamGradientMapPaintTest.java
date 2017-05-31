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
package org.breakout.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.andork.swing.QuickTestFrame;

import com.andork.plot.ImagePlotPanel;

public class ParamGradientMapPaintTest {
	public static void main(String[] args) {
		int width = 256;
		int height = 256;

		ParamGradientMapPaint paint = new ParamGradientMapPaint(
				new float[] { 0, 0 }, new float[] { 0, height }, new float[] { width, 0 },
				0, 100,
				new float[] { 0, 25, 26, 100 },
				new Color[] { Color.WHITE, Color.RED, Color.GREEN, Color.BLUE });

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		g2.setPaint(paint);
		g2.fillRect(0, 0, width, height);

		ImagePlotPanel imagePanel = new ImagePlotPanel();
		imagePanel.setImage(image);
		imagePanel.getPlot().setPreferredSize(new Dimension(width, height));

		QuickTestFrame.frame(imagePanel).setVisible(true);
	}
}
