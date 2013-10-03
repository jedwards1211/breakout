package org.andork.math.curve;

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
	
	public static class Evaluator<T> extends BSplineEvaluatorFloat
	{
		public Evaluator( int degree , PointType<T> pointType )
		{
			super( degree );
			deBoorPoints = pointType.allocate( degree + 1 );
		}
		
		T[ ]	deBoorPoints;
		
		public void eval( BSplineGf<T> s , float param , T result )
		{
			updateState( param , s.knots );
			
			if( param == s.knots[ 0 ] )
			{
				s.pointType.set( result , s.controlPoints[ 0 ] );
				return;
			}
			else if( param == s.knots[ s.knots.length - 1 ] )
			{
				s.pointType.set( result , s.controlPoints[ s.controlPoints.length - 1 ] );
				return;
			}
			
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
		
		public NurbsEvaluator( int degree , RationalPointType<T> pointType )
		{
			evaluator = new Evaluator<T>( degree , pointType );
			this.pointType = pointType;
			A = pointType.allocate( 1 )[ 0 ];
			Ap = pointType.allocate( 1 )[ 0 ];
			App = pointType.allocate( 1 )[ 0 ];
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
			pointType.scale( result , 1f / w , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = pointType.getWeight( Ap );
			pointType.combine( derivResult , -wp / w , result , 1f / w , Ap );
		}
		
		public void eval2ndDerivative( BSplineGf<T> s , float param , T result , T derivResult , T deriv2Result )
		{
			evaluator.eval( s , param , A );
			float w = pointType.getWeight( A );
			pointType.scale( result , 1f / w , A );
			
			evaluator.eval( s.getDerivative( ) , param , Ap );
			float wp = pointType.getWeight( Ap );
			pointType.combine( derivResult , -wp / w , result , 1f / w , Ap );
			
			evaluator.eval( s.getDerivative( ).getDerivative( ) , param , App );
			float wpp = pointType.getWeight( App );
			
			pointType.combine( deriv2Result , -2 * wp , derivResult , -wpp , result );
			pointType.add( deriv2Result , App , deriv2Result );
			pointType.scale( deriv2Result , 1 / w , deriv2Result );
		}
	}
}
