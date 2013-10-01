package org.andork.math.curve;

import java.util.Iterator;

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
	
	public int getDegree( )
	{
		return degree;
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
	
	public static class Evaluator extends BSplineEvaluatorFloat
	{
		Point3f[ ]	deBoorPoints;
		
		public Evaluator( int degree )
		{
			super( degree );
			deBoorPoints = VecmathUtils.allocPoint3fArray( degree + 1 );
		}
		
		public void eval( BSpline3f s , float param , Point3f result )
		{
			updateState( param , s.knots );
			
			if( param == s.knots[ 0 ] )
			{
				result.set( s.controlPoints[ 0 ] );
				return;
			}
			else if( param == s.knots[ s.knots.length - 1 ] )
			{
				result.set( s.controlPoints[ s.controlPoints.length - 1 ] );
				return;
			}
			
			for( int i = 0 ; i <= s.degree - multiplicity ; i++ )
			{
				deBoorPoints[ i ].set( s.controlPoints[ Math.max( 0 , Math.min( s.controlPoints.length - 1 , index - multiplicity - i ) ) ] );
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
		Point3f		A;
		Point3f		Ap;
		Point3f		App;
		
		public NurbsEvaluator( int degree )
		{
			evaluator = new Evaluator( degree );
			A = new Point3f( );
			Ap = new Point3f( );
			App = new Point3f( );
		}
		
		public void eval( BSpline3f s , float param , Point3f result )
		{
			evaluator.eval( s , param , result );
			result.scale( 1 / result.z );
		}
		
		public void evalDerivative( BSpline3f s , float param , Point3f result , Point3f derivResult )
		{
			evaluator.eval( s , param , A );
			float rw = A.z;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			derivResult.scaleAdd( -Ap.z , result , Ap );
			derivResult.scale( rw );
		}
		
		public void eval2ndDerivative( BSpline3f s , float param , Point3f result , Point3f derivResult , Point3f deriv2Result )
		{
			evaluator.eval( s , param , A );
			float rw = 1f / A.z;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = Ap.z;
			derivResult.scaleAdd( -wp , result , Ap );
			derivResult.scale( rw );
			
			evaluator.eval( s.getDerivative( ).getDerivative( ) , param , App );
			float wpp = App.z;
			
			deriv2Result.scale( -wpp , result );
			deriv2Result.scaleAdd( -2 * wp , derivResult , deriv2Result );
			deriv2Result.add( App );
			deriv2Result.scale( rw );
		}
	}
	
	public static class EvaluatorIterator implements Iterator<Point3f>
	{
		BSpline3f		spline;
		Evaluator		evaluator;
		Iterator<Float>	paramIter;
		
		public EvaluatorIterator( BSpline3f spline , Evaluator evaluator , Iterator<Float> paramIter )
		{
			super( );
			this.spline = spline;
			this.evaluator = evaluator;
			this.paramIter = paramIter;
		}
		
		@Override
		public boolean hasNext( )
		{
			return paramIter.hasNext( );
		}
		
		@Override
		public Point3f next( )
		{
			Point3f result = new Point3f( );
			evaluator.eval( spline , paramIter.next( ) , result );
			return result;
		}
		
		@Override
		public void remove( )
		{
			throw new UnsupportedOperationException( );
		}
	}
	
	public static Iterable<Point3f> iterable( final BSpline3f spline , final Evaluator evaluator , final Iterable<Float> paramIter )
	{
		return new Iterable<Point3f>( )
		{
			@Override
			public Iterator<Point3f> iterator( )
			{
				return new EvaluatorIterator( spline , evaluator , paramIter.iterator( ) );
			}
		};
	}
}
