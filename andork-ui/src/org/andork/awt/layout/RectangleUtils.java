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

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import org.andork.util.AnimationUtils;

public class RectangleUtils {
	public static Rectangle animate(Rectangle current, Rectangle target, long time,
			float factor, int extra, int delay) {
		Rectangle out = new Rectangle();
		animate(current, target, time, factor, extra, delay, out);
		return out;
	}

	public static void animate(Rectangle current, Rectangle target, long time,
			float factor, int extra, int delay, Rectangle out) {

		Side maxSide = null;
		int maxOffset = 0;
		float maxInterpFactor = 0f;

		for (Side side : Side.values()) {
			int curLoc = side.location(current);
			int targetLoc = side.location(target);
			int newPos = AnimationUtils.animate(curLoc, targetLoc,
					time, factor, extra, delay);
			int offset = Math.abs(newPos - curLoc);

			if (maxSide == null && offset > maxOffset) {
				maxSide = side;
				maxOffset = offset;
				maxInterpFactor = AnimationUtils.getInterpFactor(curLoc, targetLoc, newPos);
			}
		}
		maxInterpFactor = Math.min(1, Math.max(0, maxInterpFactor));

		interpolate(current, target, maxInterpFactor, out);
	}

	public static void inset(Rectangle r, Insets insets, Rectangle out) {
		out.x = r.x + insets.left;
		out.y = r.y + insets.top;
		out.width = r.width - insets.left - insets.right;
		out.height = r.height - insets.top - insets.bottom;
	}

	public static Rectangle insetCopy(Rectangle r, Insets insets) {
		Rectangle out = new Rectangle();
		inset(r, insets, out);
		return out;
	}

	public static Rectangle interpolate(Rectangle r1, Rectangle r2, float f) {
		Rectangle out = new Rectangle();
		interpolate(r1, r2, f, out);
		return out;
	}

	public static void interpolate(Rectangle r1, Rectangle r2, float f, Rectangle out) {
		if (r1.x + r1.width == r2.x + r2.width) {
			int right = r1.x + r1.width;
			out.x = Math.round(r1.x * (1f - f) + r2.x * f);
			out.width = right - out.x;
		} else {
			out.x = Math.round(r1.x * (1f - f) + r2.x * f);
			out.width = Math.round(r1.width * (1f - f) + r2.width * f);
		}
		if (r1.y + r1.height == r2.y + r2.height) {
			int bottom = r1.y + r1.height;
			out.y = Math.round(r1.y * (1f - f) + r2.y * f);
			out.height = bottom - out.y;
		} else {
			out.y = Math.round(r1.y * (1f - f) + r2.y * f);
			out.height = Math.round(r1.height * (1f - f) + r2.height * f);
		}
	}

	public static void outset(Rectangle r, Insets outsets, Rectangle out) {
		out.x = r.x - outsets.left;
		out.y = r.y - outsets.top;
		out.width = r.width + outsets.left + outsets.right;
		out.height = r.height + outsets.top + outsets.bottom;
	}

	public static Rectangle outsetCopy(Rectangle r, Insets outsets) {
		Rectangle out = new Rectangle();
		outset(r, outsets, out);
		return out;
	}

	public static int rectilinearDistance(Rectangle r, Point p) {
		int distance = 0;

		if (p.x < r.x) {
			distance = r.x - p.x;
		} else if (p.x > r.x + r.width) {
			distance = p.x - r.x - r.width;
		}

		if (p.y < r.y) {
			distance = Math.max(distance, r.y - p.y);
		} else if (p.y > r.y + r.height) {
			distance = Math.max(distance, p.y - r.y - r.height);
		}
		return distance;
	}
}
