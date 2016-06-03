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
package org.andork.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

public class ModernStyleClearButton {
	public static void createClearButton(AbstractButton clearButton) {
		clearButton.setMargin(new Insets(0, 0, 0, 0));
		clearButton.setIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-normal.png")));
		clearButton.setRolloverIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-rollover.png")));
		clearButton.setPressedIcon(new ImageIcon(ModernStyleClearButton.class.getResource("xicon-pressed.png")));
		clearButton.setPreferredSize(
				new Dimension(clearButton.getIcon().getIconWidth(), clearButton.getIcon().getIconHeight()));
		clearButton.setFocusPainted(false);
		clearButton.setBorderPainted(false);
		clearButton.setContentAreaFilled(false);
		clearButton.setOpaque(false);
	}
}
