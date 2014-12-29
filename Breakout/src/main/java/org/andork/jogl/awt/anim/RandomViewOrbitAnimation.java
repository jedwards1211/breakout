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
import org.andork.math3d.Orbiter;
import org.andork.math3d.Vecmath;
import org.andork.util.Reparam;

public class RandomViewOrbitAnimation implements Animation
{

	public RandomViewOrbitAnimation( GLAutoDrawable drawable , JoglViewSettings viewSettings , float[ ] center , float panRate , float minTilt , float maxTilt ,
			int period , int tiltPeriod )
	{
		if( period <= 0 )
		{
			throw new IllegalArgumentException( "period must be > 0" );
		}
		this.viewSettings = viewSettings;
		this.drawable = drawable;
		orbiter.setCenter( center );
		this.panRate = panRate;
		this.minTilt = minTilt;
		this.maxTilt = maxTilt;
		this.period = period;
		this.tiltPeriod = tiltPeriod;
	}

	JoglViewSettings		viewSettings;
	GLAutoDrawable	drawable;

	final float[ ]	v			= Vecmath.newMat4f( );

	Orbiter			orbiter		= new Orbiter( );

	int				period;
	int				tiltPeriod;

	float			panRate;
	float			minTilt;
	float			maxTilt;

	float			startTilt	= Float.NaN;
	float			endTilt		= Float.NaN;

	float			tiltParam;

	@Override
	public long animate( long animTime )
	{
		tiltParam += Math.PI * 2 * animTime / tiltPeriod;

		viewSettings.getViewXform( v );

		float currentTilt = orbiter.getTilt( v );
		if( Float.isNaN( startTilt ) )
		{
			if( currentTilt < minTilt )
			{
				startTilt = currentTilt;
				endTilt = maxTilt;
			}
			else if( currentTilt > maxTilt )
			{
				startTilt = minTilt;
				endTilt = maxTilt;
			}
			else
			{
				startTilt = minTilt;
				endTilt = maxTilt;
			}
		}

		if( tiltParam >= Math.PI )
		{
			startTilt = minTilt;
			endTilt = maxTilt;
		}

		float nextTilt = Reparam.linear( ( float ) Math.cos( tiltParam ) , 1 , -1 , startTilt , endTilt );

		orbiter.orbit( v , panRate * animTime / period , nextTilt - currentTilt , v );

		orbiter.orbit( v , panRate * animTime / period , 0 , v );

		viewSettings.setViewXform( v );

		drawable.display( );

		return period;
	}
}
