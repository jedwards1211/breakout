package org.andork.math3d.curve;

public abstract class BSplineEvaluatorFloat
{
	protected BSplineEvaluatorFloat( int degree )
	{
		this.degree = degree;
		
		index = -1;
	}
	
	protected int		degree;
	
	protected float[ ]	knots;
	protected float		param;
	protected int		index;
	protected int		multiplicity;
	
	protected void updateState( float param , float[ ] knots )
	{
		if( this.knots != knots )
		{
			index = -1;
		}
		
		this.knots = knots;
		
		float lastKnot = knots[ knots.length - 1 ];
		
		if( param < knots[ 0 ] || param > lastKnot )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + lastKnot + ")" );
		}
		
		if( param == lastKnot )
		{
			index = knots.length - 1;
			multiplicity = 1;
			while( index > 0 && knots[ index - 1 ] == lastKnot )
			{
				index-- ;
				multiplicity++ ;
			}
		}
		else if( index >= 0 && param > this.param )
		{
			index++ ;
			multiplicity = 1;
			
			// NOTE: param < lastKnot, so index + 1 must be in bounds
			
			float nextKnot = knots[ index ];
			while( knots[ index + 1 ] == nextKnot )
			{
				index++ ;
				multiplicity++ ;
			}
			if( knots[ index + 1 ] < param )
			{
				index = -1;
			}
			else if( knots[ index ] != param )
			{
				multiplicity = 0;
			}
		}
		
		this.param = param;
		
		if( index < 0 )
		{
			index = BSplineUtils.binarySearch( knots , 0 , knots.length , param );
			multiplicity = 0;
			for( int i = index ; i >= 0 && knots[ i ] == param ; i-- )
			{
				multiplicity++ ;
			}
		}
	}
}
