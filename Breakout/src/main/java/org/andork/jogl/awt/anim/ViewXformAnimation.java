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
package org.andork.jogl.awt.anim;

import java.util.function.Function;

import javax.media.opengl.awt.GLCanvas;

import org.andork.awt.anim.Animation;
import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;

public class ViewXformAnimation implements Animation
{
	/**
	 * @param setup
	 * @param totalTime
	 * @param function
	 *        a function that takes the animation progress from 0 to 1, and returns the new inverted view matrix.
	 */
	public ViewXformAnimation( BasicJoglSetup setup , long totalTime , boolean display , Function<Float, float[ ]> function )
	{
		this.scene = setup.getScene( );
		this.canvas = setup.getCanvas( );
		this.totalTime = totalTime;
		this.display = display;
		this.function = function;
	}
	
	JoglScene					scene;
	GLCanvas					canvas;
	
	long						elapsedTime	= 0;
	
	long						totalTime;
	boolean						display;
	Function<Float, float[ ]>	function;
	
	@Override
	public long animate( long animTime )
	{
		elapsedTime += animTime;
		
		float f = Math.min( 1f , ( float ) elapsedTime / totalTime );
		
		scene.setViewXform( function.apply( f ) );
		if( display )
		{
			canvas.display( );
		}
		
		return Math.min( 30 , Math.max( 0 , totalTime - elapsedTime ) );
	}
}
