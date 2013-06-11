package org.andork.torquescape.model.section;

import java.util.ArrayList;

import org.andork.j3d.math.J3DTempsPool;

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
	public ArrayList<SectionCurve> eval( float param , J3DTempsPool pool , ArrayList<SectionCurve> result )
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
		section.outsideDirection.set( Math.signum( radius ), 0, 0 );
		
		for( int i = 0 ; i < numSides ; i++ )
		{
			float angle = ( float ) Math.PI * 2 * i / numSides;
			float x = ( float ) Math.cos( angle ) * radius;
			float y = ( float ) Math.sin( angle ) * radius;
			section.points[ i ].set( x , y , 0 );
			section.smoothFlags[ i ] = false;
		}
		
		return result;
	}
	
}
