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
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingConstants;

public enum Corner {
	TOP_LEFT {

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.NORTHWEST;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.NORTH_WEST;
		}

		@Override
		public Side xSide() {
			return Side.LEFT;
		}

		@Override
		public Side ySide() {
			return Side.TOP;
		}

	},
	TOP_RIGHT {

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.NORTHEAST;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.NORTH_EAST;
		}

		@Override
		public Side xSide() {
			return Side.RIGHT;
		}

		@Override
		public Side ySide() {
			return Side.TOP;
		}

	},
	BOTTOM_LEFT {

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.SOUTHWEST;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.SOUTH_WEST;
		}

		@Override
		public Side xSide() {
			return Side.LEFT;
		}

		@Override
		public Side ySide() {
			return Side.BOTTOM;
		}

	},
	BOTTOM_RIGHT {

		@Override
		public int gbcAnchor() {
			return GridBagConstraints.SOUTHEAST;
		}

		@Override
		public int swingConstant() {
			return SwingConstants.SOUTH_EAST;
		}

		@Override
		public Side xSide() {
			return Side.RIGHT;
		}

		@Override
		public Side ySide() {
			return Side.BOTTOM;
		}

	};

	public static Corner fromDirections(int xDir, int yDir) {
		if (xDir == 0) {
			throw new IllegalArgumentException("xDir must be non-null");
		}
		if (yDir == 0) {
			throw new IllegalArgumentException("yDir must be non-null");
		}

		if (xDir > 0) {
			return yDir > 0 ? BOTTOM_RIGHT : TOP_RIGHT;
		} else {
			return yDir > 0 ? BOTTOM_LEFT : TOP_LEFT;
		}
	}

	public static Corner fromGbcAnchor(int gbcAnchor) {
		switch (gbcAnchor) {
		case GridBagConstraints.NORTHWEST:
			return TOP_LEFT;
		case GridBagConstraints.NORTHEAST:
			return TOP_RIGHT;
		case GridBagConstraints.SOUTHWEST:
			return BOTTOM_LEFT;
		case GridBagConstraints.SOUTHEAST:
			return BOTTOM_RIGHT;
		default:
			throw new IllegalArgumentException("gbcAnchor must be NORTHWEST, NORTHEAST, SOUTHWEST, or SOUTHEAST");
		}
	}

	public static Corner fromSides(Side side1, Side side2) {
		if (side1.axis() == side2.axis()) {
			throw new IllegalArgumentException("sides must have different axes");
		}
		if (side1.axis() == Axis.Y) {
			Side temp = side1;
			side1 = side2;
			side2 = temp;
		}

		if (side1.isLower()) {
			return side2.isLower() ? TOP_LEFT : BOTTOM_LEFT;
		} else {
			return side2.isLower() ? TOP_RIGHT : BOTTOM_RIGHT;
		}
	}

	public static Corner fromSwingConstant(int swingConstant) {
		switch (swingConstant) {
		case SwingConstants.NORTH_WEST:
			return TOP_LEFT;
		case SwingConstants.NORTH_EAST:
			return TOP_RIGHT;
		case SwingConstants.SOUTH_WEST:
			return BOTTOM_LEFT;
		case SwingConstants.SOUTH_EAST:
			return BOTTOM_RIGHT;
		default:
			throw new IllegalArgumentException(
					"swingConstant must be NORTH_WEST, NORTH_EAST, SOUTH_WEST, or SOUTH_EAST");
		}
	}

	public Corner adjacent(Side otherSide) {
		if (otherSide.axis() == xSide().axis()) {
			return fromSides(otherSide, ySide());
		} else {
			return fromSides(xSide(), otherSide);
		}
	}

	public abstract int gbcAnchor();

	public void grow(Rectangle bounds, int xAmount, int yAmount) {
		xSide().grow(bounds, xAmount);
		ySide().grow(bounds, yAmount);
	}

	public Corner inverse() {
		return fromSides(xSide().inverse(), ySide().inverse());
	}

	public Point location(Component comp) {
		return location(comp.getBounds());
	}

	public Point location(Rectangle bounds) {
		return new Point(xSide().location(bounds), ySide().location(bounds));
	}

	public Corner nextClockwise() {
		return fromSides(xSide().nextClockwise(), ySide().nextClockwise());
	}

	public Corner nextCounterClockwise() {
		return fromSides(xSide().nextCounterClockwise(), ySide().nextCounterClockwise());
	}

	public Corner opposite() {
		return fromSides(xSide().opposite(), ySide().opposite());
	}

	public void setLocation(Rectangle bounds, Point location) {
		xSide().setLocation(bounds, location.x);
		ySide().setLocation(bounds, location.y);
	}

	public Side side(Axis axis) {
		switch (axis) {
		case X:
			return xSide();
		case Y:
			return ySide();
		default:
			throw new IllegalArgumentException("axis must be non-null");
		}
	}

	public void stretch(Rectangle bounds, Point location) {
		xSide().stretch(bounds, location.x);
		ySide().stretch(bounds, location.y);
	}

	public abstract int swingConstant();

	public Corner xOpposite() {
		return fromSides(xSide().opposite(), ySide());
	}

	/**
	 * @return the side coincident with this Corner's position on the x axis.
	 */
	public abstract Side xSide();

	public Corner yOpposite() {
		return fromSides(xSide(), ySide().opposite());
	}

	/**
	 * @return the side coincident with this Corner's position on the y axis.
	 */
	public abstract Side ySide();
}
