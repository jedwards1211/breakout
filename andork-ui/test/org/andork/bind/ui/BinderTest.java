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
package org.andork.bind.ui;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.andork.bind.BimapperBinder;
import org.andork.bind.DefaultBinder;
import org.andork.func.IntegerStringBimapper;

public class BinderTest {
	public static void main(String[] args) {
		JTextField textField = new JTextField();
		JSlider slider = new JSlider(0, 100, 50);

		DefaultBinder<Integer> rootBinder = new DefaultBinder<Integer>();
		rootBinder.set(50);

		ComponentTextBinder.bind(textField,
				BimapperBinder.bind(IntegerStringBimapper.instance, rootBinder));

		JSliderValueBinder.bind(slider, rootBinder);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(textField);
		frame.getContentPane().add(slider);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
