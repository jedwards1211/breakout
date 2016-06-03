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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class LayeredBorder implements Border {
	public static Border add(Border top, Border bottom) {
		if (top == null) {
			return bottom;
		}
		if (bottom == null) {
			return top;
		}
		return new LayeredBorder(top, bottom);
	}

	public static void addOnBottom(Border b, JComponent c) {
		c.setBorder(add(c.getBorder(), b));
	}

	public static void addOnTop(Border b, JComponent c) {
		c.setBorder(add(b, c.getBorder()));
	}

	public static Border remove(Border b, Border oldb) {
		if (b == oldb || b == null) {
			return null;
		} else if (b instanceof LayeredBorder) {
			return ((LayeredBorder) b).remove(oldb);
		} else {
			return b; // it's not here
		}
	}

	public static void remove(Border b, JComponent c) {
		c.setBorder(remove(c.getBorder(), b));
	}

	protected Border top, bottom;

	protected LayeredBorder(Border top, Border bottom) {
		super();
		this.top = top;
		this.bottom = bottom;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		Insets ti = top.getBorderInsets(c);
		Insets bi = bottom.getBorderInsets(c);

		return new Insets(
				Math.max(ti.top, bi.top),
				Math.max(ti.left, bi.left),
				Math.max(ti.bottom, bi.bottom),
				Math.max(ti.right, bi.right));
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		bottom.paintBorder(c, g, x, y, width, height);
		top.paintBorder(c, g, x, y, width, height);
	}

	protected Border remove(Border oldb) {
		if (oldb == top) {
			return bottom;
		}
		if (oldb == bottom) {
			return top;
		}
		Border top2 = remove(top, oldb);
		Border bottom2 = remove(bottom, oldb);
		if (top2 == top && bottom2 == bottom) {
			return this; // it's not here
		}
		return add(top2, bottom2);
	}
}
