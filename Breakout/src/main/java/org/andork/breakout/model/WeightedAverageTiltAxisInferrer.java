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
package org.andork.breakout.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.andork.math3d.Vecmath;

public class WeightedAverageTiltAxisInferrer implements TiltAxisInferrer
{
	
	@Override
	public float[ ] inferTiltAxis( Collection<? extends Shot> shots )
	{
		if( shots.isEmpty( ) )
		{
			return new float[ ] { 0f , -1f , 0f };
		}
		
		double xyAngle = 0.0;
		double zyAngle = 0.0;
		
		double totalXyWeight = 0.0;
		double totalZyWeight = 0.0;
		
		for( Shot shot : shots )
		{
			double x = shot.to.position[ 0 ] - shot.from.position[ 0 ];
			double y = shot.to.position[ 1 ] - shot.from.position[ 1 ];
			double z = shot.to.position[ 2 ] - shot.from.position[ 2 ];
			
			double dxy = Math.sqrt( x * x + y * y );
			double dzy = Math.sqrt( z * z + y * y );
			
			xyAngle += dxy * Math.atan2( Math.signum( x ) * y , Math.abs( x ) );
			totalXyWeight += dxy;
			zyAngle += dzy * Math.atan2( Math.signum( z ) * y , Math.abs( z ) );
			totalZyWeight += dzy;
		}
		
		xyAngle /= totalXyWeight;
		zyAngle /= totalZyWeight;
		
		double[ ] xyNormal = { Math.cos( xyAngle ) , Math.sin( xyAngle ) , 0.0 };
		double[ ] zyProjection = { 0.0 , -Math.cos( zyAngle ) , Math.sin( zyAngle ) };
		
		double dot = Vecmath.dot3( xyNormal , zyProjection );
		
		float[ ] result = new float[ ] {
				( float ) ( zyProjection[ 0 ] - dot * xyNormal[ 0 ] ) ,
				( float ) ( zyProjection[ 1 ] - dot * xyNormal[ 1 ] ) ,
				( float ) ( zyProjection[ 2 ] - dot * xyNormal[ 2 ] )
		};
		
		Vecmath.normalize3( result );

		return result;
	}
}
