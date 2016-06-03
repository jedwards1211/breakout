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

import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.ortho;
import static org.andork.math3d.Vecmath.setIdentity;

import org.andork.math3d.PickXform;

public class JoglViewState implements JoglDrawContext {
	private int width;

	private int height;

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
	 * Transforms from pixel space coordinates to clipping coordinates.
	 */
	private final float[] screenXform = newMat4f();

	/**
	 * The size of a pixel in clipping coordinates.
	 */
	private final float[] pixelScale = new float[2];

	/**
	 * Transforms from screen coordinates to a ray in virtual world coordinates.
	 */
	private final PickXform pickXform = new PickXform();

	public JoglViewState() {
		update(new JoglViewSettings(), 100, 100);
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public float[] inverseViewXform() {
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
	public float[] pixelScale() {
		return pixelScale;
	}

	@Override
	public float[] projXform() {
		return p;
	}

	@Override
	public float[] screenXform() {
		return screenXform;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.andork.jogl.neu2.JoglCamera#updatePickXform()
	 */
	public void update(JoglViewSettings settings, int width, int height) {
		this.width = width;
		this.height = height;

		ortho(screenXform, 0, width, 0, height, 1, -1);
		pixelScale[0] = screenXform[0];
		pixelScale[1] = screenXform[5];

		if (settings != null) {
			settings.getViewXform(v);
			settings.getInvViewXform(vi);
			settings.getProjection().calculate(this, p);
		} else {
			setIdentity(v);
			setIdentity(vi);
			setIdentity(p);
		}

		pickXform.calculate(p, v);
	}

	@Override
	public float[] viewXform() {
		return v;
	}

	@Override
	public int width() {
		return width;
	}
}
