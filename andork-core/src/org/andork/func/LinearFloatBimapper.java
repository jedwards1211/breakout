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

public class LinearFloatBimapper implements Bimapper<Float, Float> {
	// out = m * in + b;
	public float m;
	public float b;

	public LinearFloatBimapper() {
		m = 1f;
		b = 0f;
	}

	public LinearFloatBimapper(float m, float b) {
		this.m = m;
		this.b = b;
	}

	public LinearFloatBimapper(float in0, float out0, float in1, float out1) {
		set(in0, out0, in1, out1);
	}

	@Override
	public Float map(Float in) {
		return in == null ? null : m * in + b;
	}

	public LinearFloatBimapper set(float in0, float out0, float in1, float out1) {
		m = (out1 - out0) / (in1 - in0);
		b = in0 - out0 / m;
		return this;
	}

	@Override
	public Float unmap(Float out) {
		return out == null ? null : (out - b) / m;
	}
}
