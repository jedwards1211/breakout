/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.old;

import javax.media.opengl.GL2ES2;

public class JOGLPolygonModeModifier implements JOGLModifier
{
	private int	cullFace	= GL2ES2.GL_NONE;
	
	public JOGLPolygonModeModifier( int cullFace )
	{
		super( );
		this.cullFace = cullFace;
	}

	@Override
	public void beforeDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glEnable( GL2ES2.GL_CULL_FACE );
		gl.glCullFace( cullFace );
	}
	
	@Override
	public void afterDraw( GL2ES2 gl , JOGLObject object )
	{
		gl.glDisable( GL2ES2.GL_CULL_FACE );
	}
	
}