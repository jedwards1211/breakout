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
package org.andork.util;

public class Reparam {
	/**
	 * Performs linear reparameterization.
	 *
	 * @param x
	 *            the value to reparameterize
	 * @param a1
	 *            the start of the first range
	 * @param a2
	 *            the end of the first range
	 * @param b1
	 *            the start of the second range
	 * @param b2
	 *            the end of the second range
	 */
	public static float linear(float x, float a1, float a2, float b1, float b2) {
		return b1 + (x - a1) * (b2 - b1) / (a2 - a1);
	}

	/**
	 * Performs linear reparameterization.
	 *
	 * @param x
	 *            the values to reparameterize
	 * @param a1
	 *            the start of the first range
	 * @param a2
	 *            the end of the first range
	 * @param b1
	 *            the start of the second range
	 * @param b2
	 *            the end of the second range
	 * @param out
	 *            the array to place the result in
	 */
	public static void linear(float[] x, float a1, float a2, float b1, float b2, float[] out) {
		for (int i = 0; i < x.length; i++) {
			out[i] = linear(x[i], a1, a2, b1, b2);
		}
	}
}
