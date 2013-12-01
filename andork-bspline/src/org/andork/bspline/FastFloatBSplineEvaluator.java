package org.andork.bspline;

public class FastFloatBSplineEvaluator extends FloatBSplineEvaluator
{
	private float[ ]	points;
	private int			pointsStride;
	private float[ ]	knots;
	private float		param;
	private int			knotIndex;
	private int			multiplicity;
	
	public FastFloatBSplineEvaluator bspline( FloatArrayBSpline bspline )
	{
		return degree( bspline.degree ).dimension( bspline.dimension ).points( bspline.points ).knots( bspline.knots )
				.pointsStride( bspline.dimension );
	}
	
	@Override
	public FastFloatBSplineEvaluator degree( int degree )
	{
		return ( FastFloatBSplineEvaluator ) super.degree( degree );
	}
	
	@Override
	public FastFloatBSplineEvaluator dimension( int dimension )
	{
		return ( FastFloatBSplineEvaluator ) super.dimension( dimension );
	}
	
	public FastFloatBSplineEvaluator points( float[ ] points )
	{
		this.points = points;
		return this;
	}
	
	public FastFloatBSplineEvaluator pointsStride( int pointsStride )
	{
		this.pointsStride = pointsStride;
		return this;
	}
	
	public FastFloatBSplineEvaluator knots( float[ ] knots )
	{
		if( this.knots != knots )
		{
			this.knots = knots;
			knotIndex = -1;
		}
		return this;
	}
	
	public void splineModified( )
	{
		knotIndex = -1;
	}
	
	public void eval( float param , float[ ] out )
	{
		eval( param , out , 0 );
	}
	
	public void eval( float param , float[ ] out , int outIndex )
	{
		updateState( param );
		eval( degree , dimension , knots , knotIndex , multiplicity , points , pointsStride , param , out , outIndex );
	}
	
	private void updateState( float param )
	{
		float lastKnot = knots[ knots.length - 1 ];
		
		if( param < knots[ 0 ] || param > lastKnot )
		{
			throw new IllegalArgumentException( "param (" + param + ") is out of range: [" + knots[ 0 ] + ", " + lastKnot + ")" );
		}
		
		if( param == lastKnot )
		{
			knotIndex = knots.length - 1;
			multiplicity = 1;
			while( knotIndex > 0 && knots[ knotIndex - 1 ] == lastKnot )
			{
				knotIndex-- ;
				multiplicity++ ;
			}
		}
		else if( knotIndex >= 0 && param > this.param )
		{
			if( param < knots[ knotIndex + 1 ] )
			{
				multiplicity = 0;
			}
			else
			{
				knotIndex++ ;
				multiplicity = 1;
				
				// NOTE: param < lastKnot, so index + 1 must be in bounds
				
				float nextKnot = knots[ knotIndex ];
				while( knots[ knotIndex + 1 ] == nextKnot )
				{
					knotIndex++ ;
					multiplicity++ ;
				}
				if( knots[ knotIndex + 1 ] < param )
				{
					knotIndex = -1;
				}
				else if( knots[ knotIndex ] != param )
				{
					multiplicity = 0;
				}
			}
		}
		
		this.param = param;
		
		if( knotIndex < 0 || param < knots[ knotIndex ] )
		{
			knotIndex = BSplines.binarySearch( knots , 0 , knots.length , param );
			multiplicity = 0;
			for( int i = knotIndex ; i >= 0 && knots[ i ] == param ; i-- )
			{
				multiplicity++ ;
			}
		}
	}
	
}
