package org.andork.torquescape.model.section;

import org.andork.j3d.math.J3DTempsPool;

public class PolygonCrossSectionFunction implements ICrossSectionFunction
{
	private int		numSides;
	private float	radius;
	
	public PolygonCrossSectionFunction( int numSides , float radius )
	{
		this.numSides = numSides;
		this.radius = radius;
	}
	
	@Override
	public ICrossSectionCurve[ ] eval( float param , J3DTempsPool pool )
	{
		DefaultCrossSectionCurve result = new DefaultCrossSectionCurve( numSides );
		for( int i = 0 ; i < numSides ; i++ )
		{
			float angle = ( float ) Math.PI * 2 * i / numSides;
			float x = ( float ) Math.cos( angle ) * radius;
			float y = ( float ) Math.sin( angle ) * radius;
			result.getPoint( i ).set( x , y , 0 );
			angle = ( float ) Math.PI * 2 * ( i + 0.5f ) / numSides;
			x = ( float ) Math.cos( angle );
			y = ( float ) Math.sin( angle );
			result.getNextSegmentNormal( i ).set( x , y , 0 );
			result.getPrevSegmentNormal( ( i + 1 ) % numSides ).set( x , y , 0 );
		}
		return new ICrossSectionCurve[ ] { result };
	}
	
}
