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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingConstants;

public enum Axis {
	X {
		@Override
		public int get(Point p) {
			return p.x;
		}

		@Override
		public double get(Point2D p) {
			return p.getX();
		}

		@Override
		public int lower(Insets insets) {
			return insets.left;
		}

		@Override
		public int lower(Rectangle bounds) {
			return bounds.x;
		}

		@Override
		public Side lowerSide() {
			return Side.LEFT;
		}

		@Override
		public Axis opposite() {
			return Y;
		}

		@Override
		public void set(Point p, int value) {
			p.x = value;
		}

		@Override
		public void set(Point2D p, double value) {
			p.setLocation(value, p.getY());
		}

		@Override
		public void setLower(Insets insets, int lower) {
			insets.left = lower;
		}

		@Override
		public void setLower(Rectangle bounds, int lower) {
			bounds.x = lower;
		}

		@Override
		public void setSize(Dimension dim, int size) {
			dim.width = size;
		}

		@Override
		public void setSize(Rectangle bounds, int size) {
			bounds.width = size;
		}

		@Override
		public void setUpper(Insets insets, int upper) {
			insets.right = upper;
		}

		@Override
		public int size(Dimension dim) {
			return dim.width;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.HORIZONTAL;
		}

		@Override
		public int upper(Insets insets) {
			return insets.right;
		}

		@Override
		public Side upperSide() {
			return Side.RIGHT;
		}
	},
	Y {
		@Override
		public int get(Point p) {
			return p.y;
		}

		@Override
		public double get(Point2D p) {
			return p.getY();
		}

		@Override
		public int lower(Insets insets) {
			return insets.top;
		}

		@Override
		public int lower(Rectangle bounds) {
			return bounds.y;
		}

		@Override
		public Side lowerSide() {
			return Side.TOP;
		}

		@Override
		public Axis opposite() {
			return X;
		}

		@Override
		public void set(Point p, int value) {
			p.y = value;
		}

		@Override
		public void set(Point2D p, double value) {
			p.setLocation(p.getX(), value);
		}

		@Override
		public void setLower(Insets insets, int lower) {
			insets.top = lower;
		}

		@Override
		public void setLower(Rectangle bounds, int lower) {
			bounds.y = lower;
		}

		@Override
		public void setSize(Dimension dim, int size) {
			dim.height = size;
		}

		@Override
		public void setSize(Rectangle bounds, int size) {
			bounds.height = size;
		}

		@Override
		public void setUpper(Insets insets, int upper) {
			insets.bottom = upper;
		}

		@Override
		public int size(Dimension dim) {
			return dim.height;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.VERTICAL;
		}

		@Override
		public int upper(Insets insets) {
			return insets.bottom;
		}

		@Override
		public Side upperSide() {
			return Side.BOTTOM;
		}
	};

	public static Axis fromSwingConstant(int swingConstant) {
		switch (swingConstant) {
		case SwingConstants.HORIZONTAL:
			return X;
		case SwingConstants.VERTICAL:
			return Y;
		default:
			throw new IllegalArgumentException(
					"swingConstant must be SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
		}
	}

	public final List<Side> sides = Collections.unmodifiableList(Arrays.asList(lowerSide(), upperSide()));

	public int center(Component comp) {
		return center(comp.getBounds());
	}

	public int center(Rectangle bounds) {
		return lower(bounds) + size(bounds) / 2;
	}

	public boolean contains(Component comp, int value) {
		return contains(comp.getBounds(), value);
	}

	public boolean contains(Rectangle bounds, int value) {
		int size = size(bounds);
		int lower = lower(bounds);

		return size > 0 && (lower + size < size || lower + size > value);
	}

	public abstract int get(Point p);

	public abstract double get(Point2D p);

	public void grow(Rectangle bounds, int amount) {
		setSize(bounds, size(bounds) + amount);
	}

	public int insetLocalCenter(Container parent) {
		return (lowerSide().insetLocalLocation(parent) + upperSide().insetLocalLocation(parent)) / 2;
	}

	public int insetSize(Container parent) {
		return size(parent) - sizeReduction(parent);
	}

	public int lower(Component comp) {
		return lower(comp.getBounds());
	}

	public abstract int lower(Insets insets);

	public abstract int lower(Rectangle bounds);

	public int lowerInset(Container parent) {
		return lower(parent.getInsets());
	}

	public abstract Side lowerSide();

	public abstract Axis opposite();

	public abstract void set(Point p, int value);

	public abstract void set(Point2D p, double value);

	public void setBounds(Rectangle bounds, int lower, int size) {
		setLower(bounds, lower);
		setSize(bounds, size);
	}

	public abstract void setLower(Insets insets, int lower);

	public abstract void setLower(Rectangle bounds, int lower);

	public abstract void setSize(Dimension dim, int size);

	public abstract void setSize(Rectangle bounds, int size);

	public abstract void setUpper(Insets insets, int upper);

	public void setUpper(Rectangle bounds, int upper) {
		setSize(bounds, upper - lower(bounds));
	}

	public int size(Component comp) {
		return size(comp.getSize());
	}

	public abstract int size(Dimension dim);

	public int size(Rectangle bounds) {
		return size(bounds.getSize());
	}

	public int sizeReduction(Container parent) {
		return sizeReduction(parent.getInsets());
	}

	public int sizeReduction(Insets insets) {
		return lower(insets) + upper(insets);
	}

	public abstract int swingConstant();

	public int upper(Component comp) {
		return upper(comp.getBounds());
	}

	public abstract int upper(Insets insets);

	public int upper(Rectangle bounds) {
		return lower(bounds) + size(bounds);
	}

	public int upperInset(Container parent) {
		return upper(parent.getInsets());
	}

	public abstract Side upperSide();
}
