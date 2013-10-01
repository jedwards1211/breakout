package org.andork.util;

public class Reparam
{
	/**
	 * Performs linear reparameterization.
	 * 
	 * @param x
	 *            the value to reparameterize
	 * @param a1
	 *            the start of the first range
	 * @param a2
	 *            the end of the first range
	 * @param b1
	 *            the start of the second range
	 * @param b2
	 *            the end of the second range
	 */
	public static float linear( float x , float a1 , float a2 , float b1 , float b2 )
	{
		return b1 + ( x - a1 ) * ( b2 - b1 ) / ( a2 - a1 );
	}
}
