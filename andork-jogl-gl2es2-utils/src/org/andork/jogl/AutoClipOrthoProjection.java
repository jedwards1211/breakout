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

import static org.andork.math3d.Vecmath.ortho;
import static org.andork.math3d.Vecmath.subDot3;

public class AutoClipOrthoProjection implements Projection {
	public final float[] center = new float[3];
	public float radius = 1;

	public float hSpan = 1;
	public float vSpan = 1;

	public boolean useNearClipPoint;
	public final float[] nearClipPoint = { Float.NaN, Float.NaN, Float.NaN };

	public boolean useFarClipPoint;
	public final float[] farClipPoint = { Float.NaN, Float.NaN, Float.NaN };

	@Override
	public void calculate(JoglDrawContext dc, float[] pOut) {
		float width = dc.width();
		float height = dc.height();

		float[] vi = dc.inverseViewMatrix();

		float left, right, bottom, top;
		if (vSpan / hSpan > height / width) {
			top = vSpan / 2;
			bottom = -top;
			right = top * width / height;
			left = -right;
		} else {
			right = hSpan / 2;
			left = -right;
			top = right * height / width;
			bottom = -top;
		}

		float dist = subDot3(vi, 12, center, 0, vi, 8);

		float zNear = dist - radius;
		float zFar = dist + radius;

		if (useNearClipPoint) {
			zNear = subDot3(vi, 12, nearClipPoint, 0, vi, 8);
		}
		if (useFarClipPoint) {
			zFar = subDot3(vi, 12, farClipPoint, 0, vi, 8);
		}

		ortho(pOut, left, right, bottom, top, zNear, zFar);
	}

}
