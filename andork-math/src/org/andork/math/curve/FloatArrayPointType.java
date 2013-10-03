package org.andork.math.curve;

import org.andork.math.curve.BSplineGf.RationalPointType;

public class FloatArrayPointType implements RationalPointType<float[ ]>
{
	int	dimension;
	
	public FloatArrayPointType( int dimension )
	{
		this.dimension = dimension;
	}
	
	@Override
	public float[ ][ ] allocate( int size )
	{
		return new float[ size ][ dimension ];
	}
	
	@Override
	public void set( float[ ] result , float[ ] value )
	{
		System.arraycopy( value , 0 , result , 0 , dimension );
	}
	
	@Override
	public void scale( float[ ] result , float f , float[ ] a )
	{
		for( int i = 0 ; i < dimension ; i++ )
		{
			result[ i ] = f * a[ i ];
		}
	}
	
	@Override
	public void add( float[ ] result , float[ ] a , float[ ] b )
	{
		for( int i = 0 ; i < dimension ; i++ )
		{
			result[ i ] = a[ i ] + b[ i ];
		}
	}
	
	@Override
	public void scaleAdd( float[ ] result , float f , float[ ] a , float[ ] b )
	{
		for( int i = 0 ; i < dimension ; i++ )
		{
			result[ i ] = f * a[ i ] + b[ i ];
		}
	}
	
	@Override
	public void combine( float[ ] result , float af , float[ ] a , float bf , float[ ] b )
	{
		for( int i = 0 ; i < dimension ; i++ )
		{
			result[ i ] = af * a[ i ] + bf * b[ i ];
		}
	}
	
	@Override
	public float getWeight( float[ ] point )
	{
		return point[ dimension - 1 ];
	}
}
