package org.andork.math.curve;

import java.util.Random;

public class FloatArraySmoothRandomWalk
{
	private float						interval;
	private FloatArrayBSpline			spline;
	private FastFloatBSplineEvaluator	evaluator;
	private RandomPointGenerator		generator;
	
	private float						offset	= 0;
	
	public FloatArraySmoothRandomWalk( int degree , int dimension , float interval , RandomPointGenerator generator )
	{
		this.interval = interval;
		spline = new FloatArrayBSpline( );
		spline.degree = degree;
		spline.dimension = dimension;
		this.generator = generator;
		spline.knots = createKnots( degree , interval );
		spline.points = new float[ ( spline.degree + 1 ) * spline.dimension ];
		for( int i = 0 ; i < spline.points.length ; i += spline.dimension )
		{
			generator.generateRandomPoint( spline.points , i , spline.dimension );
		}
		evaluator = new FastFloatBSplineEvaluator( ).bspline( spline );
	}
	
	private static float[ ] createKnots( int degree , float interval )
	{
		float[ ] result = new float[ ( degree + 1 ) * 2 ];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[ i ] = interval * ( i - degree );
		}
		
		return result;
	}
	
	public void advance( float amount , float[ ] result )
	{
		while( offset + amount > interval )
		{
			for( int i = 0 ; i < spline.points.length - spline.dimension ; i += spline.dimension )
			{
				System.arraycopy( spline.points , i + spline.dimension , spline.points , i , spline.dimension );
			}
			generator.generateRandomPoint( spline.points , spline.points.length - spline.dimension , spline.dimension );
			evaluator.splineModified( );
			
			amount -= interval;
		}
		
		// amount may be negative at this point
		offset += amount;
		evaluator.eval( offset , result );
	}
	
	public static interface RandomPointGenerator
	{
		public void generateRandomPoint( float[ ] out , int start , int dimension );
	}
	
	public static class DefaultRandomPointGenerator implements RandomPointGenerator
	{
		float	min , max;
		Random	random	= new Random( );
		
		public DefaultRandomPointGenerator( float min , float max )
		{
			super( );
			this.min = min;
			this.max = max;
		}

		@Override
		public void generateRandomPoint( float[ ] out , int start , int dimension )
		{
			for( int i = start ; i < start + dimension ; i++ )
			{
				out[ i ] = min + random.nextFloat( ) * ( max - min );
			}
		}
	}
}
