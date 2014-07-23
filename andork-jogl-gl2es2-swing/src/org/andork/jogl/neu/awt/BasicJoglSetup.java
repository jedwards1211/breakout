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
package org.andork.jogl.neu.awt;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.andork.jogl.neu.JoglScene;

public class BasicJoglSetup
{
	protected GLCanvas				canvas;
	protected JoglScene				scene;
	
	protected BasicNavigator		navigator;
	protected BasicOrbiter			orbiter;
	protected BasicOrthoNavigator	orthoNavigator;
	
	public BasicJoglSetup( )
	{
		this( createDefaultCanvas( ) );
	}
	
	public BasicJoglSetup( GLCanvas canvas )
	{
		this.canvas = canvas;
		init( );
	}
	
	public static GLCanvas createDefaultCanvas( )
	{
		final GLProfile glp = GLProfile.get( GLProfile.GL2ES2 );
		final GLCapabilities caps = new GLCapabilities( glp );
		GLCanvas canvas = new GLCanvas( caps );
		return canvas;
	}
	
	protected void init( )
	{
		scene = createScene( );
		canvas.addGLEventListener( scene );
		
		initMouseInput( );
		initKeyboardInput( );
	}
	
	protected JoglScene createScene( )
	{
		return new JoglScene( );
	}
	
	protected void initMouseInput( )
	{
		navigator = new BasicNavigator( this );
		
		canvas.addMouseListener( navigator );
		canvas.addMouseMotionListener( navigator );
		canvas.addMouseWheelListener( navigator );
		
		orbiter = new BasicOrbiter( this );
		
		canvas.addMouseListener( orbiter );
		canvas.addMouseMotionListener( orbiter );
		canvas.addMouseWheelListener( orbiter );
		
		orthoNavigator = new BasicOrthoNavigator( this );
	}
	
	protected void initKeyboardInput( )
	{
	}
	
	public GLCanvas getCanvas( )
	{
		return canvas;
	}
	
	public JoglScene getScene( )
	{
		return scene;
	}
	
	public BasicNavigator getNavigator( )
	{
		return navigator;
	}
	
	public BasicOrbiter getOrbiter( )
	{
		return orbiter;
	}
}
