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
package org.andork.awt.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFrame;

import org.andork.swing.PaintablePanel;
import org.andork.swing.border.GradientFillBorder;

public class DrawerLayoutTest2 {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		// Container content = frame.getContentPane( );
		// LayeredBorder.addOnTop( GradientFillBorder.from( Corner.TOP_LEFT
		// ).to( Side.BOTTOM ).colors( content.getBackground( ) ,
		// Color.LIGHT_GRAY ) , content
		// );
		// content.setPreferredSize( new Dimension( 640 , 480 ) );
		frame.getContentPane().setPreferredSize(new Dimension(640, 480));
		// content.setLayout( new DelegatingLayoutManager( ) );

		PaintablePanel panel = new PaintablePanel();
		panel.setLayout(new DelegatingLayoutManager());
		panel.setPreferredSize(new Dimension(200, 100));

		panel.setUnderpaintBorder(
				GradientFillBorder.from(Side.TOP).to(Side.BOTTOM).colors(Color.LIGHT_GRAY, Color.GRAY));

		Drawer drawer = new Drawer(panel);
		drawer.delegate().dockingSide(Side.BOTTOM);
		drawer.mainResizeHandle();
		drawer.pinButtonDelegate().insets(new Insets(10, 10, -10, -10));

		drawer.addTo(frame.getLayeredPane(), 2);

		DrawerAutoshowController autoshowController = new DrawerAutoshowController();
		frame.getContentPane().addMouseMotionListener(autoshowController);

		panel.add(drawer.maxButton(), new DrawerLayoutDelegate(drawer.maxButton(), Corner.TOP_RIGHT, Side.RIGHT));

		PaintablePanel panel2 = new PaintablePanel();
		panel2.setPreferredSize(new Dimension(100, 200));
		panel2.setUnderpaintBorder(
				GradientFillBorder.from(Side.TOP).to(Side.BOTTOM).colors(Color.LIGHT_GRAY, Color.GRAY));

		Drawer drawer2 = new Drawer(panel2);
		drawer2.delegate().dockingSide(Side.LEFT);
		drawer2.mainResizeHandle();
		drawer2.pinButtonDelegate().insets(new Insets(10, -10, -10, 10));

		drawer2.addTo(frame.getLayeredPane(), 0);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
