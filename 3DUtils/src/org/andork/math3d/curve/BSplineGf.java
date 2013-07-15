package org.andork.math3d.curve;

public class BSplineGf<T>
{
	int				degree;
	float[ ]		knots;
	T[ ]			controlPoints;
	PointType<T>	pointType;
	BSplineGf<T>	derivative;
	
	public BSplineGf( int degree , float[ ] knots , T[ ] controlPoints , PointType<T> pointType )
	{
		if( knots.length != controlPoints.length + degree + 1 )
		{
			throw new IllegalArgumentException( "knots.length (" + knots.length + ") does not equal controlPoints.length (" + controlPoints.length + ") + degree (" + degree + ") + 1" );
		}
		this.degree = degree;
		this.knots = knots;
		this.controlPoints = controlPoints;
		this.pointType = pointType;
	}
	
	public int getDegree( )
	{
		return degree;
	}
	
	public BSplineGf<T> getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			T[ ] derivControlPoints = pointType.allocate( controlPoints.length - 1 );
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				pointType.combine( derivControlPoints[ i ] , -factor , controlPoints[ i ] , factor , controlPoints[ i + 1 ] );
			}
			
			derivative = new BSplineGf<T>( degree - 1 , derivKnots , derivControlPoints , pointType );
		}
		return derivative;
	}
	
	public static interface PointType<T>
	{
		public T[ ] allocate( int size );
		
		public void set( T result , T value );
		
		public void scale( T result , float f , T a );
		
		public void add( T result , T a , T b );
		
		public void scaleAdd( T result , float f , T a , T b );
		
		public void combine( T result , float af , T a , float bf , T b );
	}
	
	public static interface RationalPointType<T> extends PointType<T>
	{
		public float getWeight( T point );
	}
	
	public static class Evaluator<T>
	{
		public Evaluator( int degree , PointType<T> pointType )
		{
			this.degree = degree;
			deBoorPoints = pointType.allocate( degree + 1 );
			
			index = -1;
		}
		
		int			degree;
		T[ ]		deBoorPoints;
		
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
		
		public void eval( BSplineGf<T> s , float param , T result )
		{
			updateState( param , s.knots );
			
			for( int i = 0 ; i <= s.degree - multiplicity ; i++ )
			{
				s.pointType.set( deBoorPoints[ i ] , s.controlPoints[ index - multiplicity - i ] );
			}
			
			for( int r = 0 ; r < s.degree - multiplicity ; r++ )
			{
				for( int i = 0 ; i < s.degree - multiplicity - r ; i++ )
				{
					int ii = index - multiplicity - i;
					float a = ( param - knots[ ii ] ) / ( knots[ ii + s.degree - r ] - knots[ ii ] );
					s.pointType.combine( deBoorPoints[ i ] , a , deBoorPoints[ i ] , 1 - a , deBoorPoints[ i + 1 ] );
				}
			}
			
			s.pointType.set( result , deBoorPoints[ 0 ] );
		}
	}
	
	public static class NurbsEvaluator<T>
	{
		Evaluator<T>			evaluator;
		RationalPointType<T>	pointType;
		
		T						A;
		T						Ap;
		T						App;
		T						C;
		T						Cp;
		T						Cpp;
		
		public NurbsEvaluator( int degree , RationalPointType<T> pointType )
		{
			evaluator = new Evaluator<>( degree , pointType );
			this.pointType = pointType;
			A = pointType.allocate( 1 )[ 0 ];
			Ap = pointType.allocate( 1 )[ 0 ];
			App = pointType.allocate( 1 )[ 0 ];
			C = pointType.allocate( 1 )[ 0 ];
			Cp = pointType.allocate( 1 )[ 0 ];
			Cpp = pointType.allocate( 1 )[ 0 ];
		}
		
		public void eval( BSplineGf<T> s , float param , T result )
		{
			evaluator.eval( s , param , A );
			pointType.scale( result , 1f / pointType.getWeight( A ) , A );
		}
		
		public void evalDerivative( BSplineGf<T> s , float param , T result , T derivResult )
		{
			evaluator.eval( s , param , A );
			float w = pointType.getWeight( A );
			pointType.scale( C , 1f / w , A );
			
			if( result != null )
			{
				pointType.set( result , C );
			}
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = pointType.getWeight( Ap );
			pointType.combine( derivResult , -wp / w , C , 1f / w , Ap );
		}
		
		public void eval2ndDerivative( BSplineGf<T> s , float param , T result , T derivResult , T deriv2Result )
		{
			evaluator.eval( s , param , A );
			float w = pointType.getWeight( A );
			pointType.scale( C , 1f / w , A );
			
			if( result != null )
			{
				pointType.set( result , C );
			}
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = pointType.getWeight( Ap );
			pointType.combine( Cp , -wp / w , C , 1f / w , Ap );
			
			if( derivResult != null )
			{
				pointType.set( derivResult , Cp );
			}
			
			evaluator.eval( s.getDerivative( ).getDerivative( ) , param , App );
			float wpp = pointType.getWeight( App );
			
			pointType.combine( deriv2Result , -2 * wp , Cp , -wpp , C );
			pointType.add( deriv2Result , App , deriv2Result );
			pointType.scale( deriv2Result , 1 / w , deriv2Result );
		}
	}
}
