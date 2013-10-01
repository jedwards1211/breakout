package org.andork.math.curve;

import javax.vecmath.Point2f;

import org.andork.math.curve.BSplineGf.RationalPointType;
import org.andork.vecmath.VecmathUtils;

public class Point2fType implements RationalPointType<Point2f>
{
	@Override
	public Point2f[ ] allocate( int size )
	{
		return VecmathUtils.allocPoint2fArray( size );
	}
	
	@Override
	public void set( Point2f result , Point2f value )
	{
		result.set( value );
	}
	
	@Override
	public void scale( Point2f result , float f , Point2f a )
	{
		result.scale( f , a );
	}
	
	@Override
	public void add( Point2f result , Point2f a , Point2f b )
	{
		result.add( a , b );
	}
	
	@Override
	public void scaleAdd( Point2f result , float f , Point2f a , Point2f b )
	{
		result.scaleAdd( f , a , b );
	}
	
	@Override
	public void combine( Point2f result , float af , Point2f a , float bf , Point2f b )
	{
		result.x = af * a.x + bf * b.x;
		result.y = af * a.y + bf * b.y;
	}
	
	@Override
	public float getWeight( Point2f point )
	{
		return point.y;
	}
}
