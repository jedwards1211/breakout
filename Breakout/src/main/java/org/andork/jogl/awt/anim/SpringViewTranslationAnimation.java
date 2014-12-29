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
import org.andork.util.AnimationUtils;

public class SpringViewTranslationAnimation implements Animation
{

	public SpringViewTranslationAnimation( GLAutoDrawable drawable , JoglViewSettings viewSettings , float[ ] target ,
		float factor , float extra , int period )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		this.viewSettings = viewSettings;
		this.drawable = drawable;
		Vecmath.setf( this.target , target );
		this.factor = factor;
		this.extra = extra;
		this.period = period;
	}

	JoglViewSettings	viewSettings;
	GLAutoDrawable		drawable;

	final float[ ]		target	= new float[ 3 ];

	final float[ ]		v		= Vecmath.newMat4f( );

	int					period;

	float				factor;
	float				extra;

	@Override
	public long animate( long animTime )
	{
		viewSettings.getViewXform( v );
		Vecmath.invAffine( v );

		v[ 12 ] = AnimationUtils.animate( v[ 12 ] , target[ 0 ] , animTime , factor , extra , period );
		v[ 13 ] = AnimationUtils.animate( v[ 13 ] , target[ 1 ] , animTime , factor , extra , period );
		v[ 14 ] = AnimationUtils.animate( v[ 14 ] , target[ 2 ] , animTime , factor , extra , period );

		boolean done = v[ 12 ] == target[ 0 ] && v[ 13 ] == target[ 1 ] && v[ 14 ] == target[ 2 ];

		Vecmath.invAffine( v );

		viewSettings.setViewXform( v );
		drawable.display( );

		return done ? 0 : period;
	}
}
