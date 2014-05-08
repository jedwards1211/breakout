package org.andork.breakout.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.andork.math3d.Vecmath;

public class WeightedAverageTiltAxisInferrer implements TiltAxisInferrer
{
	
	@Override
	public float[ ] inferTiltAxis( Collection<? extends SurveyShot> shots )
	{
		if( shots.isEmpty( ) )
		{
			return new float[ ] { 0f , -1f , 0f };
		}
		
		double xyAngle = 0.0;
		double zyAngle = 0.0;
		
		double totalXyWeight = 0.0;
		double totalZyWeight = 0.0;
		
		for( SurveyShot shot : shots )
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
