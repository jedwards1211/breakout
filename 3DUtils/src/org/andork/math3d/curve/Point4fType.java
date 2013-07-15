package org.andork.math3d.curve;

import javax.vecmath.Point4f;

import org.andork.math3d.curve.BSplineGf.RationalPointType;
import org.andork.vecmath.VecmathUtils;

public class Point4fType implements RationalPointType<Point4f>
{
	@Override
	public Point4f[ ] allocate( int size )
	{
		return VecmathUtils.allocPoint4fArray( size );
	}
	
	@Override
	public void set( Point4f result , Point4f value )
	{
		result.set( value );
	}
	
	@Override
	public void scale( Point4f result , float f , Point4f a )
	{
		result.scale( f , a );
	}
	
	@Override
	public void add( Point4f result , Point4f a , Point4f b )
	{
		result.add( a , b );
	}
	
	@Override
	public void scaleAdd( Point4f result , float f , Point4f a , Point4f b )
	{
		result.scaleAdd( f , a , b );
	}
	
	@Override
	public void combine( Point4f result , float af , Point4f a , float bf , Point4f b )
	{
		result.x = af * a.x + bf * b.x;
		result.y = af * a.y + bf * b.y;
		result.z = af * a.z + bf * b.z;
		result.w = af * a.w + bf * b.w;
	}
	
	@Override
	public float getWeight( Point4f point )
	{
		return point.w;
	}
}
