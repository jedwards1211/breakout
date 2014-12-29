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

import javax.media.opengl.GLAutoDrawable;

import org.andork.awt.anim.Animation;
import org.andork.jogl.JoglViewSettings;
import org.andork.math3d.Vecmath;

public class SinusoidalViewTranslationAnimation implements Animation
{

	public SinusoidalViewTranslationAnimation( GLAutoDrawable drawable , JoglViewSettings viewSettings , float[ ] end ,
		long period , long duration )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		if( duration <= 0 )
		{
			throw new IllegalArgumentException( "duration must be > 0" );
		}
		this.viewSettings = viewSettings;
		this.drawable = drawable;
		Vecmath.setf( this.end , end );
		this.period = period;
		this.duration = duration;
	}

	JoglViewSettings	viewSettings;
	GLAutoDrawable		drawable;

	final float[ ]		start	= new float[ 3 ];
	final float[ ]		end		= new float[ 3 ];

	final float[ ]		v		= Vecmath.newMat4f( );

	long				period;
	long				duration;
	long				elapsed;

	@Override
	public long animate( long animTime )
	{
		viewSettings.getViewXform( v );

		Vecmath.invAffine( v );

		if( elapsed == 0 )
		{
			Vecmath.getColumn3( v , 3 , start );
		}

		elapsed = Math.min( duration , elapsed + animTime );

		float f = ( float ) ( 0.5 * ( 1 - Math.cos( Math.PI * elapsed / duration ) ) );
		float rf = 1f - f;

		v[ 12 ] = rf * start[ 0 ] + f * end[ 0 ];
		v[ 13 ] = rf * start[ 1 ] + f * end[ 1 ];
		v[ 14 ] = rf * start[ 2 ] + f * end[ 2 ];

		Vecmath.invAffine( v );

		viewSettings.setViewXform( v );

		drawable.display( );

		return elapsed >= duration ? 0 : Math.min( period , duration - elapsed );
	}
}
