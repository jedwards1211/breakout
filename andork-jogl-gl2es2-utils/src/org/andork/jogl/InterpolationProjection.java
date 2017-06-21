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
package org.andork.jogl;

import org.andork.math3d.Vecmath;

public class InterpolationProjection implements Projection {
	Projection a;
	Projection b;

	public float f;

	float[] aOut = Vecmath.newMat4f();
	float[] bOut = Vecmath.newMat4f();

	public final float[] center = new float[3];
	public float radius = 1;

	public InterpolationProjection(Projection a, Projection b, float f) {
		super();
		this.a = a;
		this.b = b;
		this.f = f;
	}

	@Override
	public void calculate(float[] pOut, JoglDrawContext dc, int width, int height) {
		a.calculate(aOut, dc, width, height);
		b.calculate(bOut, dc, width, height);
		Vecmath.interp(aOut, bOut, f, pOut);
	}
}
