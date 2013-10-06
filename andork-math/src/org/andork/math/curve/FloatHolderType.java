package org.andork.math.curve;

import org.andork.math.curve.BSplineGf.PointType;
import org.omg.CORBA.FloatHolder;

public class FloatHolderType implements PointType<FloatHolder>
{
	@Override
	public FloatHolder[ ] allocate( int size )
	{
		FloatHolder[ ] f = new FloatHolder[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			f[ i ] = new FloatHolder( );
		}
		return f;
	}
	
	@Override
	public void set( FloatHolder result , FloatHolder value )
	{
		result.value = value.value;
	}
	
	@Override
	public void scale( FloatHolder result , float f , FloatHolder a )
	{
		result.value = f * a.value;
	}
	
	@Override
	public void add( FloatHolder result , FloatHolder a , FloatHolder b )
	{
		result.value = a.value + b.value;
	}
	
	@Override
	public void scaleAdd( FloatHolder result , float f , FloatHolder a , FloatHolder b )
	{
		result.value = f * a.value + b.value;
	}
	
	@Override
	public void combine( FloatHolder result , float af , FloatHolder a , float bf , FloatHolder b )
	{
		result.value = af * a.value + bf * b.value;
	}
	
}
