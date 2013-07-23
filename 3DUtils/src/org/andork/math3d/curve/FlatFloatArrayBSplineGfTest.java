package org.andork.math3d.curve;

import java.util.Arrays;

import org.andork.math3d.curve.BSplineGf.NurbsEvaluator;

public class FlatFloatArrayBSplineGfTest
{
	public static void main( String[ ] args )
	{
		float[ ] knots = {
				0 , 0 , 0 , 0 , 1 , 2 , 3 , 4 , 5 , 6 , 7 , 7 , 7 , 7
		};
		float[ ] controlPoints = {
				2 , 2 , 3 , 1 ,
				4 , 5 , 6 , 1 ,
				-2 , 8 , 4 , 1 ,
				3 , -5 , 7 , 1 ,
				2 , -3 , -5 , 1 ,
				-1 , -2 , -4 , 1 ,
				-5 , -3 , 0 , 1 ,
				0 , 8 , 1 , 1 ,
				2 , 8 , 1 , 1 ,
				3 , 8 , 1 , 1
		};
		
		Integer[ ] indices = new Integer[ controlPoints.length / 4 ];
		for( int i = 0 ; i < indices.length ; i++ )
		{
			indices[ i ] = i * 4;
		}
		
		FlatFloatArrayPointType pointType = new FlatFloatArrayPointType( controlPoints , 4 );
		
		BSplineGf<Integer> spline = new BSplineGf<Integer>( 3 , knots , indices , pointType );
		NurbsEvaluator<Integer> evaluator = new NurbsEvaluator<Integer>( 3 , pointType );
		
		Integer[ ] point = pointType.allocate( 1 );
		
		float[ ] pointOut = new float[ 4 ];
		for( float f = 0f ; f <= knots[ knots.length - 1 ] ; f += 0.01f )
		{
			evaluator.eval( spline , f , point[ 0 ] );
			pointType.get( point[ 0 ] , pointOut , 0 );
			System.out.println( f + ": " + Arrays.toString( pointOut ) );
		}
	}
	
}
