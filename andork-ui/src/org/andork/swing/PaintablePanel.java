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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class PaintablePanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 4933574132483476864L;

	public static PaintablePanel wrap(Component c) {
		if (c instanceof JComponent) {
			((JComponent) c).setOpaque(false);
		}
		PaintablePanel panel = new PaintablePanel();
		panel.setLayout(new BorderLayout());
		panel.add(c, BorderLayout.CENTER);
		return panel;
	}

	private Border underpaintBorder = null;

	public PaintablePanel() {
		setOpaque(false);
	}

	public Border getUnderpaintBorder() {
		return underpaintBorder;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (underpaintBorder != null) {
			underpaintBorder.paintBorder(this, g, 0, 0, getWidth(), getHeight());
		}
	}

	public void setUnderpaintBorder(Border b) {
		if (underpaintBorder != b) {
			underpaintBorder = b;
			repaint();
		}
	}
}
