package org.andork.math.curve;

import javax.vecmath.Point3f;

import org.andork.math.curve.BSplineGf.RationalPointType;
import org.andork.vecmath.VecmathUtils;

public class Point3fType implements RationalPointType<Point3f>
{
	@Override
	public Point3f[ ] allocate( int size )
	{
		return VecmathUtils.allocPoint3fArray( size );
	}
	
	@Override
	public void set( Point3f result , Point3f value )
	{
		result.set( value );
	}
	
	@Override
	public void scale( Point3f result , float f , Point3f a )
	{
		result.scale( f , a );
	}
	
	@Override
	public void add( Point3f result , Point3f a , Point3f b )
	{
		result.add( a , b );
	}
	
	@Override
	public void scaleAdd( Point3f result , float f , Point3f a , Point3f b )
	{
		result.scaleAdd( f , a , b );
	}
	
	@Override
	public void combine( Point3f result , float af , Point3f a , float bf , Point3f b )
	{
		result.x = af * a.x + bf * b.x;
		result.y = af * a.y + bf * b.y;
		result.z = af * a.z + bf * b.z;
	}
	
	@Override
	public float getWeight( Point3f point )
	{
		return point.z;
	}
}
