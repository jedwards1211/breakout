package org.andork.math3d.curve;

import java.util.Arrays;

public class DeBoorTemps
{
	public DeBoorTemps( int maxDegree )
	{
		weights = new float[ maxDegree + 1 ][ ];
		for( int i = 0 ; i < weights.length ; i++ )
		{
			weights[ i ] = new float[ maxDegree + 1 - i ];
		}
	}
	
	void clear( )
	{
		degree = 0;
		knots = null;
	}
	
	void initOrIterate( int degree , float[ ] knots , float param )
	{
		if( this.degree == degree && this.knots == knots && this.param < param )
		{
			iterate( param );
		}
		else
		{
			init( degree , knots , param );
		}
	}
	
	void init( int degree , float[ ] knots , float param )
	{
		if( param < knots[ 0 ] || param > knots[ knots.length - 1 ] )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + knots[ knots.length - 1 ] + "]" );
		}
		
		this.degree = degree;
		this.knots = knots;
		this.param = param;
		
		if( param == knots[ 0 ] )
		{
			multiplicity = 1;
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
		
		if( index > 0 && knots[ index ] != param )
		{
			index-- ;
		}
		
		for( int i = index ; i >= 0 && param == knots[ i ] ; i-- )
		{
			multiplicity++ ;
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
	
	/**
	 * Efficiently recomputes the values for the next parameter provided it is between the same two knots or the next two knots.
	 * 
	 * @param nextParam
	 *            the next curve parameter. Should be greater than used in the last call to {@link #init(int, float[], float)} or this method.
	 */
	void iterate( float nextParam )
	{
		if( param == nextParam )
		{
			return;
		}
		
		param = nextParam;
		
		while( index + 1 < knots.length && knots[ index + 1 ] <= nextParam )
		{
			index++ ;
			if( knots[ index ] == nextParam )
			{
				multiplicity++ ;
			}
			else
			{
				multiplicity = 0;
			}
		}
		
		if( knots[ index ] <= nextParam )
		{
			int insertionCount = degree - multiplicity;
			
			for( int r = 0 ; r < insertionCount ; r++ )
			{
				for( int i = 0 ; i < degree - multiplicity - r ; i++ )
				{
					int ii = index - multiplicity - i;
					weights[ r ][ i ] = ( nextParam - knots[ ii ] ) / ( knots[ ii + degree - r ] - knots[ ii ] );
				}
			}
		}
	}
	
	int					degree;
	float[ ]			knots;
	float				param;
	int					index;
	int					multiplicity;
	final float[ ][ ]	weights;
}
