package org.andork.math3d.curve;

import java.util.Arrays;

import javax.vecmath.Point3f;

import org.andork.vecmath.VecmathUtils;

public class BSpline3f
{
	int			degree;
	float[ ]	knots;
	Point3f[ ]	controlPoints;
	BSpline3f	derivative;
	
	public BSpline3f( int degree , float[ ] knots , Point3f[ ] controlPoints )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
	}
	
	public BSpline3f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			Point3f[ ] derivControlPoints = VecmathUtils.allocPoint3fArray( controlPoints.length - 1 );
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ].sub( controlPoints[ i + 1 ] , controlPoints[ i ] );
				derivControlPoints[ i ].scale( factor );
			}
			
			derivative = new BSpline3f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
	
	public static class Evaluator
	{
		public Evaluator( int degree )
		{
			deBoorPoints = VecmathUtils.allocPoint3fArray( degree + 1 );
		}
		
		public Point3f eval( BSpline3f s , DeBoorTemps t , Point3f result )
		{
			if( t.param == s.knots[ 0 ] )
			{
				result.set( s.controlPoints[ 0 ] );
				return result;
			}
			else if( t.param == s.knots[ s.knots.length - 1 ] )
			{
				result.set( s.controlPoints[ s.controlPoints.length - 1 ] );
				return result;
			}
			
			for( int i = 0 ; i <= s.degree - t.multiplicity ; i++ )
			{
				deBoorPoints[ i ].set( s.controlPoints[ t.index - t.multiplicity - i ] );
			}
			
			for( int r = 0 ; r < s.degree - t.multiplicity ; r++ )
			{
				for( int i = 0 ; i < s.degree - t.multiplicity - r ; i++ )
				{
					float a = t.weights[ r ][ i ];
					deBoorPoints[ i ].interpolate( deBoorPoints[ i + 1 ] , deBoorPoints[ i ] , a );
				}
			}
			
			result.set( deBoorPoints[ 0 ] );
			return result;
		}
		
		private final Point3f[ ]	deBoorPoints;
	}
}
