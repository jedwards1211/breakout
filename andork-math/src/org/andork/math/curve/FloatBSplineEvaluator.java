package org.andork.math.curve;

public class FloatBSplineEvaluator
{
	protected int		degree		= 0;
	protected int		dimension	= 0;
	protected float[ ]	deboorPoints;
	
	public FloatBSplineEvaluator degree( int degree )
	{
		this.degree = degree;
		updateDeBoorPoints( );
		return this;
	}
	
	private void updateDeBoorPoints( )
	{
		if( deboorPoints == null || deboorPoints.length < ( degree + 1 ) * dimension )
		{
			deboorPoints = new float[ ( degree + 1 ) * dimension ];
		}
	}
	
	public FloatBSplineEvaluator dimension( int dimension )
	{
		this.dimension = dimension;
		updateDeBoorPoints( );
		return this;
	}
	
	/**
	 * Evaluates a b-spline curve with the given qualities. DOES NOT DO ANY ERROR CHECKING. It's up to you to ensure you pass correct values.
	 * 
	 * @param degree
	 *            the degree of the curve
	 * @param dimension
	 *            the number of coordinates per point
	 * @param knots
	 *            the knots, sorted in ascending order
	 * @param knotIndex
	 *            the index of the knot in {@code knots} such that {@code knots[index] <= param} and {@code param < knots[index + 1}
	 * @param multiplicity
	 *            the multiplicity of {@code knots[index]} (number of adjacent equal knots)
	 * @param points
	 *            the control points
	 * @param pointsStride
	 *            the offset between control points. Can be different from {@code degree}.
	 * @param param
	 *            the spline function parameter.
	 * @param out
	 *            the array to store the evaluated point in.
	 * @param outIndex
	 *            the index in {@code out} to store the first coordinate of the evaluated point at.
	 */
	public void eval( int degree , int dimension , float[ ] knots , int knotIndex , int multiplicity , float[ ] points , int pointsStride , float param , float[ ] out , int outIndex )
	{
		if( param == knots[ 0 ] )
		{
			System.arraycopy( points , 0 , out , outIndex , dimension );
			return;
		}
		else if( param == knots[ knots.length - 1 ] )
		{
			System.arraycopy( points , points.length - pointsStride , out , outIndex , dimension );
			return;
		}
		
		int r = degree - multiplicity;
		
		for( int i = 0 ; i <= r ; i++ )
		{
			System.arraycopy( points , ( knotIndex - degree + i ) * pointsStride , deboorPoints , i * dimension , dimension );
		}
		
		for( int j = 1 ; j <= r ; j++ )
		{
			for( int i = 0 ; i <= r - j ; i++ )
			{
				float a = ( param - knots[ knotIndex - degree + j + i ] ) / ( knots[ i + knotIndex + 1 ] - knots[ knotIndex - degree + j + i ] );
				int k0 = i * dimension;
				int k1 = k0 + dimension;
				for( int k = k0 ; k < k1 ; k++ )
				{
					deboorPoints[ k ] = a * deboorPoints[ k + dimension ] + ( 1f - a ) * deboorPoints[ k ];
				}
			}
		}
		
		System.arraycopy( deboorPoints , 0 , out , outIndex , dimension );
	}
}
