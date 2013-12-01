package org.andork.vecmath;

public class PerspTest
{
	public static void main( String[ ] args )
	{
		float l = -2;
		float r = 1;
		float t = 4;
		float b = -3;
		float n = -8;
		float f = -50;
		
		float E = 2 * n / ( r - l );
		float A = ( r + l ) / ( r - l );
		float F = 2 * n / ( t - b );
		float B = ( t + b ) / ( t - b );
		float C = ( f + n ) / ( n - f );
		float D = 2 * f * n / ( n - f );
		
		float nn = -D / ( 1 - C );
		float ff = D * nn / ( D + 2 * nn );
		float ll = nn * ( A - 1 ) / E;
		float rr = 2 * nn * A / E - ll;
		float bb = nn * ( B - 1 ) / F;
		float tt = 2 * nn * B / F - bb;
		
		System.out.println(ll);
		System.out.println(rr);
		System.out.println(tt);
		System.out.println(bb);
		System.out.println(nn);
		System.out.println(ff);
	}
}
