package org.andork.math.misc;

public class AngleUtils
{
	public static double oppositeAngle( double angle )
	{
		angle = ( angle + Math.PI ) % ( Math.PI * 2 );
		return angle < 0 ? angle + Math.PI * 2 : angle;
	}
	
	public static double clockwiseRotation( double a , double b )
	{
		double diff = ( b - a ) % ( Math.PI * 2.0 );
		return diff < 0.0 ? diff + Math.PI * 2.0 : diff;
	}
	
	public static double counterclockwiseRotation( double a , double b )
	{
		return Math.PI * 2.0 - clockwiseRotation( a , b );
	}
	
	public static double clockwiseBisect( double a , double b )
	{
		return a + 0.5 * clockwiseRotation( a , b );
	}
	
	public static double rotation( double a , double b )
	{
		double result = ( b - a ) % ( Math.PI * 2.0 );
		if( result < -Math.PI )
		{
			result += Math.PI * 2.0;
		}
		if( result > Math.PI )
		{
			result -= Math.PI * 2.0;
		}
		return result;
	}
}
