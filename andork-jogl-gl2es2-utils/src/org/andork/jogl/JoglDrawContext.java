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

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.geom.Frustum;

public interface JoglDrawContext {
	public float devicePixelRatio();

	public int height();

	public float[] inverseViewMatrix();

	public float[] projectionMatrix();

	public float[] viewportMatrix();

	public float[] inverseViewportMatrix();

	public float[] viewMatrix();

	public float[] worldToScreen();

	public float[] screenToWorld();

	public float[] viewToScreen();

	public float[] screenToView();

	public int width();

	public Projection projection();

	public default void getViewPoint(float[] out) {
		float[] vi = inverseViewMatrix();
		out[0] = vi[12];
		out[1] = vi[13];
		out[2] = vi[14];
	}

	public default void getViewPoint(double[] out) {
		float[] vi = inverseViewMatrix();
		out[0] = vi[12];
		out[1] = vi[13];
		out[2] = vi[14];
	}

	public JoglViewSettings settings();

	public Frustum frustum();

	public void applyFilters(GL3 gl3, JoglFilter... filters);
}
