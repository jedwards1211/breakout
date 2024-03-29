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
package org.andork.math3d;

import java.util.stream.Stream;

public class Fitting3d {
	/**
	 * Finds a least-squares normal for a plane as close to parallel to the
	 * given vectors as possible. This method assumes the normal will have a
	 * nonzero y component.
	 *
	 * @param vectors
	 *            a stream of 3-dimensional vectors
	 * @return the 3-dimensional plane normal
	 */
	public static float[] planeNormalLeastSquares2f(Stream<float[]> vectors) {
		float[] m = new float[9];

		vectors.forEach(v -> {
			m[0] += 2 * v[0] * v[0];
			m[1] += 2 * v[0] * v[1];
			m[2] += 2 * v[0] * v[2];

			m[3] += 2 * v[1] * v[0];
			m[4] += 2 * v[1] * v[1];
			m[5] += 2 * v[1] * v[2];

			m[6] += 2 * v[2] * v[0];
			m[7] += 2 * v[2] * v[1];
			m[8] += 2 * v[2] * v[2];
		});

		Vecmath.invertGeneral3x3(m);

		return new float[] { m[3], m[4], m[5] };
	}
}
