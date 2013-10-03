package org.andork.math.curve;


public class BSpline1f
{
	int			degree;
	float[ ]	knots;
	float[ ]	controlPoints;
	BSpline1f	derivative;
	
	public BSpline1f( int degree , float[ ] knots , float[ ] controlPoints )
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
	
	public BSpline1f getDerivative( )
	{
		if( degree == 0 )
		{
			throw new IllegalArgumentException( "cannot compute derivative" );
		}
		if( derivative == null )
		{
			float[ ] derivKnots = new float[ knots.length - 2 ];
			System.arraycopy( knots , 1 , derivKnots , 0 , derivKnots.length );
			float[ ] derivControlPoints = new float[ controlPoints.length - 1 ];
			
			for( int i = 0 ; i < derivControlPoints.length ; i++ )
			{
				float factor = degree / ( knots[ i + degree + 1 ] - knots[ i + 1 ] );
				derivControlPoints[ i ] = ( controlPoints[ i + 1 ] - controlPoints[ i ] ) * factor;
			}
			
			derivative = new BSpline1f( degree - 1 , derivKnots , derivControlPoints );
		}
		return derivative;
	}
	
	public static class Evaluator extends BSplineEvaluatorFloat
	{
		float[ ]	deBoorPoints;
		
		public Evaluator( int degree )
		{
			super( degree );
			deBoorPoints = new float[ degree + 1 ];
		}
		
		public float eval( BSpline1f s , float param )
		{
			updateState( param , s.knots );
			
			for( int i = 0 ; i <= s.degree - multiplicity ; i++ )
			{
				deBoorPoints[ i ] = s.controlPoints[ index - multiplicity - i ];
			}
			
			for( int r = 0 ; r < s.degree - multiplicity ; r++ )
			{
				for( int i = 0 ; i < s.degree - multiplicity - r ; i++ )
				{
					int ii = index - multiplicity - i;
					float a = ( param - knots[ ii ] ) / ( knots[ ii + s.degree - r ] - knots[ ii ] );
					deBoorPoints[ i ] = a * deBoorPoints[ i ] + ( 1 - a ) * deBoorPoints[ i + 1 ];
				}
			}
			
			return deBoorPoints[ 0 ];
		}
	}
}
