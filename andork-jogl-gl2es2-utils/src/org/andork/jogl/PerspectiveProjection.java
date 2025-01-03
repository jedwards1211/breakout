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

import static org.andork.math3d.Vecmath.perspective;

public class PerspectiveProjection implements Projection {
	public float fovAngle;
	public float zNear;
	public float zFar;

	public PerspectiveProjection(float fov, float zNear, float zFar) {
		super();
		fovAngle = fov;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	@Override
	public void calculate(float[] pOut, JoglDrawContext dc, int width, int height) {
		perspective(pOut, fovAngle, width / (float) height, zNear, zFar);
	}

	@Override
	public Projection resize(float hSpan, float vSpan, float zNear, float zFar) {
		return new PerspectiveProjection(fovAngle, zNear, zFar);
	}

	@Override
	public boolean isOrtho() {
		return false;
	}
}
