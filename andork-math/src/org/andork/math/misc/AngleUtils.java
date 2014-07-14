package org.andork.math.misc;

public class AngleUtils
{
	public static double oppositeAngle( double angle )
	{
		angle = ( angle + Math.PI ) % ( Math.PI * 2 );
		return angle < 0 ? angle + Math.PI * 2 : angle;
	}
}
