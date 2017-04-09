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

import java.util.ArrayList;
import java.util.List;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglResource;

import com.jogamp.opengl.GL2ES2;

public class JOGLGroup implements JOGLObject, JoglResource {
	public Object userObj;
	public List<JOGLResource> objects = new ArrayList<JOGLResource>();

	public JOGLGroup() {
		super();
	}

	public JOGLGroup(Object userObj) {
		this.userObj = userObj;
	}

	@Override
	public void destroy(GL2ES2 gl) {
		for (JOGLResource object : objects) {
			object.destroy(gl);
		}
	}

	@Override
	public void dispose(GL2ES2 gl) {
		destroy(gl);
	}

	@Override
	public void draw(GL2ES2 gl, float[] m, float[] n, float[] v, float[] p) {
		for (JOGLResource object : objects) {
			if (object instanceof JOGLObject) {
				((JOGLObject) object).draw(gl, m, n, v, p);
			}
		}
	}

	@Override
	public void draw(JoglDrawContext context, GL2ES2 gl, float[] m, float[] n) {
		draw(gl, m, n, context.viewMatrix(), context.projectionMatrix());
	}

	@Override
	public void init(GL2ES2 gl) {
		for (JOGLResource object : objects) {
			object.init(gl);
		}
	}

}
