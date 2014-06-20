package org.andork.breakout.model;

public enum SurveyShotSide
{
	AT_FROM , AT_TO;
	
	public SurveyShotSide opposite( )
	{
		return this == AT_FROM ? AT_TO : AT_FROM;
	}
}
