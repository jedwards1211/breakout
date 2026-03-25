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

public class ExponentialFloatBimapper implements Bimapper<Float, Float> {
	public double a;
	public double b;
	public double c;

	/**
	 * Creates a bimapper where out = a * e ^ (b * x) + c.
	 */
	public ExponentialFloatBimapper(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public ExponentialFloatBimapper(double[] abc) {
		this(abc[0], abc[1], abc[2]);
	}

	@Override
	public Float map(Float in) {
		return in == null ? null : (float) (a * Math.exp(b * in) + c);
	}

	@Override
	public Float unmap(Float out) {
		return out == null ? null : (float) (Math.log((out - c) / a) / b);
	}
}
