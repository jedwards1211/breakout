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

import java.awt.Dimension;

import javax.swing.JFrame;

import org.andork.awt.event.MouseAdapterChain;

import com.andork.plot.PlotAxis.LabelPosition;
import com.andork.plot.PlotAxis.Orientation;

public class PlotTest {
	public static void main(String[] args) {
		Plot plot = new Plot();

		PlotAxis haxis = new PlotAxis(Orientation.HORIZONTAL, LabelPosition.TOP);
		PlotAxis vaxis = new PlotAxis(Orientation.VERTICAL, LabelPosition.LEFT);

		haxis.addPlot(plot);
		vaxis.addPlot(plot);

		plot.addLayer(new HorizontalGridLineLayer(vaxis));
		plot.addLayer(new VerticalGridLineLayer(haxis));

		PlotAxisController haxisController = new PlotAxisController(haxis);
		PlotAxisController vaxisController = new PlotAxisController(vaxis);

		PlotController plotController = new PlotController(plot, haxisController, vaxisController);
		MouseAdapterChain chain = new MouseAdapterChain();
		chain.addMouseAdapter(plotController);
		chain.install(plot);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new PlotPanelLayout());
		frame.getContentPane().add(plot);
		frame.getContentPane().add(haxis);
		frame.getContentPane().add(vaxis);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(640, 480));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
