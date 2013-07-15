package org.andork.math3d.curve;

import javax.vecmath.Point4f;

import org.andork.vecmath.VecmathUtils;

public class BSpline4f
{
	int			degree;
	float[ ]	knots;
	Point4f[ ]	controlPoints;
	BSpline4f	derivative;
	
	public BSpline4f( int degree , float[ ] knots , Point4f[ ] controlPoints )
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
	
	public BSpline4f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			Point4f[ ] derivControlPoints = VecmathUtils.allocPoint4fArray( controlPoints.length - 1 );
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ].sub( controlPoints[ i + 1 ] , controlPoints[ i ] );
				derivControlPoints[ i ].scale( factor );
			}
			
			derivative = new BSpline4f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
	
	public static interface PointType
	{
		public Point4f[ ] allocate( int size );
		
		public void set( Point4f result , Point4f value );
		
		public void scale( Point4f result , float f , Point4f a );
		
		public void add( Point4f result , Point4f a , Point4f b );
		
		public void scaleAdd( Point4f result , float f , Point4f a , Point4f b );
		
		public void combine( Point4f result , float af , Point4f a , float bf , Point4f b );
	}
	
	public static interface RationalPointType
	{
		public float getWeight( Point4f point );
	}
	
	public static class Evaluator
	{
		public Evaluator( int degree )
		{
			this.degree = degree;
			deBoorPoints = VecmathUtils.allocPoint4fArray( degree + 1 );
			
			index = -1;
		}
		
		int			degree;
		Point4f[ ]	deBoorPoints;
		
		float[ ]	knots;
		float		param;
		int			index;
		int			multiplicity;
		
		/**
		 * A modified binary search that's easier to use for B-Splines.
		 * 
		 * @return the index {@code i} in {@code [fromIndex, toIndex)} such that {@code key} is in {@code [a[i], a[i+1])}. If no such {@code i} exists, returns
		 *         {@code -1}.
		 */
		static int binarySearch( float[ ] a , int fromIndex , int toIndex , float key )
		{
			int low = fromIndex;
			int high = toIndex - 1;
			
			while( low + 1 < high )
			{
				int mid = ( low + high ) >>> 1;
				float midVal = a[ mid ];
				
				if( midVal <= key )
				{
					low = mid;
				}
				else
				{
					high = mid;
				}
			}
			return key >= a[ low ] && key < a[ high ] ? low : -1;
		}
		
		void updateState( float param , float[ ] knots )
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
				index = binarySearch( knots , 0 , knots.length , param );
				multiplicity = 0;
				for( int i = index ; i >= 0 && knots[ i ] == param ; i-- )
				{
					multiplicity++ ;
				}
			}
		}
		
		public void eval( BSpline4f s , float param , Point4f result )
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
		Point4f		A;
		Point4f		Ap;
		Point4f		App;
		Point4f		C;
		Point4f		Cp;
		Point4f		Cpp;
		
		public NurbsEvaluator( int degree )
		{
			evaluator = new Evaluator( degree );
			A = new Point4f( );
			Ap = new Point4f( );
			App = new Point4f( );
			C = new Point4f( );
			Cp = new Point4f( );
			Cpp = new Point4f( );
		}
		
		public void eval( BSpline4f s , float param , Point4f result )
		{
			evaluator.eval( s , param , result );
			result.scale( 1 / result.w );
		}
		
		public void evalDerivative( BSpline4f s , float param , Point4f result , Point4f derivResult )
		{
			evaluator.eval( s , param , A );
			float rw = A.w;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			derivResult.scaleAdd( -Ap.w , result , Ap );
			derivResult.scale( rw );
		}
		
		public void eval2ndDerivative( BSpline4f s , float param , Point4f result , Point4f derivResult , Point4f deriv2Result )
		{
			evaluator.eval( s , param , A );
			float rw = 1f / A.w;
			result.scale( rw , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = Ap.w;
			derivResult.scaleAdd( -wp , result , Ap );
			derivResult.scale( rw );
			
			evaluator.eval( s.getDerivative( ).getDerivative( ) , param , App );
			float wpp = App.w;
			
			deriv2Result.scale( -wpp , result );
			deriv2Result.scaleAdd( -2 * wp , derivResult , deriv2Result );
			deriv2Result.add( App );
			deriv2Result.scale( rw );
		}
	}
}
