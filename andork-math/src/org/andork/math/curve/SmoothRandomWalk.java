package org.andork.math.curve;

import java.util.Random;

import javax.vecmath.Point2f;

import org.andork.math.curve.BSplineGf.PointType;
import org.omg.CORBA.FloatHolder;

public class SmoothRandomWalk<T>
{
	private float					interval;
	private float[ ]				knots;
	private T[ ]					controlPoints;
	private PointType<T>			pointType;
	private BSplineGf<T>			spline;
	private RandomPointGenerator<T>	generator;
	
	private float					offset	= 0;
	private BSplineGf.Evaluator<T>	evaluator;
	
	public SmoothRandomWalk( int degree , float interval , PointType<T> pointType , RandomPointGenerator<T> generator )
	{
		this.interval = interval;
		this.pointType = pointType;
		this.generator = generator;
		knots = createKnots( degree , interval );
		controlPoints = createInitialPoints( degree , pointType , generator );
		spline = new BSplineGf<T>( degree , knots , controlPoints , pointType );
		evaluator = new BSplineGf.Evaluator<T>( degree , pointType );
	}
	
	private T[ ] createInitialPoints( int degree , PointType<T> pointType , RandomPointGenerator<T> generator )
	{
		T[ ] result = pointType.allocate( degree + 1 );
		for( int i = 0 ; i < result.length ; i++ )
		{
			generator.generateRandomPoint( result[ i ] );
		}
		return result;
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
	
	public void advance( float amount , T result )
	{
		while( offset + amount > interval )
		{
			for( int i = 0 ; i < controlPoints.length - 1 ; i++ )
			{
				pointType.set( controlPoints[ i ] , controlPoints[ i + 1 ] );
			}
			generator.generateRandomPoint( controlPoints[ controlPoints.length - 1 ] );
			spline = new BSplineGf<T>( spline.getDegree( ) , knots , controlPoints , pointType );
			
			amount -= interval;
		}
		
		// amount may be negative at this point
		offset += amount;
		evaluator.eval( spline , offset , result );
	}
	
	public static interface RandomPointGenerator<T>
	{
		public void generateRandomPoint( T point );
	}
	
	public static class RandomPoint2fGenerator implements RandomPointGenerator<Point2f>
	{
		float	min , max;
		Random	random	= new Random( );
		
		public RandomPoint2fGenerator( float min , float max )
		{
			super( );
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void generateRandomPoint( Point2f point )
		{
			point.x = ( float ) ( min + random.nextDouble( ) * ( max - min ) );
			point.y = ( float ) ( min + random.nextDouble( ) * ( max - min ) );
		}
	}
	
	public static class RandomFloatHolderGenerator implements RandomPointGenerator<FloatHolder>
	{
		float	min , max;
		Random	random	= new Random( );
		
		public RandomFloatHolderGenerator( float min , float max )
		{
			super( );
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void generateRandomPoint( FloatHolder point )
		{
			point.value = ( float ) ( min + random.nextDouble( ) * ( max - min ) );
		}
	}
}
