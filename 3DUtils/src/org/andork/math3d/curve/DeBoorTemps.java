package org.andork.math3d.curve;

import java.util.Arrays;

public class DeBoorTemps
{
	public DeBoorTemps( int degree )
	{
		weights = new float[ degree + 1 ][ ];
		for( int i = 0 ; i < weights.length ; i++ )
		{
			weights[ i ] = new float[ degree + 1 - i ];
		}
	}
	
	void setUp( int degree , float[ ] knots , float param )
	{
		if( param < knots[ 0 ] || param > knots[ knots.length - 1 ] )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + knots[ knots.length - 1 ] + "]" );
		}
		if( param == knots[ 0 ] )
		{
			return;
		}
		else if( param == knots[ knots.length - 1 ] )
		{
			return;
		}
		
		index = Arrays.binarySearch( knots , param );
		
		multiplicity = 0;
		
		if( index < 0 )
		{
			index = -( index + 1 );
		}
		
		while( index < knots.length - 1 && knots[ index + 1 ] == param )
		{
			index++ ;
		}
		
		for( int i = index ; i >= 0 && param == knots[ i ] ; i-- )
		{
			multiplicity++ ;
		}
		
		if( index > 0 && knots[ index ] != param )
		{
			index-- ;
		}
		
		int insertionCount = degree - multiplicity;
		
		for( int r = 0 ; r < insertionCount ; r++ )
		{
			for( int i = 0 ; i < degree - multiplicity - r ; i++ )
			{
				int ii = index - multiplicity - i;
				weights[ r ][ i ] = ( param - knots[ ii ] ) / ( knots[ ii + degree - r ] - knots[ ii ] );
			}
		}
	}
	
	int					index;
	int					multiplicity;
	final float[ ][ ]	weights;
}
