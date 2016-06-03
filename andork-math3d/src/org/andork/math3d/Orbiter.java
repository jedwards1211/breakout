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

import static java.lang.Math.atan2;
import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.rotY;
import static org.andork.math3d.Vecmath.setColumn3;
import static org.andork.math3d.Vecmath.setIdentity;
import static org.andork.math3d.Vecmath.setRotation;

/**
 * Applies orbit transforms to view transforms.
 */
public class Orbiter {
	final float[] center = { 0, 0, 0 };
	final float[] right = new float[3];
	final float[] forward = new float[3];
	final float[] levelForward = new float[3];
	final float[] up = new float[3];
	final float[] m1 = Vecmath.newMat4f();
	final float[] m2 = Vecmath.newMat4f();

	public void getCenter(float[] out) {
		Vecmath.setf(out, center);
	}

	public float getPan(float[] v) {
		invAffine(v, m1);
		mvmulAffine(m1, 1, 0, 0, right);
		return (float) atan2(-right[2], right[0]);
	}

	public float getTilt(float[] v) {
		invAffine(v, m1);
		mvmulAffine(m1, 1, 0, 0, right);
		levelForward[0] = right[2];
		levelForward[1] = 0;
		levelForward[2] = -right[0];
		cross(right, levelForward, up);
		mvmulAffine(m1, 0, 0, -1, forward);
		return (float) atan2(dot3(forward, up), dot3(forward, levelForward));
	}

	public void orbit(float[] v, float pan, float tilt, float[] out) {
		invAffine(v, m1);
		mvmulAffine(m1, 1, 0, 0, right);
		normalize3(right);

		setIdentity(m1);
		setIdentity(m2);

		m2[12] = -center[0];
		m2[13] = -center[1];
		m2[14] = -center[2];

		rotY(m1, -pan);
		mmulAffine(m1, m2, m2);

		setRotation(m1, right, -tilt);
		mmulAffine(m1, m2, m2);

		setIdentity(m1);
		setColumn3(m1, 3, center);

		mmulAffine(m1, m2, m2);
		mmulAffine(v, m2, out);
	}

	public void setCenter(float[] center) {
		if (Vecmath.hasNaNsOrInfinites(center)) {
			throw new IllegalArgumentException("center must not contain NaNs or infinites");
		}
		Vecmath.setf(this.center, center);
	}
}
