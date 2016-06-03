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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public enum Side {
	TOP {

		@Override
		public Axis axis() {
			return Axis.Y;
		}

		@Override
		public String borderLayoutAnchor() {
			return BorderLayout.NORTH;
		}

		@Override
		public int direction() {
			return -1;
		}

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.NORTH;
		}

		@Override
		public int get(Insets insets) {
			return insets.top;
		}

		@Override
		public Side inverse() {
			return LEFT;
		}

		@Override
		public int location(Rectangle bounds) {
			return bounds.y;
		}

		@Override
		public Side nextClockwise() {
			return RIGHT;
		}

		@Override
		public Side nextCounterClockwise() {
			return LEFT;
		}

		@Override
		public Side opposite() {
			return BOTTOM;
		}

		@Override
		public Cursor resizeCursor() {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		}

		@Override
		public void set(Insets insets, int value) {
			insets.top = value;
		}

		@Override
		public void setLocation(Rectangle bounds, int location) {
			bounds.y = location;
		}

		@Override
		public void stretch(Rectangle bounds, int location) {
			bounds.height += bounds.y - location;
			bounds.y = location;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.TOP;
		}
	},
	BOTTOM {

		@Override
		public Axis axis() {
			return Axis.Y;
		}

		@Override
		public String borderLayoutAnchor() {
			return BorderLayout.SOUTH;
		}

		@Override
		public int direction() {
			return 1;
		}

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.SOUTH;
		}

		@Override
		public int get(Insets insets) {
			return insets.bottom;
		}

		@Override
		public Side inverse() {
			return RIGHT;
		}

		@Override
		public int location(Rectangle bounds) {
			return bounds.y + bounds.height;
		}

		@Override
		public Side nextClockwise() {
			return LEFT;
		}

		@Override
		public Side nextCounterClockwise() {
			return RIGHT;
		}

		@Override
		public Side opposite() {
			return TOP;
		}

		@Override
		public Cursor resizeCursor() {
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		}

		@Override
		public void set(Insets insets, int value) {
			insets.bottom = value;
		}

		@Override
		public void setLocation(Rectangle bounds, int location) {
			bounds.y = location - bounds.height;
		}

		@Override
		public void stretch(Rectangle bounds, int location) {
			bounds.height = location - bounds.y;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.BOTTOM;
		}
	},
	LEFT {

		@Override
		public Axis axis() {
			return Axis.X;
		}

		@Override
		public String borderLayoutAnchor() {
			return BorderLayout.WEST;
		}

		@Override
		public int direction() {
			return -1;
		}

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.WEST;
		}

		@Override
		public int get(Insets insets) {
			return insets.left;
		}

		@Override
		public Side inverse() {
			return TOP;
		}

		@Override
		public int location(Rectangle bounds) {
			return bounds.x;
		}

		@Override
		public Side nextClockwise() {
			return TOP;
		}

		@Override
		public Side nextCounterClockwise() {
			return BOTTOM;
		}

		@Override
		public Side opposite() {
			return RIGHT;
		}

		@Override
		public Cursor resizeCursor() {
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		}

		@Override
		public void set(Insets insets, int value) {
			insets.left = value;
		}

		@Override
		public void setLocation(Rectangle bounds, int location) {
			bounds.x = location;
		}

		@Override
		public void stretch(Rectangle bounds, int location) {
			bounds.width += bounds.x - location;
			bounds.x = location;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.LEFT;
		}
	},
	RIGHT {

		@Override
		public Axis axis() {
			return Axis.X;
		}

		@Override
		public String borderLayoutAnchor() {
			return BorderLayout.EAST;
		}

		@Override
		public int direction() {
			return 1;
		}

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.EAST;
		}

		@Override
		public int get(Insets insets) {
			return insets.right;
		}

		@Override
		public Side inverse() {
			return BOTTOM;
		}

		@Override
		public int location(Rectangle bounds) {
			return bounds.x + bounds.width;
		}

		@Override
		public Side nextClockwise() {
			return BOTTOM;
		}

		@Override
		public Side nextCounterClockwise() {
			return TOP;
		}

		@Override
		public Side opposite() {
			return LEFT;
		}

		@Override
		public Cursor resizeCursor() {
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		}

		@Override
		public void set(Insets insets, int value) {
			insets.right = value;
		}

		@Override
		public void setLocation(Rectangle bounds, int location) {
			bounds.x = location - bounds.width;
		}

		@Override
		public void stretch(Rectangle bounds, int location) {
			bounds.width = location - bounds.x;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.RIGHT;
		}
	};

	public static Side fromAxis(Axis axis, int direction) {
		if (direction == 0) {
			throw new IllegalArgumentException("direction must be nonzero");
		}
		switch (axis) {
		case X:
			return direction > 0 ? RIGHT : LEFT;
		case Y:
			return direction < 0 ? BOTTOM : TOP;
		default:
			throw new IllegalArgumentException("axis must be non-null");
		}
	}

	public static Side fromGbcAnchor(int gbcAnchor) {
		switch (gbcAnchor) {
		case GridBagConstraints.NORTH:
			return TOP;
		case GridBagConstraints.EAST:
			return RIGHT;
		case GridBagConstraints.SOUTH:
			return BOTTOM;
		case GridBagConstraints.WEST:
			return LEFT;
		default:
			throw new IllegalArgumentException("gbcAnchor must be NORTH, EAST, SOUTH, or WEST");
		}
	}

	public static Side fromSwingConstant(int swingConstant) {
		switch (swingConstant) {
		case SwingConstants.TOP:
			return TOP;
		case SwingConstants.BOTTOM:
			return BOTTOM;
		case SwingConstants.LEFT:
			return LEFT;
		case SwingConstants.RIGHT:
			return RIGHT;
		default:
			throw new IllegalArgumentException("swingConstant must be TOP, BOTTOM, LEFT, or RIGHT");
		}
	}

	/**
	 * @return the axis this side is positioned along (for example, even though
	 *         the top side is a horizontal line, its position is on the y axis,
	 *         so its axis is {@link Axis#Y}).
	 */
	public abstract Axis axis();

	public abstract String borderLayoutAnchor();

	public Point center(Component comp) {
		return center(comp.getBounds());
	}

	public Point center(Rectangle bounds) {
		Point p = new Point();
		axis().set(p, location(bounds));
		Axis opposite = axis().opposite();
		opposite.set(p, opposite.center(bounds));
		return p;
	}

	/**
	 * @return a unit increment along this side's axis from the center of the
	 *         bounds toward the side (for example, the direction for
	 *         {@link #TOP} is -1, for {@link #RIGHT} is 1).
	 */
	public abstract int direction();

	public abstract int gbcAnchor();

	public abstract int get(Insets insets);

	public void grow(Rectangle bounds, int amount) {
		setLocation(bounds, location(bounds) + amount * direction());
	}

	public int inset(Container parent) {
		return get(parent.getInsets());
	}

	public int insetLocalLocation(Container parent) {
		return localLocation(parent) - direction() * inset(parent);
	}

	/**
	 * @return the side this becomes if the x and y axes are inverted.
	 */
	public abstract Side inverse();

	public boolean isLower() {
		return direction() < 0;
	}

	public boolean isUpper() {
		return direction() > 0;
	}

	public int localLocation(Component comp) {
		return location(SwingUtilities.getLocalBounds(comp));
	}

	public int location(Component comp) {
		return location(comp.getBounds());
	}

	public abstract int location(Rectangle bounds);

	public abstract Side nextClockwise();

	public abstract Side nextCounterClockwise();

	public abstract Side opposite();

	public abstract Cursor resizeCursor();

	public abstract void set(Insets insets, int value);

	public abstract void setLocation(Rectangle bounds, int location);

	public abstract void stretch(Rectangle bounds, int location);

	public abstract int swingConstant();
}
