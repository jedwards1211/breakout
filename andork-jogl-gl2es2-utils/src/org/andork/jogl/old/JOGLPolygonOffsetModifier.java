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
package org.andork.jogl.old;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;

public class JOGLPolygonOffsetModifier implements JOGLModifier {
	private boolean prevEnabled;

	private float factor, units;

	public JOGLPolygonOffsetModifier(float factor, float units) {
		super();
		this.factor = factor;
		this.units = units;
	}

	@Override
	public void afterDraw(GL2ES2 gl, JOGLObject object) {
		if (!prevEnabled) {
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		}
	}

	@Override
	public void beforeDraw(GL2ES2 gl, JOGLObject object) {
		prevEnabled = gl.glIsEnabled(GL.GL_POLYGON_OFFSET_FILL);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(factor, units);
	}

}
