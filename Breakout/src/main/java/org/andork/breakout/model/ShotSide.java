package org.andork.breakout.model;

public enum ShotSide
{
	AT_FROM , AT_TO;
	
	public ShotSide opposite( )
	{
		return this == AT_FROM ? AT_TO : AT_FROM;
	}
}
