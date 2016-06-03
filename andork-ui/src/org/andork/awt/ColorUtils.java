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
package org.andork.awt;

import java.awt.Color;

public class ColorUtils {
	public static Color alphaColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	public static Color darkerColor(Color c, double amount) {
		float[] hsbvals = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
		hsbvals[2] = Math.min(1f, Math.max(0f, (float) (hsbvals[2] - amount)));
		int rgb = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
		rgb |= c.getAlpha() << 24;
		return new Color(rgb, true);
	}

	public static Color lighterColor(Color c, double amount) {
		return darkerColor(c, -amount);
	}
}
