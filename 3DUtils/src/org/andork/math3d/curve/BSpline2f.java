package org.andork.math3d.curve;

import javax.vecmath.Point2f;

import org.andork.vecmath.VecmathUtils;

public class BSpline2f
{
	int			degree;
	float[ ]	knots;
	Point2f[ ]	controlPoints;
	BSpline2f	derivative;
	
	public BSpline2f( int degree , float[ ] knots , Point2f[ ] controlPoints )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
	}
	
	public int getDegree( )
	{
		return degree;
	}
	
	public BSpline2f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			Point2f[ ] derivControlPoints = VecmathUtils.allocPoint2fArray( controlPoints.length - 1 );
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ].sub( controlPoints[ i + 1 ] , controlPoints[ i ] );
				derivControlPoints[ i ].scale( factor );
			}
			
			derivative = new BSpline2f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
	
	public static class Evaluator extends BSplineEvaluatorFloat
	{
		Point2f[ ]	deBoorPoints;
		
		public Evaluator( int degree )
		{
			super( degree );
			deBoorPoints = VecmathUtils.allocPoint2fArray( degree + 1 );
		}
		
		public void eval( BSpline2f s , float param , Point2f result )
		{
			updateState( param , s.knots );
			
			for( int i = 0 ; i <= s.degree - multiplicity ; i++ )
			{
				deBoorPoints[ i ].set( s.controlPoints[ index - multiplicity - i ] );
			}
			
			for( int r = 0 ; r < s.degree - multiplicity ; r++ )
			{
				for( int i = 0 ; i < s.degree - multiplicity - r ; i++ )
				{
					int ii = index - multiplicity - i;
					float a = ( param - knots[ ii ] ) / ( knots[ ii + s.degree - r ] - knots[ ii ] );
					deBoorPoints[ i ].interpolate( deBoorPoints[ i + 1 ] , deBoorPoints[ i ] , a );
				}
			}
			
			result.set( deBoorPoints[ 0 ] );
		}
	}
	
	public static class NurbsEvaluator
	{
		Evaluator	evaluator;
		Point2f		A;
		Point2f		Ap;
		Point2f		App;
		
		public NurbsEvaluator( int degree )
		{
			evaluator = new Evaluator( degree );
			A = new Point2f( );
			Ap = new Point2f( );
			App = new Point2f( );
		}
		
		public void eval( BSpline2f s , float param , Point2f result )
		{
			evaluator.eval( s , param , result );
			result.scale( 1 / result.y );
		}
		
		public void evalDerivative( BSpline2f s , float param , Point2f result , Point2f derivResult )
		{
			evaluator.eval( s , param , A );
			float rw = A.y;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			derivResult.scaleAdd( -Ap.y , result , Ap );
			derivResult.scale( rw );
		}
		
		public void eval2ndDerivative( BSpline2f s , float param , Point2f result , Point2f derivResult , Point2f deriv2Result )
		{
			evaluator.eval( s , param , A );
			float rw = 1f / A.y;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = Ap.y;
			derivResult.scaleAdd( -wp , result , Ap );
			derivResult.scale( rw );
			
			evaluator.eval( s.getDerivative( ).getDerivative( ) , param , App );
			float wpp = App.y;
			
			deriv2Result.scale( -wpp , result );
			deriv2Result.scaleAdd( -2 * wp , derivResult , deriv2Result );
			deriv2Result.add( App );
			deriv2Result.scale( rw );
		}
	}
}
