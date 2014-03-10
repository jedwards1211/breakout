package org.andork.frf.model;

public class FloatRange
{
	private float	lo , hi;
	
	private FloatRange( )
	{
		
	}
	
	public FloatRange( float lo , float hi )
	{
		super( );
		this.lo = lo;
		this.hi = hi;
	}
	
	public float getLo( )
	{
		return lo;
	}
	
	public float getHi( )
	{
		return hi;
	}
}
