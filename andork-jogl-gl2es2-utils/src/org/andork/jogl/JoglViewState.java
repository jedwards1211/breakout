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

import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.invertGeneral;
import static org.andork.math3d.Vecmath.mmul;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.ortho;
import static org.andork.math3d.Vecmath.setIdentity;

import org.andork.math3d.PickXform;

public class JoglViewState implements JoglDrawContext {
	private int width;

	private int height;
	
	private float devicePixelRatio;

	/**
	 * The view matrix.
	 */
	private final float[] v = newMat4f();

	/**
	 * The inverse of the view matrix.
	 */
	private final float[] vi = newMat4f();

	/**
	 * The projection matrix.
	 */
	private final float[] p = newMat4f();

	/**
	 * The inverse of the projection matrix.
	 */
	private final float[] pi = newMat4f();

	/**
	 * Transforms from screen coordinates to clipping coordinates.
	 */
	private final float[] inverseViewport = newMat4f();

	/**
	 * Transforms from screen coordinates to clipping coordinates.
	 */
	private final float[] viewport = newMat4f();

	/**
	 * Transforms from model coordinates to screen coordinates.
	 */
	private final float[] worldToScreen = newMat4f();

	/**
	 * Transforms from screen coordinates to world coordinates.
	 */
	private final float[] screenToWorld = newMat4f();

	/**
	 * Transforms from screen coordinates to view coordinates.
	 */
	private final float[] screenToView = newMat4f();

	/**
	 * Transforms from view coordinates to screen coordinates.
	 */
	private final float[] viewToScreen = newMat4f();

	/**
	 * Transforms from screen coordinates to a ray in virtual world coordinates.
	 */
	private final PickXform pickXform = new PickXform();
	
	private Projection projection;
	
	private JoglViewSettings settings;

	public JoglViewState() {
		update(new JoglViewSettings(), 0, 0, 100, 100);
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public float[] inverseViewMatrix() {
		return vi;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andork.jogl.neu2.JoglCamera#pickXform()
	 */
	public PickXform pickXform() {
		return pickXform;
	}

	@Override
	public float[] projectionMatrix() {
		return p;
	}

	@Override
	public float[] viewportMatrix() {
		return viewport;
	}

	@Override
	public float[] inverseViewportMatrix() {
		return inverseViewport;
	}

	public void update(JoglViewSettings settings, int x, int y, int width, int height) {
		update(settings, x, y, width, height, 1f);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andork.jogl.neu2.JoglCamera#updatePickXform()
	 */
	public void update(JoglViewSettings settings, int x, int y, int width, int height, float devicePixelRatio) {
		this.settings = settings;
		this.width = width;
		this.height = height;
		this.devicePixelRatio = devicePixelRatio;
		
		ortho(inverseViewport, x, x + width, y, y + height, -1, 1);
		invAffine(inverseViewport, viewport);

		if (settings != null) {
			projection = settings.getProjection();

			settings.getViewXform(v);
			settings.getInvViewXform(vi);
			settings.getProjection().calculate(p, this, width, height);
			invertGeneral(p, pi);
		} else {
			projection = new PerspectiveProjection((float) Math.PI, 1e-6f, 1e4f);
			setIdentity(v);
			setIdentity(vi);
			setIdentity(p);
			setIdentity(pi);
		}

		mmul(viewport, p, viewToScreen);
		mmul(viewToScreen, v, worldToScreen);
		mmul(pi, inverseViewport, screenToView);
		mmul(vi, screenToView, screenToWorld);

		pickXform.calculate(p, v);
	}
	
	public JoglViewSettings settings() {
		return settings;
	}

	@Override
	public float[] viewMatrix() {
		return v;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public float[] worldToScreen() {
		return worldToScreen;
	}

	@Override
	public float[] screenToWorld() {
		return screenToWorld;
	}

	@Override
	public float[] viewToScreen() {
		return viewToScreen;
	}

	@Override
	public float[] screenToView() {
		return screenToView;
	}

	@Override
	public Projection projection() {
		return projection;
	}

	@Override
	public float devicePixelRatio( )
	{
		return devicePixelRatio;
	}
}
