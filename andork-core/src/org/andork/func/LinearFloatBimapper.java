package org.andork.func;

public class LinearFloatBimapper implements Bimapper<Float, Float>
{
	// out = m * in + b;
	public float	m;
	public float	b;
	
	public LinearFloatBimapper( )
	{
		m = 1f;
		b = 0f;
	}
	
	public LinearFloatBimapper( float m , float b )
	{
		this.m = m;
		this.b = b;
	}
	
	public LinearFloatBimapper( float in0 , float out0 , float in1 , float out1 )
	{
		set( in0 , out0 , in1 , out1 );
	}
	
	public LinearFloatBimapper set( float in0 , float out0 , float in1 , float out1 )
	{
		m = ( out1 - out0 ) / ( in1 - in0 );
		b = in0 - out0 / m;
		return this;
	}
	
	@Override
	public Float map( Float in )
	{
		return in == null ? null : m * in + b;
	}
	
	@Override
	public Float unmap( Float out )
	{
		return out == null ? null : ( out - b ) / m;
	}
}
