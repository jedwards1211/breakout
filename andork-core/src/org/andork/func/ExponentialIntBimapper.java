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
package org.andork.func;

public class ExponentialIntBimapper implements Bimapper<Integer, Integer> {
	public double a;
	public double b;
	public double c;

	/**
	 * Creates a bimapper where out = a * e ^ (b * x) + c.
	 */
	public ExponentialIntBimapper(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public ExponentialIntBimapper(double[] abc) {
		this(abc[0], abc[1], abc[2]);
	}

	@Override
	public Integer map(Integer in) {
		return in == null ? null : (int) Math.round(a * Math.exp(b * in) + c);
	}

	@Override
	public Integer unmap(Integer out) {
		return out == null ? null : (int) Math.round(Math.log((out - c) / a) / b);
	}
}
