package org.andork.func;

public class RoundingFloat2IntegerBimapper implements Bimapper<Float, Integer>
{
	public static final RoundingFloat2IntegerBimapper	instance	= new RoundingFloat2IntegerBimapper( );
	
	private RoundingFloat2IntegerBimapper( )
	{
		
	}
	
	@Override
	public Integer map( Float in )
	{
		return Math.round( in );
	}
	
	@Override
	public Float unmap( Integer out )
	{
		return out.floatValue( );
	}
}
