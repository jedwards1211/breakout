package org.andork.frf.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.andork.math3d.Vecmath;

public class MedianTiltAxisInferrer implements TiltAxisInferrer
{
	
	@Override
	public float[ ] inferTiltAxis( Collection<? extends SurveyShot> shots )
	{
		if( shots.isEmpty( ) )
		{
			return new float[ ] { 0f , -1f , 0f };
		}
		
		List<Double> xyAngles = new ArrayList<Double>( );
		List<Double> zyAngles = new ArrayList<Double>( );
		
		for( SurveyShot shot : shots )
		{
			double x = shot.to.position[ 0 ] - shot.from.position[ 0 ];
			double y = shot.to.position[ 1 ] - shot.from.position[ 1 ];
			double z = shot.to.position[ 2 ] - shot.from.position[ 2 ];
			
			xyAngles.add( Math.atan2( Math.signum( x ) * y , Math.abs( x ) ) );
			zyAngles.add( Math.atan2( Math.signum( z ) * y , Math.abs( z ) ) );
		}
		
		Collections.sort( xyAngles );
		Collections.sort( zyAngles );
		
		double xyAngle = xyAngles.get( xyAngles.size( ) / 2 );
		double zyAngle = zyAngles.get( zyAngles.size( ) / 2 );
		
		if( xyAngles.size( ) % 2 == 0 )
		{
			xyAngle = ( xyAngles.get( xyAngles.size( ) / 2 - 1 ) + xyAngle ) * 0.5;
		}
		if( zyAngles.size( ) % 2 == 0 )
		{
			zyAngle = ( zyAngles.get( zyAngles.size( ) / 2 - 1 ) + zyAngle ) * 0.5;
		}
		
		double[ ] xyNormal = { Math.cos( xyAngle ) , Math.sin( xyAngle ) , 0.0 };
		double[ ] zyProjection = { 0.0 , -Math.cos( zyAngle ) , Math.sin( zyAngle ) };
		
		double dot = Vecmath.dot3( xyNormal , zyProjection );
		
		return new float[ ] {
				( float ) ( zyProjection[ 0 ] - dot * xyNormal[ 0 ] ) ,
				( float ) ( zyProjection[ 1 ] - dot * xyNormal[ 1 ] ) ,
				( float ) ( zyProjection[ 2 ] - dot * xyNormal[ 2 ] )
		};
	}
}
