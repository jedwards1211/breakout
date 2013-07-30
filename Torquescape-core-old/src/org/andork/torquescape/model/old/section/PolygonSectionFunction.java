package org.andork.torquescape.model.old.section;

import java.util.ArrayList;

public class PolygonSectionFunction implements ISectionFunction
{
	private int		numSides;
	private float	radius;
	
	public PolygonSectionFunction( int numSides , float radius )
	{
		this.numSides = numSides;
		this.radius = radius;
	}
	
	@Override
	public ArrayList<SectionCurve> eval( float param , ArrayList<SectionCurve> result )
	{
		while( result.size( ) > 1 )
		{
			result.remove( 1 );
		}
		
		if( result.size( ) == 1 && result.get( 0 ).points.length != numSides )
		{
			result.remove( 0 );
		}
		
		if( result.isEmpty( ) )
		{
			result.add( new SectionCurve( numSides ) );
		}
		
		SectionCurve section = result.get( 0 );
		section.outsideDirection[ 0 ] = Math.signum( radius );
		section.outsideDirection[ 1 ] = 0;
		section.outsideDirection[ 2 ] = 0;
		
		int k = 0;
		for( int i = 0 ; i < numSides ; i++ )
		{
			float angle = ( float ) Math.PI * 2 * i / numSides;
			float x = ( float ) Math.cos( angle ) * radius;
			float y = ( float ) Math.sin( angle ) * radius;
			section.points[ k++ ] = x;
			section.points[ k++ ] = y;
			section.points[ k++ ] = 0;
			section.smoothFlags[ i ] = false;
		}
		
		return result;
	}
	
}
