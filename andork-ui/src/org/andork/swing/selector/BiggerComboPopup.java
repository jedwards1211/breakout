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
package org.andork.swing.selector;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

public class BiggerComboPopup extends BasicComboPopup {

	/**
	 *
	 */
	private static final long serialVersionUID = 500768327739230318L;

	public BiggerComboPopup(JComboBox combo) {
		super(combo);
	}

	@Override
	public void show(Component invoker, int x, int y) {
		Dimension size = list.getPreferredSize();
		size.width = Math.max(size.width, comboBox.getWidth());
		scroller.setPreferredSize(size);
		scroller.setMaximumSize(size);
		pack();
		super.show(invoker, x, y);
	}
}
