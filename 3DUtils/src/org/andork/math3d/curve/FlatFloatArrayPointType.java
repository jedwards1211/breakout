package org.andork.math3d.curve;

import java.util.Arrays;

import org.andork.math3d.curve.BSplineGf.RationalPointType;

public class FlatFloatArrayPointType implements RationalPointType<Integer>
{
	private float[ ]	array;
	private int			dimension;
	
	private float[ ]	temps	= new float[ 0 ];
	
	public FlatFloatArrayPointType( float[ ] array , int dimension )
	{
		super( );
		this.array = array;
		this.dimension = dimension;
	}
	
	@Override
	public Integer[ ] allocate( int size )
	{
		Integer[ ] result = new Integer[ size ];
		int k = -temps.length * dimension - 1;
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = k;
			k -= dimension;
		}
		temps = Arrays.copyOf( temps , temps.length + size * dimension );
		return result;
	}
	
	private float[ ] getArray( int index )
	{
		return index < 0 ? array : temps;
	}
	
	private int fixIndex( int index )
	{
		if( index < 0 )
		{
			index = -( index + 1 );
		}
		return index;
	}
	
	@Override
	public void set( Integer result , Integer value )
	{
		float[ ] rarray = getArray( result );
		float[ ] varray = getArray( value );
		result = fixIndex( result );
		value = fixIndex( value );
		
		int end = result + dimension;
		while( result < end )
		{
			rarray[ result++ ] = varray[ value++ ];
		}
	}
	
	@Override
	public void scale( Integer result , float f , Integer a )
	{
		float[ ] rarray = getArray( result );
		float[ ] varray = getArray( a );
		result = fixIndex( result );
		a = fixIndex( a );
		
		int end = result + dimension;
		while( result < end )
		{
			rarray[ result++ ] = f * varray[ a++ ];
		}
	}
	
	@Override
	public void add( Integer result , Integer a , Integer b )
	{
		float[ ] rarray = getArray( result );
		float[ ] aarray = getArray( a );
		float[ ] barray = getArray( b );
		result = fixIndex( result );
		a = fixIndex( a );
		b = fixIndex( b );
		
		int end = result + dimension;
		while( result < end )
		{
			rarray[ result++ ] = aarray[ a++ ] + barray[ b++ ];
		}
	}
	
	@Override
	public void scaleAdd( Integer result , float f , Integer a , Integer b )
	{
		float[ ] rarray = getArray( result );
		float[ ] aarray = getArray( a );
		float[ ] barray = getArray( b );
		result = fixIndex( result );
		a = fixIndex( a );
		b = fixIndex( b );
		
		int end = result + dimension;
		while( result < end )
		{
			rarray[ result++ ] = f * aarray[ a++ ] + barray[ b++ ];
		}
	}
	
	@Override
	public void combine( Integer result , float af , Integer a , float bf , Integer b )
	{
		float[ ] rarray = getArray( result );
		float[ ] aarray = getArray( a );
		float[ ] barray = getArray( b );
		result = fixIndex( result );
		a = fixIndex( a );
		b = fixIndex( b );
		
		int end = result + dimension;
		while( result < end )
		{
			rarray[ result++ ] = af * aarray[ a++ ] + bf * barray[ b++ ];
		}
	}
	
	@Override
	public float getWeight( Integer point )
	{
		float[ ] parray = getArray( point );
		point = fixIndex( point );
		
		return parray[ point + dimension - 1 ];
	}
}
