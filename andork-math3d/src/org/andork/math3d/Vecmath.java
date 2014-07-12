package org.andork.math3d;

import java.util.Arrays;

public class Vecmath
{
	private static final double	EPSILON_ABSOLUTE	= 1.0e-5f;
	private static final double	FEPS				= 1.110223024E-8f;
	
	public static double distance3( double[ ] a , double[ ] b )
	{
		double dx = a[ 0 ] - b[ 0 ];
		double dy = a[ 1 ] - b[ 1 ];
		double dz = a[ 2 ] - b[ 2 ];
		
		return ( double ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static double distance3( double[ ] a , float[ ] b )
	{
		double dx = a[ 0 ] - b[ 0 ];
		double dy = a[ 1 ] - b[ 1 ];
		double dz = a[ 2 ] - b[ 2 ];
		
		return ( double ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static double distance3( double[ ] a , int ai , double[ ] b , int bi )
	{
		double dx = a[ ai ] - b[ bi ];
		double dy = a[ ai + 1 ] - b[ bi + 1 ];
		double dz = a[ ai + 2 ] - b[ bi + 2 ];
		
		return ( double ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	/**
	 * Computes (a - b) dot c.
	 */
	public static double subDot3( double[ ] a , double[ ] b , double[ ] c )
	{
		return ( a[ 0 ] - b[ 0 ] ) * c[ 0 ] + ( a[ 1 ] - b[ 1 ] ) * c[ 1 ] + ( a[ 2 ] - b[ 2 ] ) * c[ 2 ];
	}
	
	/**
	 * Computes (a - b) dot c.
	 */
	public static double subDot3( double[ ] a , float[ ] b , double[ ] c )
	{
		return ( a[ 0 ] - b[ 0 ] ) * c[ 0 ] + ( a[ 1 ] - b[ 1 ] ) * c[ 1 ] + ( a[ 2 ] - b[ 2 ] ) * c[ 2 ];
	}
	
	public static double dot3( double[ ] a , double[ ] b )
	{
		return a[ 0 ] * b[ 0 ] + a[ 1 ] * b[ 1 ] + a[ 2 ] * b[ 2 ];
	}
	
	public static double dot3( double[ ] a , int ai , double[ ] b , int bi )
	{
		return a[ ai + 0 ] * b[ bi + 0 ] + a[ ai + 1 ] * b[ bi + 1 ] + a[ ai + 2 ] * b[ bi + 2 ];
	}
	
	public static void cross( double[ ] a , double[ ] b , double[ ] out )
	{
		if( out != a && out != b )
		{
			out[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			out[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
		}
		else
		{
			double x = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			double y = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
			out[ 1 ] = y;
			out[ 0 ] = x;
		}
	}
	
	public static void cross( double[ ] out , double[ ] a , double x , double y , double z )
	{
		if( out != a )
		{
			out[ 0 ] = a[ 1 ] * z - a[ 2 ] * y;
			out[ 1 ] = a[ 2 ] * x - a[ 0 ] * z;
			out[ 2 ] = a[ 0 ] * y - a[ 1 ] * x;
		}
		else
		{
			double cx = a[ 1 ] * z - a[ 2 ] * y;
			double cy = a[ 2 ] * x - a[ 0 ] * z;
			out[ 2 ] = a[ 0 ] * y - a[ 1 ] * x;
			out[ 1 ] = cy;
			out[ 0 ] = cx;
		}
	}
	
	public static void cross( double x , double y , double z , double[ ] b , double[ ] out )
	{
		if( out != b )
		{
			out[ 0 ] = y * b[ 2 ] - z * b[ 1 ];
			out[ 1 ] = z * b[ 0 ] - x * b[ 2 ];
			out[ 2 ] = x * b[ 1 ] - y * b[ 0 ];
		}
		else
		{
			double cx = y * b[ 2 ] - z * b[ 1 ];
			double cy = z * b[ 0 ] - x * b[ 2 ];
			out[ 2 ] = x * b[ 1 ] - y * b[ 0 ];
			out[ 1 ] = cy;
			out[ 0 ] = cx;
		}
	}
	
	public static void cross(
			double ax , double ay , double az ,
			double bx , double by , double bz ,
			double[ ] out )
	{
		double cx = ay * bz - az * by;
		double cy = az * bx - ax * bz;
		out[ 2 ] = ax * by - ay * bx;
		out[ 1 ] = cy;
		out[ 0 ] = cx;
	}
	
	public static void cross( double[ ] a , int ai , double[ ] b , int bi , double[ ] out , int outi )
	{
		if( out != a && out != b )
		{
			out[ outi + 0 ] = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			out[ outi + 1 ] = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
		}
		else
		{
			double x = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			double y = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
			out[ outi + 1 ] = y;
			out[ outi + 0 ] = x;
		}
	}
	
	public static double[ ] newMat4d( )
	{
		return new double[ ] { 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 };
	}
	
	public static void mpmul( double[ ] m , double[ ] p )
	{
		double rw = 1 / ( m[ 3 ] * p[ 0 ] + m[ 7 ] * p[ 1 ] + m[ 11 ] * p[ 2 ] + m[ 15 ] );
		double x = rw * ( m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ] );
		double y = rw * ( m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ] );
		p[ 2 ] = rw * ( m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ] );
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmul( double[ ] m , double[ ] p , double[ ] out )
	{
		if( p != out )
		{
			double rw = 1 / ( m[ 3 ] * p[ 0 ] + m[ 7 ] * p[ 1 ] + m[ 11 ] * p[ 2 ] + m[ 15 ] );
			out[ 0 ] = rw * ( m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ] );
			out[ 1 ] = rw * ( m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ] );
			out[ 2 ] = rw * ( m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ] );
		}
		else
		{
			mpmul( m , p );
		}
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p )
	{
		double x = m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ];
		double y = m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ];
		p[ 2 ] = m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ];
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , int pi )
	{
		double x = m[ 0 ] * p[ pi ] + m[ 4 ] * p[ pi + 1 ] + m[ 8 ] * p[ pi + 2 ] + m[ 12 ];
		double y = m[ 1 ] * p[ pi ] + m[ 5 ] * p[ pi + 1 ] + m[ 9 ] * p[ pi + 2 ] + m[ 13 ];
		p[ pi + 2 ] = m[ 2 ] * p[ pi ] + m[ 6 ] * p[ pi + 1 ] + m[ 10 ] * p[ pi + 2 ] + m[ 14 ];
		p[ pi + 1 ] = y;
		p[ pi ] = x;
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , double[ ] out )
	{
		if( p != out )
		{
			out[ 0 ] = m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ];
			out[ 1 ] = m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ];
			out[ 2 ] = m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ];
		}
		else
		{
			mpmulAffine( m , p );
		}
	}
	
	public static void mpmulAffine( double[ ] m , double x , double y , double z , double[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z + m[ 12 ];
		out[ 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z + m[ 13 ];
		out[ 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z + m[ 14 ];
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , int vi , double[ ] out , int outi )
	{
		if( p != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * p[ vi ] + m[ 4 ] * p[ vi + 1 ] + m[ 8 ] * p[ vi + 2 ] + m[ 12 ];
			out[ outi + 1 ] = m[ 1 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 9 ] * p[ vi + 2 ] + m[ 13 ];
			out[ outi + 2 ] = m[ 2 ] * p[ vi ] + m[ 6 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 14 ];
		}
		else
		{
			mpmulAffine( m , p , vi );
		}
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v )
	{
		double x = m[ 0 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
		double y = m[ 1 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 9 ] * v[ 2 ];
		v[ 2 ] = m[ 2 ] * v[ 0 ] + m[ 6 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		v[ 1 ] = y;
		v[ 0 ] = x;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , int vi )
	{
		double x = m[ 0 ] * v[ vi ] + m[ 4 ] * v[ vi + 1 ] + m[ 8 ] * v[ vi + 2 ];
		double y = m[ 1 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 9 ] * v[ vi + 2 ];
		v[ vi + 2 ] = m[ 2 ] * v[ vi ] + m[ 6 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		v[ vi + 1 ] = y;
		v[ vi ] = x;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , double[ ] out )
	{
		if( v != out )
		{
			out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
			out[ 1 ] = m[ 1 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 9 ] * v[ 2 ];
			out[ 2 ] = m[ 2 ] * v[ 0 ] + m[ 6 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		}
		else
		{
			mvmulAffine( m , v );
		}
	}
	
	public static void mvmulAffine( double[ ] m , double x , double y , double z , double[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z;
		out[ 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z;
		out[ 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( float[ ] m , double x , double y , double z , double[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z;
		out[ 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z;
		out[ 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( double[ ] m , double x , double y , double z , double[ ] out , int outi )
	{
		out[ outi ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z;
		out[ outi + 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z;
		out[ outi + 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , int vi , double[ ] out , int outi )
	{
		if( v != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * v[ vi ] + m[ 4 ] * v[ vi + 1 ] + m[ 8 ] * v[ vi + 2 ];
			out[ outi + 1 ] = m[ 1 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 9 ] * v[ vi + 2 ];
			out[ outi + 2 ] = m[ 2 ] * v[ vi ] + m[ 6 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		}
		else
		{
			mvmulAffine( m , v , vi );
		}
	}
	
	public static void mmul( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ] + ma[ 12 ] * mb[ 3 ];
			double m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ] + ma[ 12 ] * mb[ 7 ];
			double m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ] + ma[ 12 ] * mb[ 11 ];
			double m03 = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ] * mb[ 15 ];
			
			double m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ] + ma[ 13 ] * mb[ 3 ];
			double m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ] + ma[ 13 ] * mb[ 7 ];
			double m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ] + ma[ 13 ] * mb[ 11 ];
			double m13 = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ] * mb[ 15 ];
			
			double m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ] + ma[ 14 ] * mb[ 3 ];
			double m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ] + ma[ 14 ] * mb[ 7 ];
			double m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ] + ma[ 14 ] * mb[ 11 ];
			double m23 = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ] * mb[ 15 ];
			
			double m30 = ma[ 3 ] * mb[ 0 ] + ma[ 7 ] * mb[ 1 ] + ma[ 11 ] * mb[ 2 ] + ma[ 15 ] * mb[ 3 ];
			double m31 = ma[ 3 ] * mb[ 4 ] + ma[ 7 ] * mb[ 5 ] + ma[ 11 ] * mb[ 6 ] + ma[ 15 ] * mb[ 7 ];
			double m32 = ma[ 3 ] * mb[ 8 ] + ma[ 7 ] * mb[ 9 ] + ma[ 11 ] * mb[ 10 ] + ma[ 15 ] * mb[ 11 ];
			double m33 = ma[ 3 ] * mb[ 12 ] + ma[ 7 ] * mb[ 13 ] + ma[ 11 ] * mb[ 14 ] + ma[ 15 ] * mb[ 15 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 12 ] = m03;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 13 ] = m13;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
			out[ 14 ] = m23;
			out[ 3 ] = m30;
			out[ 7 ] = m31;
			out[ 11 ] = m32;
			out[ 15 ] = m33;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ] + ma[ 12 ] * mb[ 3 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ] + ma[ 12 ] * mb[ 7 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ] + ma[ 12 ] * mb[ 11 ];
			out[ 12 ] = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ] * mb[ 15 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ] + ma[ 13 ] * mb[ 3 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ] + ma[ 13 ] * mb[ 7 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ] + ma[ 13 ] * mb[ 11 ];
			out[ 13 ] = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ] * mb[ 15 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ] + ma[ 14 ] * mb[ 3 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ] + ma[ 14 ] * mb[ 7 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ] + ma[ 14 ] * mb[ 11 ];
			out[ 14 ] = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ] * mb[ 15 ];
			
			out[ 3 ] = ma[ 3 ] * mb[ 0 ] + ma[ 7 ] * mb[ 1 ] + ma[ 11 ] * mb[ 2 ] + ma[ 15 ] * mb[ 3 ];
			out[ 7 ] = ma[ 3 ] * mb[ 4 ] + ma[ 7 ] * mb[ 5 ] + ma[ 11 ] * mb[ 6 ] + ma[ 15 ] * mb[ 7 ];
			out[ 11 ] = ma[ 3 ] * mb[ 8 ] + ma[ 7 ] * mb[ 9 ] + ma[ 11 ] * mb[ 10 ] + ma[ 15 ] * mb[ 11 ];
			out[ 15 ] = ma[ 3 ] * mb[ 12 ] + ma[ 7 ] * mb[ 13 ] + ma[ 11 ] * mb[ 14 ] + ma[ 15 ] * mb[ 15 ];
		}
	}
	
	public static void mmulAffine( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			double m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			double m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			double m03 = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ];
			
			double m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			double m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			double m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			double m13 = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ];
			
			double m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			double m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			double m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			double m23 = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 12 ] = m03;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 13 ] = m13;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
			out[ 14 ] = m23;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			out[ 12 ] = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			out[ 13 ] = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			out[ 14 ] = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ];
		}
	}
	
	public static void mmulRotational( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			double m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			double m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			
			double m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			double m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			double m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			
			double m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			double m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			double m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
		}
	}
	
	public static void mmul3x3( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 3 ] + ma[ 2 ] * mb[ 6 ];
			double m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 7 ];
			double m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 8 ];
			
			double m10 = ma[ 3 ] * mb[ 0 ] + ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 6 ];
			double m11 = ma[ 3 ] * mb[ 1 ] + ma[ 4 ] * mb[ 4 ] + ma[ 5 ] * mb[ 7 ];
			double m12 = ma[ 3 ] * mb[ 2 ] + ma[ 4 ] * mb[ 5 ] + ma[ 5 ] * mb[ 8 ];
			
			double m20 = ma[ 6 ] * mb[ 0 ] + ma[ 7 ] * mb[ 3 ] + ma[ 8 ] * mb[ 6 ];
			double m21 = ma[ 6 ] * mb[ 1 ] + ma[ 7 ] * mb[ 4 ] + ma[ 8 ] * mb[ 7 ];
			double m22 = ma[ 6 ] * mb[ 2 ] + ma[ 7 ] * mb[ 5 ] + ma[ 8 ] * mb[ 8 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m10;
			out[ 4 ] = m11;
			out[ 5 ] = m12;
			out[ 6 ] = m20;
			out[ 7 ] = m21;
			out[ 8 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 3 ] + ma[ 2 ] * mb[ 6 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 7 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 8 ];
			
			out[ 3 ] = ma[ 3 ] * mb[ 0 ] + ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 6 ];
			out[ 4 ] = ma[ 3 ] * mb[ 1 ] + ma[ 4 ] * mb[ 4 ] + ma[ 5 ] * mb[ 7 ];
			out[ 5 ] = ma[ 3 ] * mb[ 2 ] + ma[ 4 ] * mb[ 5 ] + ma[ 5 ] * mb[ 8 ];
			
			out[ 6 ] = ma[ 6 ] * mb[ 0 ] + ma[ 7 ] * mb[ 3 ] + ma[ 8 ] * mb[ 6 ];
			out[ 7 ] = ma[ 6 ] * mb[ 1 ] + ma[ 7 ] * mb[ 4 ] + ma[ 8 ] * mb[ 7 ];
			out[ 8 ] = ma[ 6 ] * mb[ 2 ] + ma[ 7 ] * mb[ 5 ] + ma[ 8 ] * mb[ 8 ];
		}
	}
	
	public static void setd( double[ ] array , double ... values )
	{
		System.arraycopy( values , 0 , array , 0 , values.length );
	}
	
	public static void setdNoNaNOrInf( double[ ] array , double ... values )
	{
		for( int i = 0 ; i < values.length ; i++ )
		{
			double d = values[ i ];
			if( !Double.isNaN( d ) && !Double.isInfinite( d ) )
			{
				array[ i ] = d;
			}
		}
	}
	
	public static void setIdentity( double[ ] m )
	{
		m[ 0 ] = 1;
		m[ 4 ] = 0;
		m[ 8 ] = 0;
		m[ 12 ] = 0;
		
		m[ 1 ] = 0;
		m[ 5 ] = 1;
		m[ 9 ] = 0;
		m[ 13 ] = 0;
		
		m[ 2 ] = 0;
		m[ 6 ] = 0;
		m[ 10 ] = 1;
		m[ 14 ] = 0;
		
		m[ 3 ] = 0;
		m[ 7 ] = 0;
		m[ 11 ] = 0;
		m[ 15 ] = 1;
	}
	
	public static void setIdentityAffine( double[ ] m )
	{
		m[ 0 ] = 1;
		m[ 4 ] = 0;
		m[ 8 ] = 0;
		m[ 12 ] = 0;
		
		m[ 1 ] = 0;
		m[ 5 ] = 1;
		m[ 9 ] = 0;
		m[ 13 ] = 0;
		
		m[ 2 ] = 0;
		m[ 6 ] = 0;
		m[ 10 ] = 1;
		m[ 14 ] = 0;
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double[ ] v )
	{
		m[ rowIndex ] = v[ 0 ];
		m[ rowIndex + 4 ] = v[ 1 ];
		m[ rowIndex + 8 ] = v[ 2 ];
		m[ rowIndex + 12 ] = v[ 3 ];
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double[ ] v , int vi )
	{
		m[ rowIndex ] = v[ vi + 0 ];
		m[ rowIndex + 4 ] = v[ vi + 1 ];
		m[ rowIndex + 8 ] = v[ vi + 2 ];
		m[ rowIndex + 12 ] = v[ vi + 3 ];
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double a , double b , double c , double d )
	{
		m[ rowIndex ] = a;
		m[ rowIndex + 4 ] = b;
		m[ rowIndex + 8 ] = c;
		m[ rowIndex + 12 ] = d;
	}
	
	public static void setColumn3( double[ ] m , int colIndex , double a , double b , double c )
	{
		colIndex *= 4;
		m[ colIndex ] = a;
		m[ colIndex + 1 ] = b;
		m[ colIndex + 2 ] = c;
	}
	
	public static void getColumn3( double[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		v[ 0 ] = m[ colIndex ];
		v[ 1 ] = m[ colIndex + 1 ];
		v[ 2 ] = m[ colIndex + 2 ];
	}
	
	public static void getColumn3( float[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		v[ 0 ] = m[ colIndex ];
		v[ 1 ] = m[ colIndex + 1 ];
		v[ 2 ] = m[ colIndex + 2 ];
	}
	
	public static void setColumn3( double[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 1 ] = v[ 1 ];
		m[ colIndex + 2 ] = v[ 2 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 1 ] = v[ 1 ];
		m[ colIndex + 2 ] = v[ 2 ];
		m[ colIndex + 3 ] = v[ 3 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double[ ] v , int vi )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ vi + 0 ];
		m[ colIndex + 1 ] = v[ vi + 1 ];
		m[ colIndex + 2 ] = v[ vi + 2 ];
		m[ colIndex + 3 ] = v[ vi + 3 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double a , double b , double c , double d )
	{
		colIndex *= 4;
		m[ colIndex ] = a;
		m[ colIndex + 1 ] = b;
		m[ colIndex + 2 ] = c;
		m[ colIndex + 3 ] = d;
	}
	
	public static void setScale( double[ ] m , double[ ] v )
	{
		m[ 0 ] = v[ 0 ];
		m[ 5 ] = v[ 1 ];
		m[ 10 ] = v[ 2 ];
	}
	
	public static void setScale( double[ ] m , double[ ] v , int vi )
	{
		m[ 0 ] = v[ vi ];
		m[ 5 ] = v[ vi + 1 ];
		m[ 10 ] = v[ vi + 2 ];
	}
	
	public static void getScale( double[ ] m , double[ ] v )
	{
		v[ 0 ] = m[ 0 ];
		v[ 1 ] = m[ 5 ];
		v[ 2 ] = m[ 10 ];
	}
	
	public static void getScale( double[ ] m , double[ ] v , int vi )
	{
		v[ vi + 0 ] = m[ 0 ];
		v[ vi + 1 ] = m[ 5 ];
		v[ vi + 2 ] = m[ 10 ];
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the x axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the X axis in radians
	 */
	public static void rotX( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = 1f;
		mat[ 4 ] = 0f;
		mat[ 8 ] = 0f;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = 0f;
		mat[ 5 ] = cosAngle;
		mat[ 9 ] = -sinAngle;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = 0f;
		mat[ 6 ] = sinAngle;
		mat[ 10 ] = cosAngle;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the y axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the Y axis in radians
	 */
	public static void rotY( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 4 ] = 0f;
		mat[ 8 ] = sinAngle;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = 0f;
		mat[ 5 ] = 1f;
		mat[ 9 ] = 0f;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = -sinAngle;
		mat[ 6 ] = 0f;
		mat[ 10 ] = cosAngle;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the z axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the Z axis in radians
	 */
	public static void rotZ( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 4 ] = -sinAngle;
		mat[ 8 ] = 0f;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = sinAngle;
		mat[ 5 ] = cosAngle;
		mat[ 9 ] = 0f;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = 0f;
		mat[ 6 ] = 0f;
		mat[ 10 ] = 1f;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	public static void transpose( double[ ] m , double[ ] out )
	{
		if( out != m )
		{
			out[ 0 ] = m[ 0 ];
			out[ 1 ] = m[ 4 ];
			out[ 2 ] = m[ 8 ];
			out[ 3 ] = m[ 12 ];
			
			out[ 4 ] = m[ 1 ];
			out[ 5 ] = m[ 5 ];
			out[ 6 ] = m[ 9 ];
			out[ 7 ] = m[ 13 ];
			
			out[ 8 ] = m[ 2 ];
			out[ 9 ] = m[ 6 ];
			out[ 10 ] = m[ 10 ];
			out[ 11 ] = m[ 14 ];
			
			out[ 12 ] = m[ 3 ];
			out[ 13 ] = m[ 7 ];
			out[ 14 ] = m[ 11 ];
			out[ 15 ] = m[ 15 ];
		}
		else
		{
			double t = m[ 1 ];
			m[ 1 ] = m[ 4 ];
			m[ 4 ] = t;
			
			t = m[ 2 ];
			m[ 2 ] = m[ 8 ];
			m[ 8 ] = t;
			
			t = m[ 3 ];
			m[ 3 ] = m[ 12 ];
			m[ 12 ] = t;
			
			t = m[ 6 ];
			m[ 6 ] = m[ 9 ];
			m[ 9 ] = t;
			
			t = m[ 7 ];
			m[ 7 ] = m[ 13 ];
			m[ 13 ] = t;
			
			t = m[ 11 ];
			m[ 11 ] = m[ 14 ];
			m[ 14 ] = t;
		}
	}
	
	/**
	 * Transposes the upper left 3x3 portion of {@code mat} to {@code out}.
	 * 
	 * @param mat
	 *        a 16-element double array.
	 * @param out
	 *        a 9-element double array.
	 */
	public static void transposeTo3x3( double[ ] mat , double[ ] out )
	{
		out[ 0 ] = mat[ 0 ];
		out[ 1 ] = mat[ 4 ];
		out[ 2 ] = mat[ 8 ];
		out[ 3 ] = mat[ 1 ];
		out[ 4 ] = mat[ 5 ];
		out[ 5 ] = mat[ 9 ];
		out[ 6 ] = mat[ 2 ];
		out[ 7 ] = mat[ 6 ];
		out[ 8 ] = mat[ 10 ];
	}
	
	public static void mcopy( double[ ] msrc , double[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 16 );
	}
	
	public static double detAffine( double[ ] m )
	{
		return m[ 0 ] * ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) -
				m[ 4 ] * ( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) +
				m[ 8 ] * ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] );
	}
	
	public static void invAffine( double[ ] m )
	{
		invAffine( m , m );
	}
	
	public static void invAffine( double[ ] m , double[ ] out )
	{
		double determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		double s = ( m[ 0 ] * m[ 0 ] + m[ 4 ] * m[ 4 ] +
				m[ 8 ] * m[ 8 ] + m[ 12 ] * m[ 12 ] ) *
				( m[ 1 ] * m[ 1 ] + m[ 5 ] * m[ 5 ] +
						m[ 9 ] * m[ 9 ] + m[ 13 ] * m[ 13 ] ) *
				( m[ 2 ] * m[ 2 ] + m[ 6 ] * m[ 6 ] +
						m[ 10 ] * m[ 10 ] + m[ 14 ] * m[ 14 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		double tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) * s;
		double tmp1 = -( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) * s;
		double tmp2 = ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] ) * s;
		double tmp4 = -( m[ 1 ] * m[ 10 ] - m[ 2 ] * m[ 9 ] ) * s;
		double tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 2 ] * m[ 8 ] ) * s;
		double tmp6 = -( m[ 0 ] * m[ 9 ] - m[ 1 ] * m[ 8 ] ) * s;
		double tmp8 = ( m[ 1 ] * m[ 6 ] - m[ 2 ] * m[ 5 ] ) * s;
		double tmp9 = -( m[ 0 ] * m[ 6 ] - m[ 2 ] * m[ 4 ] ) * s;
		double tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 1 ] * m[ 4 ] ) * s;
		double tmp3 = -( m[ 12 ] * tmp0 + m[ 13 ] * tmp1 + m[ 14 ] * tmp2 );
		double tmp7 = -( m[ 12 ] * tmp4 + m[ 13 ] * tmp5 + m[ 14 ] * tmp6 );
		out[ 14 ] = -( m[ 12 ] * tmp8 + m[ 13 ] * tmp9 + m[ 14 ] * tmp10 );
		
		out[ 0 ] = tmp0;
		out[ 4 ] = tmp1;
		out[ 8 ] = tmp2;
		out[ 12 ] = tmp3;
		out[ 1 ] = tmp4;
		out[ 5 ] = tmp5;
		out[ 9 ] = tmp6;
		out[ 13 ] = tmp7;
		out[ 2 ] = tmp8;
		out[ 6 ] = tmp9;
		out[ 10 ] = tmp10;
		out[ 3 ] = out[ 7 ] = out[ 11 ] = 0f;
		out[ 15 ] = 1f;
	}
	
	public static void invAffineToTranspose3x3( double[ ] m , double[ ] out )
	{
		double determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		double s = ( m[ 0 ] * m[ 0 ] + m[ 4 ] * m[ 4 ] +
				m[ 8 ] * m[ 8 ] + m[ 12 ] * m[ 12 ] ) *
				( m[ 1 ] * m[ 1 ] + m[ 5 ] * m[ 5 ] +
						m[ 9 ] * m[ 9 ] + m[ 13 ] * m[ 13 ] ) *
				( m[ 2 ] * m[ 2 ] + m[ 6 ] * m[ 6 ] +
						m[ 10 ] * m[ 10 ] + m[ 14 ] * m[ 14 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		double tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) * s;
		double tmp1 = -( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) * s;
		double tmp2 = ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] ) * s;
		double tmp4 = -( m[ 1 ] * m[ 10 ] - m[ 2 ] * m[ 9 ] ) * s;
		double tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 2 ] * m[ 8 ] ) * s;
		double tmp6 = -( m[ 0 ] * m[ 9 ] - m[ 1 ] * m[ 8 ] ) * s;
		double tmp8 = ( m[ 1 ] * m[ 6 ] - m[ 2 ] * m[ 5 ] ) * s;
		double tmp9 = -( m[ 0 ] * m[ 6 ] - m[ 2 ] * m[ 4 ] ) * s;
		double tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 1 ] * m[ 4 ] ) * s;
		
		out[ 0 ] = tmp0;
		out[ 3 ] = tmp4;
		out[ 6 ] = tmp8;
		out[ 1 ] = tmp1;
		out[ 4 ] = tmp5;
		out[ 7 ] = tmp9;
		out[ 2 ] = tmp2;
		out[ 5 ] = tmp6;
		out[ 8 ] = tmp10;
	}
	
	public static void invertGeneral( double[ ] mat )
	{
		invertGeneral( mat , mat );
	}
	
	/**
	 * General invert routine. Inverts t1 and places the result in "this". Note that this routine handles both the "this" version and the non-"this" version.
	 * 
	 * Also note that since this routine is slow anyway, we won't worry about allocating a little bit of garbage.
	 */
	public static void invertGeneral( double[ ] mat , double[ ] out )
	{
		double tmp[ ] = new double[ 16 ];
		int row_perm[ ] = new int[ 4 ];
		
		// Use LU decomposition and backsubstitution code specifically
		// for doubleing-point 4x4 matrices.
		
		// Copy source matrix to tmp
		System.arraycopy( mat , 0 , tmp , 0 , tmp.length );
		
		// Calculate LU decomposition: Is the matrix singular?
		if( !luDecomposition( tmp , row_perm ) )
		{
			// Matrix has no inverse
			throw new IllegalArgumentException( "Singular Matrix" );
		}
		
		// Perform back substitution on the identity matrix
		// luDecomposition will set rot[] & scales[] for use
		// in luBacksubstituation
		out[ 0 ] = 1f;
		out[ 1 ] = 0f;
		out[ 2 ] = 0f;
		out[ 3 ] = 0f;
		out[ 4 ] = 0f;
		out[ 5 ] = 1f;
		out[ 6 ] = 0f;
		out[ 7 ] = 0f;
		out[ 8 ] = 0f;
		out[ 9 ] = 0f;
		out[ 10 ] = 1f;
		out[ 11 ] = 0f;
		out[ 12 ] = 0f;
		out[ 13 ] = 0f;
		out[ 14 ] = 0f;
		out[ 15 ] = 1f;
		luBacksubstitution( tmp , row_perm , out );
	}
	
	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU decomposition of a row-wise permutation of itself. The input parameters are "matrix0"
	 * and "dimen". The array "matrix0" is also an output parameter. The vector "row_perm[4]" is an output parameter that contains the row permutations
	 * resulting from partial pivoting. The output parameter "even_row_xchg" is 1 when the number of row exchanges is even, or -1 otherwise. Assumes data type
	 * is always double.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	static boolean luDecomposition( double[ ] matrix0 ,
			int[ ] row_perm )
	{
		
		// Can't re-use this temporary since the method is static.
		double row_scale[ ] = new double[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			double big, temp;
			
			// For each row ...
			for( int i = 0 ; i < 4 ; i++ )
			{
				big = 0f;
				
				// For each column, find the largest element in the row
				for( int j = 0 ; j < 4 ; j++ )
				{
					temp = matrix0[ j * 4 + i ];
					temp = Math.abs( temp );
					if( temp > big )
					{
						big = temp;
					}
				}
				
				// Is the matrix singular?
				if( big == 0f )
				{
					return false;
				}
				row_scale[ i ] = 1f / big;
			}
		}
		
		{
			int j;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				double sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = ( 4 * j ) + i;
					sum = matrix0[ target ];
					k = i;
					p1 = i;
					p2 = 4 * j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1 += 4;
						p2++ ;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0f;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = ( 4 * j ) + i;
					sum = matrix0[ target ];
					k = j;
					p1 = i;
					p2 = 4 * j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1 += 4;
						p2++ ;
					}
					matrix0[ target ] = sum;
					
					// Is this the best pivot so far?
					if( ( temp = row_scale[ i ] * Math.abs( sum ) ) >= big )
					{
						big = temp;
						imax = i;
					}
				}
				
				if( imax < 0 )
				{
					return false;
				}
				
				// Is a row exchange necessary?
				if( j != imax )
				{
					// Yes: exchange rows
					k = 4;
					p1 = imax;
					p2 = j;
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1 ] = matrix0[ p2 ];
						matrix0[ p2 ] = temp;
						p1 += 4;
						p2 += 4;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( 4 * j ) + j ] == 0.0 )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1f / ( matrix0[ ( 4 * j ) + j ] );
					target = 4 * j + j + 1;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target++ ;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Solves a set of linear equations. The input parameters "matrix1", and "row_perm" come from luDecompostionD4x4 and do not change here. The parameter
	 * "matrix2" is a set of column vectors assembled into a 4x4 matrix of doubleing-point values. The procedure takes each column of "matrix2" in turn and
	 * treats it as the right-hand side of the matrix equation Ax = LUx = b. The solution vector replaces the original column of the matrix.
	 * 
	 * If "matrix2" is the identity matrix, the procedure replaces its contents with the inverse of the matrix from which "matrix1" was originally derived.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	static void luBacksubstitution( double[ ] matrix1 ,
			int[ ] row_perm ,
			double[ ] matrix2 )
	{
		
		int i, ii, ip, j, k;
		int rp;
		int cv, rv;
		
		// rp = row_perm;
		rp = 0;
		
		// For each column vector of matrix2 ...
		for( k = 0 ; k < 4 ; k++ )
		{
			// cv = &(matrix2[0][k]);
			cv = k;
			ii = -1;
			
			// Forward substitution
			for( i = 0 ; i < 4 ; i++ )
			{
				double sum;
				
				ip = row_perm[ rp + i ];
				sum = matrix2[ ip + 4 * cv ];
				matrix2[ ip + 4 * cv ] = matrix2[ i + 4 * cv ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j * 4 ] * matrix2[ j + 4 * cv ];
					}
				}
				else if( sum != 0f )
				{
					ii = i;
				}
				matrix2[ i + 4 * cv ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3;
			matrix2[ 3 + 4 * cv ] /= matrix1[ rv + 3 * 4 ];
			
			rv-- ;
			matrix2[ 2 + 4 * cv ] = ( matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 2 * 4 ];
			
			rv-- ;
			matrix2[ 1 + 4 * cv ] = ( matrix2[ 1 + 4 * cv ] -
					matrix1[ rv + 2 * 4 ] * matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 1 * 4 ];
			
			rv-- ;
			matrix2[ 0 + 4 * cv ] = ( matrix2[ 0 + 4 * cv ] -
					matrix1[ rv + 1 * 4 ] * matrix2[ 1 + 4 * cv ] -
					matrix1[ rv + 2 * 4 ] * matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 0 * 4 ];
		}
	}
	
	/**
	 * Helping function that specifies the position and orientation of a view matrix. The inverse of this transform can be used to control the ViewPlatform
	 * object within the scene graph.
	 * 
	 * @param eye
	 *        the location of the eye
	 * @param center
	 *        a point in the virtual world where the eye is looking
	 * @param up
	 *        an up vector specifying the frustum's up direction
	 */
	public static void lookAt( double[ ] mat , double eyex , double eyey , double eyez , double centerx , double centery , double centerz , double upx , double upy , double upz )
	{
		double forwardx, forwardy, forwardz, invMag;
		double sidex, sidey, sidez;
		
		forwardx = eyex - centerx;
		forwardy = eyey - centery;
		forwardz = eyez - centerz;
		
		invMag = 1f / ( double ) Math.sqrt( forwardx * forwardx + forwardy * forwardy + forwardz * forwardz );
		forwardx = forwardx * invMag;
		forwardy = forwardy * invMag;
		forwardz = forwardz * invMag;
		
		invMag = 1f / ( double ) Math.sqrt( upx * upx + upy * upy + upz * upz );
		upx *= invMag;
		upy *= invMag;
		upz *= invMag;
		
		// side = Up cross forward
		sidex = upy * forwardz - forwardy * upz;
		sidey = upz * forwardx - upx * forwardz;
		sidez = upx * forwardy - upy * forwardx;
		
		invMag = 1f / ( double ) Math.sqrt( sidex * sidex + sidey * sidey + sidez * sidez );
		sidex *= invMag;
		sidey *= invMag;
		sidez *= invMag;
		
		// recompute up = forward cross side
		
		upx = forwardy * sidez - sidey * forwardz;
		upy = forwardz * sidex - forwardx * sidez;
		upz = forwardx * sidey - forwardy * sidex;
		
		// transpose because we calculated the inverse of what we want
		mat[ 0 ] = sidex;
		mat[ 4 ] = sidey;
		mat[ 8 ] = sidez;
		
		mat[ 1 ] = upx;
		mat[ 5 ] = upy;
		mat[ 9 ] = upz;
		
		mat[ 2 ] = forwardx;
		mat[ 6 ] = forwardy;
		mat[ 10 ] = forwardz;
		
		mat[ 12 ] = -eyex * mat[ 0 ] + -eyey * mat[ 4 ] + -eyez * mat[ 8 ];
		mat[ 13 ] = -eyex * mat[ 1 ] + -eyey * mat[ 5 ] + -eyez * mat[ 9 ];
		mat[ 14 ] = -eyex * mat[ 2 ] + -eyey * mat[ 6 ] + -eyez * mat[ 10 ];
		
		mat[ 3 ] = mat[ 7 ] = mat[ 11 ] = 0;
		mat[ 15 ] = 1;
	}
	
	/**
	 * Creates a perspective projection transform that mimics a standard, camera-based, view-model. This transform maps coordinates from Eye Coordinates (EC) to
	 * Clipping Coordinates (CC). Note that unlike the similar function in OpenGL, the clipping coordinates generated by the resulting transform are in a
	 * right-handed coordinate system (as are all other coordinate systems in Java 3D). Also note that the field of view is specified in radians.
	 * 
	 * @param fovx
	 *        specifies the field of view in the x direction, in radians
	 * @param aspect
	 *        specifies the aspect ratio and thus the field of view in the x direction. The aspect ratio is the ratio of x to y, or width to height.
	 * @param zNear
	 *        the distance to the frustum's near clipping plane. This value must be positive, (the value -zNear is the location of the near clip plane).
	 * @param zFar
	 *        the distance to the frustum's far clipping plane
	 */
	public static void perspective( double[ ] mat , double fovx , double aspect ,
			double zNear , double zFar )
	{
		double sine, cotangent, deltaZ;
		double half_fov = fovx * 0.5f;
		
		deltaZ = zFar - zNear;
		sine = ( double ) Math.sin( half_fov );
		cotangent = ( double ) Math.cos( half_fov ) / sine;
		
		mat[ 0 ] = cotangent;
		mat[ 5 ] = cotangent * aspect;
		mat[ 10 ] = ( zFar + zNear ) / deltaZ;
		mat[ 14 ] = 2f * zNear * zFar / deltaZ;
		mat[ 11 ] = -1f;
		mat[ 4 ] = mat[ 8 ] = mat[ 12 ] = mat[ 1 ] = mat[ 9 ] = mat[ 13 ] = mat[ 2 ] =
				mat[ 6 ] = mat[ 3 ] = mat[ 7 ] = mat[ 15 ] = 0;
	}
	
	public static void calcClippingPlanes( double[ ] mat , double[ ] btlrnf )
	{
		btlrnf[ 4 ] = -mat[ 14 ] / ( 1 - mat[ 10 ] );
		btlrnf[ 5 ] = mat[ 14 ] * btlrnf[ 4 ] / ( mat[ 14 ] + 2 * btlrnf[ 4 ] );
		btlrnf[ 0 ] = btlrnf[ 4 ] * ( mat[ 9 ] - 1 ) / mat[ 5 ];
		btlrnf[ 1 ] = 2 * btlrnf[ 4 ] * mat[ 9 ] / mat[ 5 ] - btlrnf[ 1 ];
		btlrnf[ 2 ] = btlrnf[ 4 ] * ( mat[ 8 ] - 1 ) / mat[ 0 ];
		btlrnf[ 3 ] = 2 * btlrnf[ 4 ] * ( mat[ 8 ] - mat[ 0 ] ) / btlrnf[ 2 ];
	}
	
	public static void ortho( double[ ] mat , double left , double right , double bottom , double top , double zNear , double zFar )
	{
		mat[ 0 ] = 2 / ( right - left );
		mat[ 12 ] = -( right + left ) / ( right - left );
		mat[ 5 ] = 2 / ( top - bottom );
		mat[ 13 ] = -( top + bottom ) / ( top - bottom );
		mat[ 10 ] = -2 / ( zFar - zNear );
		mat[ 14 ] = -( zFar + zNear ) / ( zFar - zNear );
		
		mat[ 15 ] = 1;
		mat[ 4 ] = mat[ 8 ] = mat[ 1 ] = mat[ 9 ] = mat[ 2 ] = mat[ 6 ] = mat[ 7 ] = mat[ 11 ] = 0;
	}
	
	private static boolean almostZero( double a )
	{
		return( ( a < EPSILON_ABSOLUTE ) && ( a > -EPSILON_ABSOLUTE ) );
	}
	
	/**
	 * Sets the rotational component (upper 3x3) of this transform to the matrix equivalent values of the axis-angle argument; the other elements of this
	 * transform are unchanged; any pre-existing scale in the transform is preserved.
	 * 
	 * @param a1
	 *        the axis-angle to be converted (x, y, z, angle)
	 */
	public static void setRotation( double[ ] mat , double x , double y , double z , double angle )
	{
		double mag = ( double ) Math.sqrt( x * x + y * y + z * z );
		
		if( almostZero( mag ) )
		{
			setIdentity( mat );
		}
		else
		{
			mag = 1f / mag;
			double ax = x * mag;
			double ay = y * mag;
			double az = z * mag;
			
			double sinTheta = ( double ) Math.sin( angle );
			double cosTheta = ( double ) Math.cos( angle );
			double t = 1f - cosTheta;
			
			double xz = ax * az;
			double xy = ax * ay;
			double yz = ay * az;
			
			mat[ 0 ] = t * ax * ax + cosTheta;
			mat[ 4 ] = t * xy - sinTheta * az;
			mat[ 8 ] = t * xz + sinTheta * ay;
			mat[ 12 ] = 0;
			
			mat[ 1 ] = t * xy + sinTheta * az;
			mat[ 5 ] = t * ay * ay + cosTheta;
			mat[ 9 ] = t * yz - sinTheta * ax;
			mat[ 13 ] = 0;
			
			mat[ 2 ] = t * xz - sinTheta * ay;
			mat[ 6 ] = t * yz + sinTheta * ax;
			mat[ 10 ] = t * az * az + cosTheta;
			mat[ 14 ] = 0;
			
			mat[ 3 ] = 0;
			mat[ 7 ] = 0;
			mat[ 11 ] = 0;
			mat[ 15 ] = 1;
		}
	}
	
	public static void setRotation( double[ ] mat , double[ ] axis , double angle )
	{
		setRotation( mat , axis[ 0 ] , axis[ 1 ] , axis[ 2 ] , angle );
	}
	
	public static void normalize( double[ ] v , int start , int count )
	{
		double factor = 0;
		for( int i = start ; i < start + count ; i++ )
		{
			factor += v[ i ] * v[ i ];
		}
		
		factor = 1.0 / Math.sqrt( factor );
		
		for( int i = start ; i < start + count ; i++ )
		{
			v[ i ] *= factor;
		}
	}
	
	public static void normalize3( double[ ] v )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		v[ 0 ] *= factor;
		v[ 1 ] *= factor;
		v[ 2 ] *= factor;
	}
	
	public static void normalize3( double x , double y , double z , double[ ] out )
	{
		double factor = 1.0 / Math.sqrt( x * x + y * y + z * z );
		out[ 0 ] = ( double ) ( x * factor );
		out[ 1 ] = ( double ) ( y * factor );
		out[ 2 ] = ( double ) ( z * factor );
	}
	
	public static void normalize3( double[ ] v , double[ ] out )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		out[ 0 ] = ( double ) ( v[ 0 ] * factor );
		out[ 1 ] = ( double ) ( v[ 1 ] * factor );
		out[ 2 ] = ( double ) ( v[ 2 ] * factor );
	}
	
	public static void normalize3( float[ ] v , double[ ] out )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		out[ 0 ] = ( double ) ( v[ 0 ] * factor );
		out[ 1 ] = ( double ) ( v[ 1 ] * factor );
		out[ 2 ] = ( double ) ( v[ 2 ] * factor );
	}
	
	/**
	 * Projects 3-dimensional vector {@code a} onto vector {@code b}, storing the result in {@code out}.
	 */
	public static void vvproj3( double[ ] a , double[ ] b , double[ ] out )
	{
		double aDotB = dot3( a , b );
		double bDotB = dot3( b , b );
		double x = b[ 0 ] * aDotB / bDotB;
		double y = b[ 1 ] * aDotB / bDotB;
		double z = b[ 2 ] * aDotB / bDotB;
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	/**
	 * Projects 3-dimensional vector {@code a} onto a plane with normal {@code n}, storing the result in {@code out}.
	 */
	public static void vpproj3( double[ ] a , double[ ] n , double[ ] out )
	{
		double aDotN = dot3( a , n );
		double nDotN = dot3( n , n );
		double x = a[ 0 ] - n[ 0 ] * aDotN / nDotN;
		double y = a[ 1 ] - n[ 1 ] * aDotN / nDotN;
		double z = a[ 2 ] - n[ 2 ] * aDotN / nDotN;
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	public static void add3( double[ ] a , double[ ] b , double[ ] out )
	{
		out[ 0 ] = a[ 0 ] + b[ 0 ];
		out[ 1 ] = a[ 1 ] + b[ 1 ];
		out[ 2 ] = a[ 2 ] + b[ 2 ];
	}
	
	public static void add3( double[ ] a , int ai , double[ ] b , int bi , double[ ] out , int outi )
	{
		out[ outi + 0 ] = a[ ai + 0 ] + b[ bi + 0 ];
		out[ outi + 1 ] = a[ ai + 1 ] + b[ bi + 1 ];
		out[ outi + 2 ] = a[ ai + 2 ] + b[ bi + 2 ];
	}
	
	public static void sub3( double[ ] a , double[ ] b , double[ ] out )
	{
		out[ 0 ] = a[ 0 ] - b[ 0 ];
		out[ 1 ] = a[ 1 ] - b[ 1 ];
		out[ 2 ] = a[ 2 ] - b[ 2 ];
	}
	
	public static void sub3( double[ ] a , int ai , double[ ] b , int bi , double[ ] out , int outi )
	{
		out[ outi + 0 ] = a[ ai + 0 ] - b[ bi + 0 ];
		out[ outi + 1 ] = a[ ai + 1 ] - b[ bi + 1 ];
		out[ outi + 2 ] = a[ ai + 2 ] - b[ bi + 2 ];
	}
	
	public static void scale3( double[ ] a , double f )
	{
		scale3( a , f , a );
	}
	
	public static void scale3( double[ ] a , double f , double[ ] out )
	{
		out[ 0 ] = a[ 0 ] * f;
		out[ 1 ] = a[ 1 ] * f;
		out[ 2 ] = a[ 2 ] * f;
	}
	
	public static void scaleAdd3( double a , double[ ] b , double[ ] c , double[ ] out )
	{
		out[ 0 ] = a * b[ 0 ] + c[ 0 ];
		out[ 1 ] = a * b[ 1 ] + c[ 1 ];
		out[ 2 ] = a * b[ 2 ] + c[ 2 ];
	}
	
	public static void scaleAdd3( double a , double[ ] b , float[ ] c , double[ ] out )
	{
		out[ 0 ] = a * b[ 0 ] + c[ 0 ];
		out[ 1 ] = a * b[ 1 ] + c[ 1 ];
		out[ 2 ] = a * b[ 2 ] + c[ 2 ];
	}
	
	public static double length( double[ ] v , int start , int count )
	{
		double total = 0;
		for( int i = start ; i < count ; i++ )
		{
			total += v[ i ] * v[ i ];
		}
		return ( double ) Math.sqrt( total );
	}
	
	public static double length3( double[ ] v )
	{
		return ( double ) Math.sqrt( dot3( v , v ) );
	}
	
	public static void negate3( double[ ] v )
	{
		v[ 0 ] = -v[ 0 ];
		v[ 1 ] = -v[ 1 ];
		v[ 2 ] = -v[ 2 ];
	}
	
	public static boolean epsilonEquals( double[ ] a , double[ ] b , double epsilon )
	{
		for( int i = 0 ; i < a.length ; i++ )
		{
			double diff = a[ i ] - b[ i ];
			if( Double.isNaN( diff ) )
			{
				return false;
			}
			if( Math.abs( diff ) > epsilon )
			{
				return false;
			}
		}
		return true;
	}
	
	public static String matToString( double[ ] m , String elemFormat )
	{
		StringBuilder sb = new StringBuilder( );
		for( int row = 0 ; row < 4 ; row++ )
		{
			for( int col = 0 ; col < 4 ; col++ )
			{
				if( col > 0 )
				{
					sb.append( ' ' );
				}
				sb.append( String.format( elemFormat , m[ row + col * 4 ] ) );
			}
			sb.append( '\n' );
		}
		return sb.toString( );
	}
	
	public static String matToString( double[ ] m )
	{
		return matToString( m , "%12.4f" );
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////
	// FLOAT METHODS /////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////
	
	public static float distance3( float[ ] a , float[ ] b )
	{
		float dx = a[ 0 ] - b[ 0 ];
		float dy = a[ 1 ] - b[ 1 ];
		float dz = a[ 2 ] - b[ 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static float distance3sq( float[ ] a , float[ ] b )
	{
		float dx = a[ 0 ] - b[ 0 ];
		float dy = a[ 1 ] - b[ 1 ];
		float dz = a[ 2 ] - b[ 2 ];
		
		return dx * dx + dy * dy + dz * dz;
	}
	
	/**
	 * @param p
	 *        a 3-dimensional point.
	 * @param origin
	 *        the origin of a 3-dimensional ray.
	 * @param unitDirection
	 *        the 3-dimensional direction of the ray - must be a unit vector
	 * @return the squared distance from {@code p} to the nearest point along the ray.
	 */
	public static float distanceFromLine3sq( float[ ] p , float[ ] origin , float[ ] unitDirection )
	{
		float adjacent = subDot3( p , origin , unitDirection );
		float hypoteneuseSq = distance3sq( p , origin );
		return hypoteneuseSq - adjacent * adjacent;
	}
	
	public static float partDist( float[ ] a , float[ ] b , int ... dims )
	{
		float total = 0f;
		
		for( int dim : dims )
		{
			float dimDist = a[ dim ] - b[ dim ];
			total += dimDist * dimDist;
		}
		
		return ( float ) Math.sqrt( total );
	}
	
	public static double partDist( double[ ] a , double[ ] b , int ... dims )
	{
		double total = 0f;
		
		for( int dim : dims )
		{
			double dimDist = a[ dim ] - b[ dim ];
			total += dimDist * dimDist;
		}
		
		return Math.sqrt( total );
	}
	
	public static float distance3( float[ ] a , int ai , float[ ] b , int bi )
	{
		float dx = a[ ai ] - b[ bi ];
		float dy = a[ ai + 1 ] - b[ bi + 1 ];
		float dz = a[ ai + 2 ] - b[ bi + 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	/**
	 * Computes ((x,y,z) - b) dot c.
	 */
	public static float subDot3( float x , float y , float z , float[ ] b , float[ ] c )
	{
		return ( x - b[ 0 ] ) * c[ 0 ] + ( y - b[ 1 ] ) * c[ 1 ] + ( z - b[ 2 ] ) * c[ 2 ];
	}
	
	/**
	 * Computes (a - b) dot c.
	 */
	public static float subDot3( float[ ] a , float[ ] b , float[ ] c )
	{
		return ( a[ 0 ] - b[ 0 ] ) * c[ 0 ] + ( a[ 1 ] - b[ 1 ] ) * c[ 1 ] + ( a[ 2 ] - b[ 2 ] ) * c[ 2 ];
	}
	
	/**
	 * Computes (a - b) dot c.
	 */
	public static float subDot3( float[ ] a , int ai , float[ ] b , int bi , float[ ] c , int ci )
	{
		return ( a[ ai + 0 ] - b[ bi + 0 ] ) * c[ ci + 0 ] +
				( a[ ai + 1 ] - b[ bi + 1 ] ) * c[ ci + 1 ] +
				( a[ ai + 2 ] - b[ bi + 2 ] ) * c[ ci + 2 ];
	}
	
	public static float dot3( float[ ] a , float[ ] b )
	{
		return a[ 0 ] * b[ 0 ] + a[ 1 ] * b[ 1 ] + a[ 2 ] * b[ 2 ];
	}
	
	public static float dot3( float[ ] a , int ai , float[ ] b , int bi )
	{
		return a[ ai + 0 ] * b[ bi + 0 ] + a[ ai + 1 ] * b[ bi + 1 ] + a[ ai + 2 ] * b[ bi + 2 ];
	}
	
	public static void cross( float[ ] a , float[ ] b , float[ ] out )
	{
		if( out != a && out != b )
		{
			out[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			out[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
		}
		else
		{
			float x = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			float y = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
			out[ 1 ] = y;
			out[ 0 ] = x;
		}
	}
	
	/**
	 * Computes (b - a) x (c - a).
	 */
	public static void threePointNormal( float[ ] a , float[ ] b , float[ ] c , float[ ] out )
	{
		float b0 = b[ 0 ] - a[ 0 ];
		float b1 = b[ 1 ] - a[ 1 ];
		float b2 = b[ 2 ] - a[ 2 ];
		float c0 = c[ 0 ] - a[ 0 ];
		float c1 = c[ 1 ] - a[ 1 ];
		float c2 = c[ 2 ] - a[ 2 ];
		
		float x = b1 * c2 - b2 * c1;
		float y = b2 * c0 - b0 * c2;
		float z = b0 * c1 - b1 * c0;
		
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	public static void cross( float[ ] a , float[ ] b , double[ ] out )
	{
		out[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
		out[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
		out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
	}
	
	public static void cross( float[ ] a , float x , float y , float z , float[ ] out )
	{
		if( out != a )
		{
			out[ 0 ] = a[ 1 ] * z - a[ 2 ] * y;
			out[ 1 ] = a[ 2 ] * x - a[ 0 ] * z;
			out[ 2 ] = a[ 0 ] * y - a[ 1 ] * x;
		}
		else
		{
			float cx = a[ 1 ] * z - a[ 2 ] * y;
			float cy = a[ 2 ] * x - a[ 0 ] * z;
			out[ 2 ] = a[ 0 ] * y - a[ 1 ] * x;
			out[ 1 ] = cy;
			out[ 0 ] = cx;
		}
	}
	
	public static void cross( float x , float y , float z , float[ ] b , float[ ] out )
	{
		if( out != b )
		{
			out[ 0 ] = y * b[ 2 ] - z * b[ 1 ];
			out[ 1 ] = z * b[ 0 ] - x * b[ 2 ];
			out[ 2 ] = x * b[ 1 ] - y * b[ 0 ];
		}
		else
		{
			float cx = y * b[ 2 ] - z * b[ 1 ];
			float cy = z * b[ 0 ] - x * b[ 2 ];
			out[ 2 ] = x * b[ 1 ] - y * b[ 0 ];
			out[ 1 ] = cy;
			out[ 0 ] = cx;
		}
	}
	
	/**
	 * Computes out = (a - b) x c.
	 */
	public static void subCross( float[ ] a , float[ ] b , float[ ] c , float[ ] out )
	{
		cross( a[ 0 ] - b[ 0 ] , a[ 1 ] - b[ 1 ] , a[ 2 ] - b[ 2 ] , c , out );
	}
	
	public static void cross( float x , float y , float z , float[ ] b , int bi , float[ ] out , int outi )
	{
		if( out != b )
		{
			out[ outi + 0 ] = y * b[ bi + 2 ] - z * b[ bi + 1 ];
			out[ outi + 1 ] = z * b[ bi + 0 ] - x * b[ bi + 2 ];
			out[ outi + 2 ] = x * b[ bi + 1 ] - y * b[ bi + 0 ];
		}
		else
		{
			float cx = y * b[ bi + 2 ] - z * b[ bi + 1 ];
			float cy = z * b[ bi + 0 ] - x * b[ bi + 2 ];
			out[ outi + 2 ] = x * b[ bi + 1 ] - y * b[ bi + 0 ];
			out[ outi + 1 ] = cy;
			out[ outi + 0 ] = cx;
		}
	}
	
	public static void cross(
			float ax , float ay , float az ,
			float bx , float by , float bz ,
			float[ ] out )
	{
		float cx = ay * bz - az * by;
		float cy = az * bx - ax * bz;
		out[ 2 ] = ax * by - ay * bx;
		out[ 1 ] = cy;
		out[ 0 ] = cx;
	}
	
	public static void cross( float[ ] a , int ai , float[ ] b , int bi , float[ ] out , int outi )
	{
		if( out != a && out != b )
		{
			out[ outi + 0 ] = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			out[ outi + 1 ] = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
		}
		else
		{
			float x = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			float y = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
			out[ outi + 1 ] = y;
			out[ outi + 0 ] = x;
		}
	}
	
	public static float[ ] newMat3f( )
	{
		return new float[ ] { 1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 };
	}
	
	public static float[ ] newMat4f( )
	{
		return new float[ ] { 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 };
	}
	
	public static void mpmul( float[ ] m , float[ ] p )
	{
		float rw = 1 / ( m[ 3 ] * p[ 0 ] + m[ 7 ] * p[ 1 ] + m[ 11 ] * p[ 2 ] + m[ 15 ] );
		float x = rw * ( m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ] );
		float y = rw * ( m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ] );
		p[ 2 ] = rw * ( m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ] );
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmul( float[ ] m , float[ ] p , float[ ] out )
	{
		if( p != out )
		{
			float rw = 1 / ( m[ 3 ] * p[ 0 ] + m[ 7 ] * p[ 1 ] + m[ 11 ] * p[ 2 ] + m[ 15 ] );
			out[ 0 ] = rw * ( m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ] );
			out[ 1 ] = rw * ( m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ] );
			out[ 2 ] = rw * ( m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ] );
		}
		else
		{
			mpmul( m , p );
		}
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p )
	{
		float x = m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ];
		float y = m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ];
		p[ 2 ] = m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ];
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi )
	{
		float x = m[ 0 ] * p[ vi ] + m[ 4 ] * p[ vi + 1 ] + m[ 8 ] * p[ vi + 2 ] + m[ 12 ];
		float y = m[ 1 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 9 ] * p[ vi + 2 ] + m[ 13 ];
		p[ vi + 2 ] = m[ 2 ] * p[ vi ] + m[ 6 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 14 ];
		p[ vi + 1 ] = y;
		p[ vi ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , float[ ] out )
	{
		if( p != out )
		{
			out[ 0 ] = m[ 0 ] * p[ 0 ] + m[ 4 ] * p[ 1 ] + m[ 8 ] * p[ 2 ] + m[ 12 ];
			out[ 1 ] = m[ 1 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 9 ] * p[ 2 ] + m[ 13 ];
			out[ 2 ] = m[ 2 ] * p[ 0 ] + m[ 6 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 14 ];
		}
		else
		{
			mpmulAffine( m , p );
		}
	}
	
	public static void mpmulAffine( float[ ] m , float x , float y , float z , float[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z + m[ 12 ];
		out[ 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z + m[ 13 ];
		out[ 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z + m[ 14 ];
	}
	
	public static void mpmulAffine( float[ ] m , float x , float y , float z , float[ ] out , int outi )
	{
		out[ outi ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z + m[ 12 ];
		out[ outi + 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z + m[ 13 ];
		out[ outi + 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z + m[ 14 ];
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi , float[ ] out , int outi )
	{
		if( p != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * p[ vi ] + m[ 4 ] * p[ vi + 1 ] + m[ 8 ] * p[ vi + 2 ] + m[ 12 ];
			out[ outi + 1 ] = m[ 1 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 9 ] * p[ vi + 2 ] + m[ 13 ];
			out[ outi + 2 ] = m[ 2 ] * p[ vi ] + m[ 6 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 14 ];
		}
		else
		{
			mpmulAffine( m , p , vi );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v )
	{
		float x = m[ 0 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
		float y = m[ 1 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 9 ] * v[ 2 ];
		v[ 2 ] = m[ 2 ] * v[ 0 ] + m[ 6 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		v[ 1 ] = y;
		v[ 0 ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi )
	{
		float x = m[ 0 ] * v[ vi ] + m[ 4 ] * v[ vi + 1 ] + m[ 8 ] * v[ vi + 2 ];
		float y = m[ 1 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 9 ] * v[ vi + 2 ];
		v[ vi + 2 ] = m[ 2 ] * v[ vi ] + m[ 6 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		v[ vi + 1 ] = y;
		v[ vi ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , float[ ] out )
	{
		if( v != out )
		{
			out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
			out[ 1 ] = m[ 1 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 9 ] * v[ 2 ];
			out[ 2 ] = m[ 2 ] * v[ 0 ] + m[ 6 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		}
		else
		{
			mvmulAffine( m , v );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float x , float y , float z , float[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z;
		out[ 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z;
		out[ 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( float[ ] m , float x , float y , float z , float[ ] out , int outi )
	{
		out[ outi ] = m[ 0 ] * x + m[ 4 ] * y + m[ 8 ] * z;
		out[ outi + 1 ] = m[ 1 ] * x + m[ 5 ] * y + m[ 9 ] * z;
		out[ outi + 2 ] = m[ 2 ] * x + m[ 6 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi , float[ ] out , int outi )
	{
		if( v != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * v[ vi ] + m[ 4 ] * v[ vi + 1 ] + m[ 8 ] * v[ vi + 2 ];
			out[ outi + 1 ] = m[ 1 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 9 ] * v[ vi + 2 ];
			out[ outi + 2 ] = m[ 2 ] * v[ vi ] + m[ 6 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		}
		else
		{
			mvmulAffine( m , v , vi );
		}
	}
	
	public static void mmul( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ] + ma[ 12 ] * mb[ 3 ];
			float m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ] + ma[ 12 ] * mb[ 7 ];
			float m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ] + ma[ 12 ] * mb[ 11 ];
			float m03 = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ] * mb[ 15 ];
			
			float m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ] + ma[ 13 ] * mb[ 3 ];
			float m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ] + ma[ 13 ] * mb[ 7 ];
			float m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ] + ma[ 13 ] * mb[ 11 ];
			float m13 = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ] * mb[ 15 ];
			
			float m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ] + ma[ 14 ] * mb[ 3 ];
			float m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ] + ma[ 14 ] * mb[ 7 ];
			float m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ] + ma[ 14 ] * mb[ 11 ];
			float m23 = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ] * mb[ 15 ];
			
			float m30 = ma[ 3 ] * mb[ 0 ] + ma[ 7 ] * mb[ 1 ] + ma[ 11 ] * mb[ 2 ] + ma[ 15 ] * mb[ 3 ];
			float m31 = ma[ 3 ] * mb[ 4 ] + ma[ 7 ] * mb[ 5 ] + ma[ 11 ] * mb[ 6 ] + ma[ 15 ] * mb[ 7 ];
			float m32 = ma[ 3 ] * mb[ 8 ] + ma[ 7 ] * mb[ 9 ] + ma[ 11 ] * mb[ 10 ] + ma[ 15 ] * mb[ 11 ];
			float m33 = ma[ 3 ] * mb[ 12 ] + ma[ 7 ] * mb[ 13 ] + ma[ 11 ] * mb[ 14 ] + ma[ 15 ] * mb[ 15 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 12 ] = m03;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 13 ] = m13;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
			out[ 14 ] = m23;
			out[ 3 ] = m30;
			out[ 7 ] = m31;
			out[ 11 ] = m32;
			out[ 15 ] = m33;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ] + ma[ 12 ] * mb[ 3 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ] + ma[ 12 ] * mb[ 7 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ] + ma[ 12 ] * mb[ 11 ];
			out[ 12 ] = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ] * mb[ 15 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ] + ma[ 13 ] * mb[ 3 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ] + ma[ 13 ] * mb[ 7 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ] + ma[ 13 ] * mb[ 11 ];
			out[ 13 ] = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ] * mb[ 15 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ] + ma[ 14 ] * mb[ 3 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ] + ma[ 14 ] * mb[ 7 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ] + ma[ 14 ] * mb[ 11 ];
			out[ 14 ] = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ] * mb[ 15 ];
			
			out[ 3 ] = ma[ 3 ] * mb[ 0 ] + ma[ 7 ] * mb[ 1 ] + ma[ 11 ] * mb[ 2 ] + ma[ 15 ] * mb[ 3 ];
			out[ 7 ] = ma[ 3 ] * mb[ 4 ] + ma[ 7 ] * mb[ 5 ] + ma[ 11 ] * mb[ 6 ] + ma[ 15 ] * mb[ 7 ];
			out[ 11 ] = ma[ 3 ] * mb[ 8 ] + ma[ 7 ] * mb[ 9 ] + ma[ 11 ] * mb[ 10 ] + ma[ 15 ] * mb[ 11 ];
			out[ 15 ] = ma[ 3 ] * mb[ 12 ] + ma[ 7 ] * mb[ 13 ] + ma[ 11 ] * mb[ 14 ] + ma[ 15 ] * mb[ 15 ];
		}
	}
	
	public static void mmulAffine( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			float m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			float m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			float m03 = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ];
			
			float m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			float m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			float m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			float m13 = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ];
			
			float m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			float m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			float m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			float m23 = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 12 ] = m03;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 13 ] = m13;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
			out[ 14 ] = m23;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			out[ 12 ] = ma[ 0 ] * mb[ 12 ] + ma[ 4 ] * mb[ 13 ] + ma[ 8 ] * mb[ 14 ] + ma[ 12 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			out[ 13 ] = ma[ 1 ] * mb[ 12 ] + ma[ 5 ] * mb[ 13 ] + ma[ 9 ] * mb[ 14 ] + ma[ 13 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			out[ 14 ] = ma[ 2 ] * mb[ 12 ] + ma[ 6 ] * mb[ 13 ] + ma[ 10 ] * mb[ 14 ] + ma[ 14 ];
		}
	}
	
	public static void mmulRotational( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			float m01 = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			float m02 = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			
			float m10 = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			float m11 = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			float m12 = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			
			float m20 = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			float m21 = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			float m22 = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
			
			out[ 0 ] = m00;
			out[ 4 ] = m01;
			out[ 8 ] = m02;
			out[ 1 ] = m10;
			out[ 5 ] = m11;
			out[ 9 ] = m12;
			out[ 2 ] = m20;
			out[ 6 ] = m21;
			out[ 10 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 4 ] * mb[ 1 ] + ma[ 8 ] * mb[ 2 ];
			out[ 4 ] = ma[ 0 ] * mb[ 4 ] + ma[ 4 ] * mb[ 5 ] + ma[ 8 ] * mb[ 6 ];
			out[ 8 ] = ma[ 0 ] * mb[ 8 ] + ma[ 4 ] * mb[ 9 ] + ma[ 8 ] * mb[ 10 ];
			
			out[ 1 ] = ma[ 1 ] * mb[ 0 ] + ma[ 5 ] * mb[ 1 ] + ma[ 9 ] * mb[ 2 ];
			out[ 5 ] = ma[ 1 ] * mb[ 4 ] + ma[ 5 ] * mb[ 5 ] + ma[ 9 ] * mb[ 6 ];
			out[ 9 ] = ma[ 1 ] * mb[ 8 ] + ma[ 5 ] * mb[ 9 ] + ma[ 9 ] * mb[ 10 ];
			
			out[ 2 ] = ma[ 2 ] * mb[ 0 ] + ma[ 6 ] * mb[ 1 ] + ma[ 10 ] * mb[ 2 ];
			out[ 6 ] = ma[ 2 ] * mb[ 4 ] + ma[ 6 ] * mb[ 5 ] + ma[ 10 ] * mb[ 6 ];
			out[ 10 ] = ma[ 2 ] * mb[ 8 ] + ma[ 6 ] * mb[ 9 ] + ma[ 10 ] * mb[ 10 ];
		}
	}
	
	public static void mmul3x3( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 3 ] + ma[ 2 ] * mb[ 6 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 7 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 8 ];
			
			float m10 = ma[ 3 ] * mb[ 0 ] + ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 6 ];
			float m11 = ma[ 3 ] * mb[ 1 ] + ma[ 4 ] * mb[ 4 ] + ma[ 5 ] * mb[ 7 ];
			float m12 = ma[ 3 ] * mb[ 2 ] + ma[ 4 ] * mb[ 5 ] + ma[ 5 ] * mb[ 8 ];
			
			float m20 = ma[ 6 ] * mb[ 0 ] + ma[ 7 ] * mb[ 3 ] + ma[ 8 ] * mb[ 6 ];
			float m21 = ma[ 6 ] * mb[ 1 ] + ma[ 7 ] * mb[ 4 ] + ma[ 8 ] * mb[ 7 ];
			float m22 = ma[ 6 ] * mb[ 2 ] + ma[ 7 ] * mb[ 5 ] + ma[ 8 ] * mb[ 8 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m10;
			out[ 4 ] = m11;
			out[ 5 ] = m12;
			out[ 6 ] = m20;
			out[ 7 ] = m21;
			out[ 8 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 3 ] + ma[ 2 ] * mb[ 6 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 7 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 8 ];
			
			out[ 3 ] = ma[ 3 ] * mb[ 0 ] + ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 6 ];
			out[ 4 ] = ma[ 3 ] * mb[ 1 ] + ma[ 4 ] * mb[ 4 ] + ma[ 5 ] * mb[ 7 ];
			out[ 5 ] = ma[ 3 ] * mb[ 2 ] + ma[ 4 ] * mb[ 5 ] + ma[ 5 ] * mb[ 8 ];
			
			out[ 6 ] = ma[ 6 ] * mb[ 0 ] + ma[ 7 ] * mb[ 3 ] + ma[ 8 ] * mb[ 6 ];
			out[ 7 ] = ma[ 6 ] * mb[ 1 ] + ma[ 7 ] * mb[ 4 ] + ma[ 8 ] * mb[ 7 ];
			out[ 8 ] = ma[ 6 ] * mb[ 2 ] + ma[ 7 ] * mb[ 5 ] + ma[ 8 ] * mb[ 8 ];
		}
	}
	
	public static void setf( float[ ] array , float ... values )
	{
		System.arraycopy( values , 0 , array , 0 , Math.min( array.length , values.length ) );
	}
	
	public static void set3( float[ ] array , int arrayi , float[ ] values , int valuesi )
	{
		System.arraycopy( values , valuesi , array , arrayi , 3 );
	}
	
	public static void setIdentity( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 4 ] = 0;
		m[ 8 ] = 0;
		m[ 12 ] = 0;
		
		m[ 1 ] = 0;
		m[ 5 ] = 1;
		m[ 9 ] = 0;
		m[ 13 ] = 0;
		
		m[ 2 ] = 0;
		m[ 6 ] = 0;
		m[ 10 ] = 1;
		m[ 14 ] = 0;
		
		m[ 3 ] = 0;
		m[ 7 ] = 0;
		m[ 11 ] = 0;
		m[ 15 ] = 1;
	}
	
	public static void setIdentityAffine( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 4 ] = 0;
		m[ 8 ] = 0;
		m[ 12 ] = 0;
		
		m[ 1 ] = 0;
		m[ 5 ] = 1;
		m[ 9 ] = 0;
		m[ 13 ] = 0;
		
		m[ 2 ] = 0;
		m[ 6 ] = 0;
		m[ 10 ] = 1;
		m[ 14 ] = 0;
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float[ ] v )
	{
		m[ rowIndex ] = v[ 0 ];
		m[ rowIndex + 4 ] = v[ 1 ];
		m[ rowIndex + 8 ] = v[ 2 ];
		m[ rowIndex + 12 ] = v[ 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float[ ] v , int vi )
	{
		m[ rowIndex ] = v[ vi + 0 ];
		m[ rowIndex + 4 ] = v[ vi + 1 ];
		m[ rowIndex + 8 ] = v[ vi + 2 ];
		m[ rowIndex + 12 ] = v[ vi + 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float a , float b , float c , float d )
	{
		m[ rowIndex ] = a;
		m[ rowIndex + 4 ] = b;
		m[ rowIndex + 8 ] = c;
		m[ rowIndex + 12 ] = d;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , float a , float b , float c )
	{
		colIndex *= 4;
		m[ colIndex ] = a;
		m[ colIndex + 1 ] = b;
		m[ colIndex + 2 ] = c;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , float[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 1 ] = v[ 1 ];
		m[ colIndex + 2 ] = v[ 2 ];
	}
	
	public static void getColumn3( float[ ] m , int colIndex , float[ ] v )
	{
		colIndex *= 4;
		v[ 0 ] = m[ colIndex ];
		v[ 1 ] = m[ colIndex + 1 ];
		v[ 2 ] = m[ colIndex + 2 ];
	}
	
	public static void getColumn3( float[ ] m , int colIndex , float[ ] v , int vi )
	{
		colIndex *= 4;
		v[ vi ] = m[ colIndex ];
		v[ vi + 1 ] = m[ colIndex + 1 ];
		v[ vi + 2 ] = m[ colIndex + 2 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 1 ] = v[ 1 ];
		m[ colIndex + 2 ] = v[ 2 ];
		m[ colIndex + 3 ] = v[ 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float[ ] v , int vi )
	{
		colIndex *= 4;
		m[ colIndex ] = v[ vi + 0 ];
		m[ colIndex + 1 ] = v[ vi + 1 ];
		m[ colIndex + 2 ] = v[ vi + 2 ];
		m[ colIndex + 3 ] = v[ vi + 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float a , float b , float c , float d )
	{
		colIndex *= 4;
		m[ colIndex ] = a;
		m[ colIndex + 1 ] = b;
		m[ colIndex + 2 ] = c;
		m[ colIndex + 3 ] = d;
	}
	
	public static void setScale( float[ ] m , float[ ] v )
	{
		m[ 0 ] = v[ 0 ];
		m[ 5 ] = v[ 1 ];
		m[ 10 ] = v[ 2 ];
	}
	
	public static void setScale( float[ ] m , float[ ] v , int vi )
	{
		m[ 0 ] = v[ vi ];
		m[ 5 ] = v[ vi + 1 ];
		m[ 10 ] = v[ vi + 2 ];
	}
	
	public static void getScale( float[ ] m , float[ ] v )
	{
		v[ 0 ] = m[ 0 ];
		v[ 1 ] = m[ 5 ];
		v[ 2 ] = m[ 10 ];
	}
	
	public static void getScale( float[ ] m , float[ ] v , int vi )
	{
		v[ vi + 0 ] = m[ 0 ];
		v[ vi + 1 ] = m[ 5 ];
		v[ vi + 2 ] = m[ 10 ];
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the x axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the X axis in radians
	 */
	public static void rotX( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = 1f;
		mat[ 4 ] = 0f;
		mat[ 8 ] = 0f;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = 0f;
		mat[ 5 ] = cosAngle;
		mat[ 9 ] = -sinAngle;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = 0f;
		mat[ 6 ] = sinAngle;
		mat[ 10 ] = cosAngle;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the y axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the Y axis in radians
	 */
	public static void rotY( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 4 ] = 0f;
		mat[ 8 ] = sinAngle;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = 0f;
		mat[ 5 ] = 1f;
		mat[ 9 ] = 0f;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = -sinAngle;
		mat[ 6 ] = 0f;
		mat[ 10 ] = cosAngle;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the z axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *        the angle to rotate about the Z axis in radians
	 */
	public static void rotZ( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 4 ] = -sinAngle;
		mat[ 8 ] = 0f;
		mat[ 12 ] = 0f;
		
		mat[ 1 ] = sinAngle;
		mat[ 5 ] = cosAngle;
		mat[ 9 ] = 0f;
		mat[ 13 ] = 0f;
		
		mat[ 2 ] = 0f;
		mat[ 6 ] = 0f;
		mat[ 10 ] = 1f;
		mat[ 14 ] = 0f;
		
		mat[ 3 ] = 0f;
		mat[ 7 ] = 0f;
		mat[ 11 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	public static void transpose( float[ ] m , float[ ] out )
	{
		if( out != m )
		{
			out[ 0 ] = m[ 0 ];
			out[ 1 ] = m[ 1 ];
			out[ 2 ] = m[ 2 ];
			out[ 3 ] = m[ 3 ];
			
			out[ 4 ] = m[ 4 ];
			out[ 5 ] = m[ 5 ];
			out[ 6 ] = m[ 6 ];
			out[ 7 ] = m[ 7 ];
			
			out[ 8 ] = m[ 8 ];
			out[ 9 ] = m[ 9 ];
			out[ 10 ] = m[ 10 ];
			out[ 11 ] = m[ 11 ];
			
			out[ 12 ] = m[ 12 ];
			out[ 13 ] = m[ 13 ];
			out[ 14 ] = m[ 14 ];
			out[ 15 ] = m[ 15 ];
		}
		else
		{
			float t = m[ 4 ];
			m[ 4 ] = m[ 1 ];
			m[ 1 ] = t;
			
			t = m[ 8 ];
			m[ 8 ] = m[ 2 ];
			m[ 2 ] = t;
			
			t = m[ 12 ];
			m[ 12 ] = m[ 3 ];
			m[ 3 ] = t;
			
			t = m[ 9 ];
			m[ 9 ] = m[ 6 ];
			m[ 6 ] = t;
			
			t = m[ 13 ];
			m[ 13 ] = m[ 7 ];
			m[ 7 ] = t;
			
			t = m[ 14 ];
			m[ 14 ] = m[ 11 ];
			m[ 11 ] = t;
		}
	}
	
	/**
	 * Transposes the upper left 3x3 portion of {@code mat} to {@code out}.
	 * 
	 * @param mat
	 *        a 16-element float array.
	 * @param out
	 *        a 9-element float array.
	 */
	public static void transposeTo3x3( float[ ] mat , float[ ] out )
	{
		out[ 0 ] = mat[ 0 ];
		out[ 1 ] = mat[ 1 ];
		out[ 2 ] = mat[ 2 ];
		out[ 3 ] = mat[ 4 ];
		out[ 4 ] = mat[ 5 ];
		out[ 5 ] = mat[ 6 ];
		out[ 6 ] = mat[ 8 ];
		out[ 7 ] = mat[ 9 ];
		out[ 8 ] = mat[ 10 ];
	}
	
	public static void mcopy( float[ ] msrc , float[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 16 );
	}
	
	public static void mcopyAffine( float[ ] msrc , float[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 12 );
	}
	
	public static float detAffine( float[ ] m )
	{
		return m[ 0 ] * ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) -
				m[ 4 ] * ( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) +
				m[ 8 ] * ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] );
	}
	
	public static void invAffine( float[ ] m )
	{
		invAffine( m , m );
	}
	
	public static void invAffine( float[ ] m , float[ ] out )
	{
		float determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		float s = ( m[ 0 ] * m[ 0 ] + m[ 4 ] * m[ 4 ] +
				m[ 8 ] * m[ 8 ] + m[ 12 ] * m[ 12 ] ) *
				( m[ 1 ] * m[ 1 ] + m[ 5 ] * m[ 5 ] +
						m[ 9 ] * m[ 9 ] + m[ 13 ] * m[ 13 ] ) *
				( m[ 2 ] * m[ 2 ] + m[ 6 ] * m[ 6 ] +
						m[ 10 ] * m[ 10 ] + m[ 14 ] * m[ 14 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		float tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) * s;
		float tmp1 = -( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) * s;
		float tmp2 = ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] ) * s;
		float tmp4 = -( m[ 1 ] * m[ 10 ] - m[ 2 ] * m[ 9 ] ) * s;
		float tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 2 ] * m[ 8 ] ) * s;
		float tmp6 = -( m[ 0 ] * m[ 9 ] - m[ 1 ] * m[ 8 ] ) * s;
		float tmp8 = ( m[ 1 ] * m[ 6 ] - m[ 2 ] * m[ 5 ] ) * s;
		float tmp9 = -( m[ 0 ] * m[ 6 ] - m[ 2 ] * m[ 4 ] ) * s;
		float tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 1 ] * m[ 4 ] ) * s;
		float tmp3 = -( m[ 12 ] * tmp0 + m[ 13 ] * tmp1 + m[ 14 ] * tmp2 );
		float tmp7 = -( m[ 12 ] * tmp4 + m[ 13 ] * tmp5 + m[ 14 ] * tmp6 );
		out[ 14 ] = -( m[ 12 ] * tmp8 + m[ 13 ] * tmp9 + m[ 14 ] * tmp10 );
		
		out[ 0 ] = tmp0;
		out[ 4 ] = tmp1;
		out[ 8 ] = tmp2;
		out[ 12 ] = tmp3;
		out[ 1 ] = tmp4;
		out[ 5 ] = tmp5;
		out[ 9 ] = tmp6;
		out[ 13 ] = tmp7;
		out[ 2 ] = tmp8;
		out[ 6 ] = tmp9;
		out[ 10 ] = tmp10;
		out[ 3 ] = out[ 7 ] = out[ 11 ] = 0f;
		out[ 15 ] = 1f;
	}
	
	public static void invAffineToTranspose3x3( float[ ] m , float[ ] out )
	{
		float determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		float s = ( m[ 0 ] * m[ 0 ] + m[ 4 ] * m[ 4 ] +
				m[ 8 ] * m[ 8 ] + m[ 12 ] * m[ 12 ] ) *
				( m[ 1 ] * m[ 1 ] + m[ 5 ] * m[ 5 ] +
						m[ 9 ] * m[ 9 ] + m[ 13 ] * m[ 13 ] ) *
				( m[ 2 ] * m[ 2 ] + m[ 6 ] * m[ 6 ] +
						m[ 10 ] * m[ 10 ] + m[ 14 ] * m[ 14 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		float tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) * s;
		float tmp1 = -( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) * s;
		float tmp2 = ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] ) * s;
		float tmp4 = -( m[ 1 ] * m[ 10 ] - m[ 2 ] * m[ 9 ] ) * s;
		float tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 2 ] * m[ 8 ] ) * s;
		float tmp6 = -( m[ 0 ] * m[ 9 ] - m[ 1 ] * m[ 8 ] ) * s;
		float tmp8 = ( m[ 1 ] * m[ 6 ] - m[ 2 ] * m[ 5 ] ) * s;
		float tmp9 = -( m[ 0 ] * m[ 6 ] - m[ 2 ] * m[ 4 ] ) * s;
		float tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 1 ] * m[ 4 ] ) * s;
		
		out[ 0 ] = tmp0;
		out[ 3 ] = tmp4;
		out[ 6 ] = tmp8;
		out[ 1 ] = tmp1;
		out[ 4 ] = tmp5;
		out[ 7 ] = tmp9;
		out[ 2 ] = tmp2;
		out[ 5 ] = tmp6;
		out[ 8 ] = tmp10;
	}
	
	public static void invertGeneral( float[ ] mat )
	{
		invertGeneral( mat , mat );
	}
	
	/**
	 * General invert routine. Inverts t1 and places the result in "this". Note that this routine handles both the "this" version and the non-"this" version.
	 * 
	 * Also note that since this routine is slow anyway, we won't worry about allocating a little bit of garbage.
	 */
	public static void invertGeneral( float[ ] mat , float[ ] out )
	{
		float tmp[ ] = new float[ 16 ];
		int row_perm[ ] = new int[ 4 ];
		
		// Use LU decomposition and backsubstitution code specifically
		// for floating-point 4x4 matrices.
		
		// Copy source matrix to tmp
		System.arraycopy( mat , 0 , tmp , 0 , tmp.length );
		
		// Calculate LU decomposition: Is the matrix singular?
		if( !luDecomposition( tmp , row_perm ) )
		{
			// Matrix has no inverse
			throw new IllegalArgumentException( "Singular Matrix" );
		}
		
		// Perform back substitution on the identity matrix
		// luDecomposition will set rot[] & scales[] for use
		// in luBacksubstituation
		out[ 0 ] = 1f;
		out[ 1 ] = 0f;
		out[ 2 ] = 0f;
		out[ 3 ] = 0f;
		out[ 4 ] = 0f;
		out[ 5 ] = 1f;
		out[ 6 ] = 0f;
		out[ 7 ] = 0f;
		out[ 8 ] = 0f;
		out[ 9 ] = 0f;
		out[ 10 ] = 1f;
		out[ 11 ] = 0f;
		out[ 12 ] = 0f;
		out[ 13 ] = 0f;
		out[ 14 ] = 0f;
		out[ 15 ] = 1f;
		luBacksubstitution( tmp , row_perm , out );
	}
	
	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU decomposition of a row-wise permutation of itself. The input parameters are "matrix0"
	 * and "dimen". The array "matrix0" is also an output parameter. The vector "row_perm[4]" is an output parameter that contains the row permutations
	 * resulting from partial pivoting. The output parameter "even_row_xchg" is 1 when the number of row exchanges is even, or -1 otherwise. Assumes data type
	 * is always float.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	static boolean luDecomposition( float[ ] matrix0 ,
			int[ ] row_perm )
	{
		
		// Can't re-use this temporary since the method is static.
		float row_scale[ ] = new float[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			float big, temp;
			
			// For each row ...
			for( int i = 0 ; i < 4 ; i++ )
			{
				big = 0f;
				
				// For each column, find the largest element in the row
				for( int j = 0 ; j < 4 ; j++ )
				{
					temp = matrix0[ j * 4 + i ];
					temp = Math.abs( temp );
					if( temp > big )
					{
						big = temp;
					}
				}
				
				// Is the matrix singular?
				if( big == 0f )
				{
					return false;
				}
				row_scale[ i ] = 1f / big;
			}
		}
		
		{
			int j;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				float sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = ( 4 * j ) + i;
					sum = matrix0[ target ];
					k = i;
					p1 = i;
					p2 = 4 * j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1 += 4;
						p2++ ;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0f;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = ( 4 * j ) + i;
					sum = matrix0[ target ];
					k = j;
					p1 = i;
					p2 = 4 * j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1 += 4;
						p2++ ;
					}
					matrix0[ target ] = sum;
					
					// Is this the best pivot so far?
					if( ( temp = row_scale[ i ] * Math.abs( sum ) ) >= big )
					{
						big = temp;
						imax = i;
					}
				}
				
				if( imax < 0 )
				{
					return false;
				}
				
				// Is a row exchange necessary?
				if( j != imax )
				{
					// Yes: exchange rows
					k = 4;
					p1 = imax;
					p2 = j;
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1 ] = matrix0[ p2 ];
						matrix0[ p2 ] = temp;
						p1 += 4;
						p2 += 4;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( 4 * j ) + j ] == 0.0 )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1f / ( matrix0[ ( 4 * j ) + j ] );
					target = 4 * j + j + 1;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target++ ;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Solves a set of linear equations. The input parameters "matrix1", and "row_perm" come from luDecompostionD4x4 and do not change here. The parameter
	 * "matrix2" is a set of column vectors assembled into a 4x4 matrix of floating-point values. The procedure takes each column of "matrix2" in turn and
	 * treats it as the right-hand side of the matrix equation Ax = LUx = b. The solution vector replaces the original column of the matrix.
	 * 
	 * If "matrix2" is the identity matrix, the procedure replaces its contents with the inverse of the matrix from which "matrix1" was originally derived.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	static void luBacksubstitution( float[ ] matrix1 ,
			int[ ] row_perm ,
			float[ ] matrix2 )
	{
		
		int i, ii, ip, j, k;
		int rp;
		int cv, rv;
		
		// rp = row_perm;
		rp = 0;
		
		// For each column vector of matrix2 ...
		for( k = 0 ; k < 4 ; k++ )
		{
			// cv = &(matrix2[0][k]);
			cv = k;
			ii = -1;
			
			// Forward substitution
			for( i = 0 ; i < 4 ; i++ )
			{
				float sum;
				
				ip = row_perm[ rp + i ];
				sum = matrix2[ ip + 4 * cv ];
				matrix2[ ip + 4 * cv ] = matrix2[ i + 4 * cv ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j * 4 ] * matrix2[ j + 4 * cv ];
					}
				}
				else if( sum != 0f )
				{
					ii = i;
				}
				matrix2[ i + 4 * cv ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3;
			matrix2[ 3 + 4 * cv ] /= matrix1[ rv + 3 * 4 ];
			
			rv-- ;
			matrix2[ 2 + 4 * cv ] = ( matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 2 * 4 ];
			
			rv-- ;
			matrix2[ 1 + 4 * cv ] = ( matrix2[ 1 + 4 * cv ] -
					matrix1[ rv + 2 * 4 ] * matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 1 * 4 ];
			
			rv-- ;
			matrix2[ 0 + 4 * cv ] = ( matrix2[ 0 + 4 * cv ] -
					matrix1[ rv + 1 * 4 ] * matrix2[ 1 + 4 * cv ] -
					matrix1[ rv + 2 * 4 ] * matrix2[ 2 + 4 * cv ] -
					matrix1[ rv + 3 * 4 ] * matrix2[ 3 + 4 * cv ] ) / matrix1[ rv + 0 * 4 ];
		}
	}
	
	/**
	 * Helping function that specifies the position and orientation of a view matrix. The inverse of this transform can be used to control the ViewPlatform
	 * object within the scene graph.
	 * 
	 * @param eye
	 *        the location of the eye
	 * @param center
	 *        a point in the virtual world where the eye is looking
	 * @param up
	 *        an up vector specifying the frustum's up direction
	 */
	public static void lookAt( float[ ] mat , float eyex , float eyey , float eyez , float centerx , float centery , float centerz , float upx , float upy , float upz )
	{
		float forwardx, forwardy, forwardz, invMag;
		float sidex, sidey, sidez;
		
		forwardx = eyex - centerx;
		forwardy = eyey - centery;
		forwardz = eyez - centerz;
		
		invMag = 1f / ( float ) Math.sqrt( forwardx * forwardx + forwardy * forwardy + forwardz * forwardz );
		forwardx = forwardx * invMag;
		forwardy = forwardy * invMag;
		forwardz = forwardz * invMag;
		
		invMag = 1f / ( float ) Math.sqrt( upx * upx + upy * upy + upz * upz );
		upx *= invMag;
		upy *= invMag;
		upz *= invMag;
		
		// side = Up cross forward
		sidex = upy * forwardz - forwardy * upz;
		sidey = upz * forwardx - upx * forwardz;
		sidez = upx * forwardy - upy * forwardx;
		
		invMag = 1f / ( float ) Math.sqrt( sidex * sidex + sidey * sidey + sidez * sidez );
		sidex *= invMag;
		sidey *= invMag;
		sidez *= invMag;
		
		// recompute up = forward cross side
		
		upx = forwardy * sidez - sidey * forwardz;
		upy = forwardz * sidex - forwardx * sidez;
		upz = forwardx * sidey - forwardy * sidex;
		
		// transpose because we calculated the inverse of what we want
		mat[ 0 ] = sidex;
		mat[ 4 ] = sidey;
		mat[ 8 ] = sidez;
		
		mat[ 1 ] = upx;
		mat[ 5 ] = upy;
		mat[ 9 ] = upz;
		
		mat[ 2 ] = forwardx;
		mat[ 6 ] = forwardy;
		mat[ 10 ] = forwardz;
		
		mat[ 12 ] = -eyex * mat[ 0 ] + -eyey * mat[ 4 ] + -eyez * mat[ 8 ];
		mat[ 13 ] = -eyex * mat[ 1 ] + -eyey * mat[ 5 ] + -eyez * mat[ 9 ];
		mat[ 14 ] = -eyex * mat[ 2 ] + -eyey * mat[ 6 ] + -eyez * mat[ 10 ];
		
		mat[ 3 ] = mat[ 7 ] = mat[ 11 ] = 0;
		mat[ 15 ] = 1;
	}
	
	/**
	 * Creates a perspective projection transform that mimics a standard, camera-based, view-model. This transform maps coordinates from Eye Coordinates (EC) to
	 * Clipping Coordinates (CC). Note that the field of view is specified in radians.
	 * 
	 * @param fovx
	 *        specifies the field of view in the x direction, in radians
	 * @param aspect
	 *        specifies the aspect ratio and thus the field of view in the x direction. The aspect ratio is the ratio of x to y, or width to height.
	 * @param zNear
	 *        the distance to the frustum's near clipping plane. This value must be positive, (the value -zNear is the location of the near clip plane).
	 * @param zFar
	 *        the distance to the frustum's far clipping plane
	 */
	public static void perspective( float[ ] mat , float fovx , float aspect ,
			float zNear , float zFar )
	{
		float sine, cotangent, deltaZ;
		float half_fov = fovx * 0.5f;
		
		deltaZ = zFar - zNear;
		sine = ( float ) Math.sin( half_fov );
		cotangent = ( float ) Math.cos( half_fov ) / sine;
		
		mat[ 0 ] = cotangent;
		mat[ 5 ] = cotangent * aspect;
		mat[ 10 ] = -( zFar + zNear ) / deltaZ;
		mat[ 14 ] = -2f * zNear * zFar / deltaZ;
		mat[ 11 ] = -1f;
		mat[ 4 ] = mat[ 8 ] = mat[ 12 ] = mat[ 1 ] = mat[ 9 ] = mat[ 13 ] = mat[ 2 ] =
				mat[ 6 ] = mat[ 3 ] = mat[ 7 ] = mat[ 15 ] = 0;
	}
	
	public static void calcClippingPlanes( float[ ] mat , float[ ] btlrnf )
	{
		btlrnf[ 4 ] = -mat[ 14 ] / ( 1 - mat[ 10 ] );
		btlrnf[ 5 ] = mat[ 14 ] * btlrnf[ 4 ] / ( mat[ 14 ] + 2 * btlrnf[ 4 ] );
		btlrnf[ 0 ] = btlrnf[ 4 ] * ( mat[ 9 ] - 1 ) / mat[ 5 ];
		btlrnf[ 1 ] = 2 * btlrnf[ 4 ] / mat[ 5 ] + btlrnf[ 0 ];
		btlrnf[ 2 ] = btlrnf[ 4 ] * ( mat[ 8 ] - 1 ) / mat[ 0 ];
		btlrnf[ 3 ] = 2 * btlrnf[ 4 ] / mat[ 0 ] + btlrnf[ 2 ];
	}
	
	public static void ortho( float[ ] mat , float left , float right , float bottom , float top , float zNear , float zFar )
	{
		mat[ 0 ] = 2 / ( right - left );
		mat[ 12 ] = -( right + left ) / ( right - left );
		mat[ 5 ] = 2 / ( top - bottom );
		mat[ 13 ] = -( top + bottom ) / ( top - bottom );
		mat[ 10 ] = -2 / ( zFar - zNear );
		mat[ 14 ] = -( zFar + zNear ) / ( zFar - zNear );
		
		mat[ 15 ] = 1;
		mat[ 4 ] = mat[ 8 ] = mat[ 1 ] = mat[ 9 ] = mat[ 2 ] = mat[ 6 ] = mat[ 7 ] = mat[ 11 ] = 0;
	}
	
	private static boolean almostZero( float a )
	{
		return( ( a < EPSILON_ABSOLUTE ) && ( a > -EPSILON_ABSOLUTE ) );
	}
	
	public static boolean isZero( float[ ] v )
	{
		for( float f : v )
		{
			if( f != 0f )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isZero3( float[ ] v )
	{
		return v[ 0 ] == 0f && v[ 1 ] == 0f && v[ 2 ] == 0f;
	}
	
	/**
	 * Sets the rotational component (upper 3x3) of this transform to the matrix equivalent values of the axis-angle argument; the other elements of this
	 * transform are unchanged; any pre-existing scale in the transform is preserved.
	 * 
	 * @param a1
	 *        the axis-angle to be converted (x, y, z, angle)
	 */
	public static void setRotation( float[ ] mat , float x , float y , float z , float angle )
	{
		float mag = ( float ) Math.sqrt( x * x + y * y + z * z );
		
		if( almostZero( mag ) )
		{
			setIdentity( mat );
		}
		else
		{
			mag = 1f / mag;
			float ax = x * mag;
			float ay = y * mag;
			float az = z * mag;
			
			float sinTheta = ( float ) Math.sin( angle );
			float cosTheta = ( float ) Math.cos( angle );
			float t = 1f - cosTheta;
			
			float xz = ax * az;
			float xy = ax * ay;
			float yz = ay * az;
			
			mat[ 0 ] = t * ax * ax + cosTheta;
			mat[ 4 ] = t * xy - sinTheta * az;
			mat[ 8 ] = t * xz + sinTheta * ay;
			mat[ 12 ] = 0;
			
			mat[ 1 ] = t * xy + sinTheta * az;
			mat[ 5 ] = t * ay * ay + cosTheta;
			mat[ 9 ] = t * yz - sinTheta * ax;
			mat[ 13 ] = 0;
			
			mat[ 2 ] = t * xz - sinTheta * ay;
			mat[ 6 ] = t * yz + sinTheta * ax;
			mat[ 10 ] = t * az * az + cosTheta;
			mat[ 14 ] = 0;
			
			mat[ 3 ] = 0;
			mat[ 7 ] = 0;
			mat[ 11 ] = 0;
			mat[ 15 ] = 1;
		}
	}
	
	public static void setRotation( float[ ] mat , float[ ] axis , float angle )
	{
		setRotation( mat , axis[ 0 ] , axis[ 1 ] , axis[ 2 ] , angle );
	}
	
	public static void normalize( float[ ] v , int start , int count )
	{
		double factor = 0;
		for( int i = start ; i < start + count ; i++ )
		{
			factor += v[ i ] * v[ i ];
		}
		
		factor = 1.0 / Math.sqrt( factor );
		
		for( int i = start ; i < start + count ; i++ )
		{
			v[ i ] *= factor;
		}
	}
	
	public static void normalize3( float[ ] v )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		v[ 0 ] *= factor;
		v[ 1 ] *= factor;
		v[ 2 ] *= factor;
	}
	
	public static void normalize3( float x , float y , float z , float[ ] out )
	{
		double factor = 1.0 / Math.sqrt( x * x + y * y + z * z );
		out[ 0 ] = ( float ) ( x * factor );
		out[ 1 ] = ( float ) ( y * factor );
		out[ 2 ] = ( float ) ( z * factor );
	}
	
	public static void normalize2( float[ ] v , float[ ] out )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] );
		out[ 0 ] = ( float ) ( v[ 0 ] * factor );
		out[ 1 ] = ( float ) ( v[ 1 ] * factor );
	}
	
	public static void normalize3( float[ ] v , float[ ] out )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		out[ 0 ] = ( float ) ( v[ 0 ] * factor );
		out[ 1 ] = ( float ) ( v[ 1 ] * factor );
		out[ 2 ] = ( float ) ( v[ 2 ] * factor );
	}
	
	public static void normalize3( float[ ] v , int vi , float[ ] out , int outi )
	{
		double factor = 1.0 / Math.sqrt( v[ vi ] * v[ vi ] + v[ vi + 1 ] * v[ vi + 1 ] + v[ vi + 2 ] * v[ vi + 2 ] );
		out[ outi ] = ( float ) ( v[ vi ] * factor );
		out[ outi + 1 ] = ( float ) ( v[ vi + 1 ] * factor );
		out[ outi + 2 ] = ( float ) ( v[ vi + 2 ] * factor );
	}
	
	/**
	 * Projects 3-dimensional vector {@code a} onto vector {@code b}, storing the result in {@code out}.
	 */
	public static void vvproj3( float[ ] a , float[ ] b , float[ ] out )
	{
		float aDotB = dot3( a , b );
		float bDotB = dot3( b , b );
		float x = b[ 0 ] * aDotB / bDotB;
		float y = b[ 1 ] * aDotB / bDotB;
		float z = b[ 2 ] * aDotB / bDotB;
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	/**
	 * Projects the vector from an {@code origin} to a {@code point} onto another vector, storing the result in {@code out}.
	 * 
	 * @param point
	 * @param origin
	 * @param vector
	 * @param out
	 */
	public static void projPointOntoVector3( float[ ] point , float[ ] origin , float[ ] vector , float[ ] out )
	{
		float aDotB = subDot3( point , origin , vector );
		float bDotB = dot3( vector , vector );
		float x = vector[ 0 ] * aDotB / bDotB;
		float y = vector[ 1 ] * aDotB / bDotB;
		float z = vector[ 2 ] * aDotB / bDotB;
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	/**
	 * Projects 3-dimensional vector {@code a} onto a plane with normal {@code n}, storing the result in {@code out}.
	 */
	public static void vpproj3( float[ ] a , float[ ] n , float[ ] out )
	{
		float aDotN = dot3( a , n );
		float nDotN = dot3( n , n );
		float x = a[ 0 ] - n[ 0 ] * aDotN / nDotN;
		float y = a[ 1 ] - n[ 1 ] * aDotN / nDotN;
		float z = a[ 2 ] - n[ 2 ] * aDotN / nDotN;
		out[ 0 ] = x;
		out[ 1 ] = y;
		out[ 2 ] = z;
	}
	
	public static void add3( float[ ] a , float[ ] b , float[ ] out )
	{
		out[ 0 ] = a[ 0 ] + b[ 0 ];
		out[ 1 ] = a[ 1 ] + b[ 1 ];
		out[ 2 ] = a[ 2 ] + b[ 2 ];
	}
	
	public static void add3( float[ ] a , int ai , float[ ] b , int bi , float[ ] out , int outi )
	{
		out[ outi + 0 ] = a[ ai + 0 ] + b[ bi + 0 ];
		out[ outi + 1 ] = a[ ai + 1 ] + b[ bi + 1 ];
		out[ outi + 2 ] = a[ ai + 2 ] + b[ bi + 2 ];
	}
	
	public static void sub3( float[ ] a , float[ ] b , float[ ] out )
	{
		out[ 0 ] = a[ 0 ] - b[ 0 ];
		out[ 1 ] = a[ 1 ] - b[ 1 ];
		out[ 2 ] = a[ 2 ] - b[ 2 ];
	}
	
	public static void sub3( float[ ] a , int ai , float[ ] b , int bi , float[ ] out , int outi )
	{
		out[ outi + 0 ] = a[ ai + 0 ] - b[ bi + 0 ];
		out[ outi + 1 ] = a[ ai + 1 ] - b[ bi + 1 ];
		out[ outi + 2 ] = a[ ai + 2 ] - b[ bi + 2 ];
	}
	
	public static void scale3( float[ ] a , float f )
	{
		scale3( a , f , a );
	}
	
	public static void scale3( float[ ] a , float f , float[ ] out )
	{
		out[ 0 ] = a[ 0 ] * f;
		out[ 1 ] = a[ 1 ] * f;
		out[ 2 ] = a[ 2 ] * f;
	}
	
	/**
	 * Computes out = a * b + c
	 */
	public static void scaleAdd3( float a , float[ ] b , float[ ] c , float[ ] out )
	{
		out[ 0 ] = a * b[ 0 ] + c[ 0 ];
		out[ 1 ] = a * b[ 1 ] + c[ 1 ];
		out[ 2 ] = a * b[ 2 ] + c[ 2 ];
	}
	
	/**
	 * Computes out = a * b + c
	 */
	public static void scaleAdd3( float a , float[ ] b , int bi , float[ ] c , int ci , float[ ] out , int outi )
	{
		out[ outi + 0 ] = a * b[ bi + 0 ] + c[ ci + 0 ];
		out[ outi + 1 ] = a * b[ bi + 1 ] + c[ ci + 1 ];
		out[ outi + 2 ] = a * b[ bi + 2 ] + c[ ci + 2 ];
	}
	
	public static void interp( float[ ] a , float[ ] b , float f , float[ ] out )
	{
		for( int i = 0 ; i < a.length ; i++ )
		{
			out[ i ] = ( 1 - f ) * a[ i ] + f * b[ i ];
		}
	}
	
	public static void interp3( float[ ] a , float[ ] b , float f , float[ ] out )
	{
		out[ 0 ] = ( 1 - f ) * a[ 0 ] + f * b[ 0 ];
		out[ 1 ] = ( 1 - f ) * a[ 1 ] + f * b[ 1 ];
		out[ 2 ] = ( 1 - f ) * a[ 2 ] + f * b[ 2 ];
	}
	
	public static void interp3( double[ ] a , double[ ] b , double f , double[ ] out )
	{
		out[ 0 ] = ( 1 - f ) * a[ 0 ] + f * b[ 0 ];
		out[ 1 ] = ( 1 - f ) * a[ 1 ] + f * b[ 1 ];
		out[ 2 ] = ( 1 - f ) * a[ 2 ] + f * b[ 2 ];
	}
	
	public static void interp3( double[ ] a , double[ ] b , double f , float[ ] out , int outi )
	{
		out[ outi + 0 ] = ( float ) ( ( 1 - f ) * a[ 0 ] + f * b[ 0 ] );
		out[ outi + 1 ] = ( float ) ( ( 1 - f ) * a[ 1 ] + f * b[ 1 ] );
		out[ outi + 2 ] = ( float ) ( ( 1 - f ) * a[ 2 ] + f * b[ 2 ] );
	}
	
	public static float length( float[ ] v , int start , int count )
	{
		float total = 0;
		for( int i = start ; i < count ; i++ )
		{
			total += v[ i ] * v[ i ];
		}
		return ( float ) Math.sqrt( total );
	}
	
	public static float partLength( float[ ] v , int ... dims )
	{
		float total = 0;
		for( int dim : dims )
		{
			total += v[ dim ] * v[ dim ];
		}
		return ( float ) Math.sqrt( total );
	}
	
	public static double partLength( double[ ] v , int ... dims )
	{
		double total = 0;
		for( int dim : dims )
		{
			total += v[ dim ] * v[ dim ];
		}
		return Math.sqrt( total );
	}
	
	public static float length3( float[ ] v )
	{
		return ( float ) Math.sqrt( dot3( v , v ) );
	}
	
	/**
	 * Computes out = -v.
	 */
	public static void negate3( float[ ] v , float[ ] out )
	{
		out[ 0 ] = -v[ 0 ];
		out[ 1 ] = -v[ 1 ];
		out[ 2 ] = -v[ 2 ];
	}
	
	/**
	 * Computes out = -v.
	 */
	public static void negate3( float[ ] v , int vi , float[ ] out , int outi )
	{
		out[ outi + 0 ] = -v[ vi + 0 ];
		out[ outi + 1 ] = -v[ vi + 1 ];
		out[ outi + 2 ] = -v[ vi + 2 ];
	}
	
	public static void negate3( float[ ] v )
	{
		v[ 0 ] = -v[ 0 ];
		v[ 1 ] = -v[ 1 ];
		v[ 2 ] = -v[ 2 ];
	}
	
	public static void negate3( float[ ] v , int vi )
	{
		v[ vi + 0 ] = -v[ vi + 0 ];
		v[ vi + 1 ] = -v[ vi + 1 ];
		v[ vi + 2 ] = -v[ vi + 2 ];
	}
	
	public static boolean epsilonEquals( float[ ] a , float[ ] b , float epsilon )
	{
		for( int i = 0 ; i < a.length ; i++ )
		{
			float diff = a[ i ] - b[ i ];
			if( Float.isNaN( diff ) )
			{
				return false;
			}
			if( Math.abs( diff ) > epsilon )
			{
				return false;
			}
		}
		return true;
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////
	// MIXED DOUBLE/FLOAT METHODS ////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////
	
	public static void setf( double[ ] a , float ... b )
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			a[ i ] = b[ i ];
		}
	}
	
	public static void setd( float[ ] a , double ... b )
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			a[ i ] = ( float ) b[ i ];
		}
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double[ ] v )
	{
		m[ rowIndex ] = ( float ) ( float ) v[ 0 ];
		m[ rowIndex + 4 ] = ( float ) v[ 1 ];
		m[ rowIndex + 8 ] = ( float ) v[ 2 ];
		m[ rowIndex + 12 ] = ( float ) v[ 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double[ ] v , int vi )
	{
		m[ rowIndex ] = ( float ) v[ vi + 0 ];
		m[ rowIndex + 4 ] = ( float ) v[ vi + 1 ];
		m[ rowIndex + 8 ] = ( float ) v[ vi + 2 ];
		m[ rowIndex + 12 ] = ( float ) v[ vi + 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double a , double b , double c , double d )
	{
		m[ rowIndex ] = ( float ) a;
		m[ rowIndex + 4 ] = ( float ) b;
		m[ rowIndex + 8 ] = ( float ) c;
		m[ rowIndex + 12 ] = ( float ) d;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , double a , double b , double c )
	{
		colIndex *= 4;
		m[ colIndex ] = ( float ) a;
		m[ colIndex + 1 ] = ( float ) b;
		m[ colIndex + 2 ] = ( float ) c;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = ( float ) v[ 0 ];
		m[ colIndex + 1 ] = ( float ) v[ 1 ];
		m[ colIndex + 2 ] = ( float ) v[ 2 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double[ ] v )
	{
		colIndex *= 4;
		m[ colIndex ] = ( float ) v[ 0 ];
		m[ colIndex + 1 ] = ( float ) v[ 1 ];
		m[ colIndex + 2 ] = ( float ) v[ 2 ];
		m[ colIndex + 3 ] = ( float ) v[ 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double[ ] v , int vi )
	{
		colIndex *= 4;
		m[ colIndex ] = ( float ) v[ vi + 0 ];
		m[ colIndex + 1 ] = ( float ) v[ vi + 1 ];
		m[ colIndex + 2 ] = ( float ) v[ vi + 2 ];
		m[ colIndex + 3 ] = ( float ) v[ vi + 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double a , double b , double c , double d )
	{
		colIndex *= 4;
		m[ colIndex ] = ( float ) a;
		m[ colIndex + 1 ] = ( float ) b;
		m[ colIndex + 2 ] = ( float ) c;
		m[ colIndex + 3 ] = ( float ) d;
	}
	
	public static String matToString( float[ ] m , String elemFormat )
	{
		StringBuilder sb = new StringBuilder( );
		for( int row = 0 ; row < 4 ; row++ )
		{
			for( int col = 0 ; col < 4 ; col++ )
			{
				if( col > 0 )
				{
					sb.append( ' ' );
				}
				sb.append( String.format( elemFormat , m[ row + col * 4 ] ) );
			}
			sb.append( '\n' );
		}
		return sb.toString( );
	}
	
	public static String matToString( float[ ] m )
	{
		return matToString( m , "%12.4f" );
	}
	
	public static float[ ] toFloats( double[ ] doubles )
	{
		float[ ] result = new float[ doubles.length ];
		for( int i = 0 ; i < doubles.length ; i++ )
		{
			result[ i ] = ( float ) doubles[ i ];
		}
		return result;
	}
	
	public static boolean hasNaNsOrInfinites( double ... doubles )
	{
		for( double d : doubles )
		{
			if( Double.isNaN( d ) || Double.isInfinite( d ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasNaNsOrInfinites( float ... floats )
	{
		for( float f : floats )
		{
			if( Float.isNaN( f ) || Float.isInfinite( f ) )
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Performs gaussian elimination on the m by n matrix A. This method is faster than {@link #gauss(float[], int, int)} because it doesn't actually swap
	 * rows. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the reduced matrix. Row <code>i</code> of the reduced matrix is
	 * row <code>row_perms[ i ]</code> of A.
	 */
	public static void gauss( float[ ] A , int m , int n , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < m && j < n )
		{
			int maxi = i;
			float maxpivot = A[ row_perms[ i ] + j * m ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				float newpivot = A[ row_perms[ k ] + j * m ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ row_perms[ i ] + k * m ] /= maxpivot;
				}
				
				// subtract row i from the rows below
				for( int u = i + 1 ; u < m ; u++ )
				{
					float multiplier = A[ row_perms[ u ] + j * m ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ row_perms[ u ] + k * m ] -= multiplier * A[ row_perms[ i ] + k * m ];
					}
				}
				i += 1;
			}
			j += 1;
		}
	}
	
	/**
	 * Performs gaussian elimination on the m by n matrix A. This method is faster than {@link #gauss(float[], int, int)} because it doesn't actually swap
	 * rows. Instead of exchanging rows, row_perms is used to mark the positions of the rows in the reduced matrix. Row <code>i</code> of the reduced matrix is
	 * row <code>row_perms[ i ]</code> of A.
	 */
	public static void fullGauss( float[ ] A , int m , int n , int[ ] row_perms )
	{
		int i = 0;
		int j = 0;
		
		for( int k = 0 ; k < row_perms.length ; k++ )
		{
			row_perms[ k ] = k;
		}
		
		while( i < m && j < n )
		{
			int maxi = i;
			float maxpivot = A[ row_perms[ i ] + j * m ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				float newpivot = A[ row_perms[ k ] + j * m ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					int temp = row_perms[ i ];
					row_perms[ i ] = row_perms[ maxi ];
					row_perms[ maxi ] = temp;
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ row_perms[ i ] + k * m ] /= maxpivot;
				}
				
				// subtract row i from the other rows
				for( int u = 0 ; u < m ; u++ )
				{
					if( u == i )
					{
						continue;
					}
					float multiplier = A[ row_perms[ u ] + j * m ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ row_perms[ u ] + k * m ] -= multiplier * A[ row_perms[ i ] + k * m ];
					}
				}
				i += 1;
			}
			j += 1;
		}
	}
	
	public static void fullGauss( float[ ] A , int m , int n )
	{
		int i = 0;
		int j = 0;
		
		while( i < m && j < n )
		{
			int maxi = i;
			float maxpivot = A[ i + j * m ];
			
			// find the largest pivot in column j
			for( int k = i + 1 ; k < m ; k++ )
			{
				float newpivot = A[ k + j * m ];
				if( Math.abs( newpivot ) > Math.abs( maxpivot ) )
				{
					maxpivot = newpivot;
					maxi = k;
				}
			}
			if( maxpivot != 0 )
			{
				// swap the row with the largest pivot with row i
				if( i != maxi )
				{
					for( int k = j ; k < n ; k++ )
					{
						int km = k * m;
						float temp = A[ i + km ];
						A[ i + km ] = A[ maxi + km ];
						A[ maxi + km ] = temp;
					}
				}
				
				// divide row i by the pivot value
				for( int k = j ; k < n ; k++ )
				{
					A[ i + k * m ] /= maxpivot;
				}
				
				// subtract row i from the other rows
				for( int u = 0 ; u < m ; u++ )
				{
					if( u == i )
					{
						continue;
					}
					float multiplier = A[ u + j * m ];
					
					for( int k = j ; k < n ; k++ )
					{
						A[ u + k * m ] -= multiplier * A[ i + k * m ];
					}
				}
				i += 1;
			}
			j += 1;
		}
	}
	
	public static String prettyPrint( float[ ] a , int columns )
	{
		int intDigits = 0;
		int fracDigits = 0;
		
		for( float f : a )
		{
			if( f == 0f )
			{
				continue;
			}
			int log = ( int ) Math.floor( Math.log10( Math.abs( f ) ) );
			intDigits = Math.max( intDigits , log + 1 );
			fracDigits = Math.max( fracDigits , -log );
		}
		
		int totalChars = intDigits + fracDigits + 2;
		if( totalChars < 7 )
		{
			fracDigits += 7 - totalChars;
		}
		
		String elemFormat = String.format( "%%%d.%df" , intDigits + fracDigits + 2 , fracDigits );
		
		StringBuilder sb = new StringBuilder( );
		
		int nrows = a.length / columns;
		
		for( int row = 0 ; row < nrows ; row++ )
		{
			sb.append( row == 0 ? "[ " : "  " );
			for( int column = 0 ; column < columns ; column++ )
			{
				if( column > 0 )
				{
					sb.append( ' ' );
				}
				sb.append( String.format( elemFormat , a[ row + column * nrows ] ) );
			}
			sb.append( row == nrows - 1 ? " ]" : "  \n" );
		}
		
		return sb.toString( );
	}
	
	public static String prettyPrint( float[ ] a , int columns , int[ ] row_perms )
	{
		int intDigits = 0;
		int fracDigits = 0;
		
		for( float f : a )
		{
			if( f == 0f )
			{
				continue;
			}
			int log = ( int ) Math.floor( Math.log10( Math.abs( f ) ) );
			intDigits = Math.max( intDigits , log + 1 );
			fracDigits = Math.max( fracDigits , -log );
		}
		
		int totalChars = intDigits + fracDigits + 2;
		if( totalChars < 7 )
		{
			fracDigits += 7 - totalChars;
		}
		
		String elemFormat = String.format( "%%%d.%df" , intDigits + fracDigits + 2 , fracDigits );
		
		StringBuilder sb = new StringBuilder( );
		
		int nrows = a.length / columns;
		
		for( int row = 0 ; row < nrows ; row++ )
		{
			sb.append( row == 0 ? "[ " : "  " );
			for( int column = 0 ; column < columns ; column++ )
			{
				if( column > 0 )
				{
					sb.append( ' ' );
				}
				sb.append( String.format( elemFormat , a[ row_perms[ row ] + column * nrows ] ) );
			}
			sb.append( row == nrows - 1 ? " ]" : "  \n" );
		}
		
		return sb.toString( );
	}
	
	public static double rotation( double startAngle , double endAngle )
	{
		if( startAngle < endAngle )
		{
			double result = endAngle - startAngle;
			return result < Math.PI ? result : result - Math.PI * 2;
		}
		else
		{
			double result = endAngle - startAngle;
			return result > -Math.PI ? result : result + Math.PI * 2;
		}
	}
	
	public static float rotation( float startAngle , float endAngle )
	{
		if( startAngle < endAngle )
		{
			float result = endAngle - startAngle;
			return result < Math.PI ? result : ( float ) ( result - Math.PI * 2 );
		}
		else
		{
			float result = endAngle - startAngle;
			return result > -Math.PI ? result : ( float ) ( result + Math.PI * 2 );
		}
	}
	
	/**
	 * Computes out = vector[0] * basis[0] + vector[1] * basis[1] + ...
	 * out must != vector!
	 */
	public static void combine( float[ ] out , float[ ] vector , float[ ] ... basis )
	{
		Arrays.fill( out , 0f );
		for( int i = 0 ; i < vector.length ; i++ )
		{
			scaleAdd3( vector[ i ] , basis[ i ] , out , out );
		}
	}
	
	/**
	 * Computes the general solution of a linear system. An original algorithm by Andy Edwards!
	 * 
	 * @param A
	 *        a matrix that has been row-reduced by {@link #gauss(double[], int, int, int[])}
	 * @param m
	 *        the number of rows in A
	 * @param n
	 *        the number of columns in A
	 * @param row_perms
	 *        the row permuations of A from {@link #gauss(double[], int, int, int[])}
	 * @param aug
	 *        <code>true</code> if A is an augmented matrix
	 * @param soln
	 *        output parameter: coefficients for each variable in the system (and constants for non-homogeneous systems). E.g. for a 3 variable augmented
	 *        matrix with x3 free, the output will be of the form
	 * 
	 *        <pre>
	 * [  0,  0, a3, a0,
	 *    0,  0, b3, b0,
	 *    0,  0,  1,  0 ]
	 * </pre>
	 * 
	 *        Represents the solutions
	 * 
	 *        <pre>
	 * x1 = 0 * x1 + 0 * x2 + a3 * x3 + a0
	 * x2 = 0 * x1 + 0 * x2 + b3 * x3 + b0
	 * x3 = 0 * x1 + 0 * x2 +  1 * x3 +  0
	 * </pre>
	 * 
	 *        Only the free variables will have nonzero coefficients for themselves. For a 3 variable homogeneous matrix, the output would lack the
	 *        constants a0, b0, and c0.<br>
	 * 
	 *        The basis for the solution space will be the set of all nonzero "columns" of this output, excluding the last for augmented matrices which is
	 *        the offset from the origin.
	 * @param free
	 *        Output parameter: an array of booleans identifying which variables are free.
	 * @return <code>true</code> if the system was successfully solved, <code>false</code> if the system is inconsistent.
	 */
	public static boolean generalSolution( double[ ] A , int m , int n , int[ ] row_perms , boolean aug , double[ ] soln , boolean[ ] free )
	{
		int nvars = aug ? n - 1 : n;
		
		Arrays.fill( soln , 0 );
		Arrays.fill( free , true );
		
		// for all rows...
		for( int i = 0 ; i < m ; i++ )
		{
			int rowstart = row_perms != null ? row_perms[ i ] * n : i * n;
			
			// find pivot column/variable no. in row
			int j;
			for( j = i ; j < nvars ; j++ )
			{
				if( A[ rowstart + j ] != 0 )
				{
					break;
				}
			}
			
			if( j == nvars )
			{
				// check for inconsistent system
				if( aug && A[ rowstart + j ] != 0 )
				{
					return false;
				}
			}
			else
			{
				free[ j ] = false;
				
				// set the coefficents for the solution equation of the current pivot variable
				double pivot = A[ rowstart + j ];
				for( int k = j + 1 ; k < nvars ; k++ )
				{
					soln[ n * j + k ] = -A[ rowstart + k ] / pivot;
				}
				if( aug )
				{
					soln[ n * j + n - 1 ] = A[ rowstart + n - 1 ] / pivot;
				}
			}
		}
		
		double[ ] temp = new double[ n ];
		
		// for free variables, mark a coefficient of 1 for the variable itself in its
		// solution equation
		int var = 0;
		for( var = 0 ; var < nvars ; var++ )
		{
			if( free[ var ] )
			{
				int rowstart = var * n;
				soln[ rowstart + var ] = 1;
			}
		}
		
		for( var = nvars - 1 ; var >= 0 ; var-- )
		{
			if( !free[ var ] )
			{
				int rowstart = var * n;
				
				// substitute free variable equations into pivot variable equation
				
				Arrays.fill( temp , 0 );
				
				for( int j = var ; j < nvars ; j++ )
				{
					double coef = soln[ rowstart + j ];
					if( coef != 0 )
					{
						int rowstart2 = j * n;
						
						for( int k = j ; k < n ; k++ )
						{
							temp[ k ] += soln[ rowstart2 + k ] * coef;
						}
					}
				}
				
				System.arraycopy( temp , 0 , soln , rowstart , nvars );
				
				// handle constant coefficient
				if( aug )
				{
					soln[ rowstart + n - 1 ] += temp[ n - 1 ];
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Computes the outer product of a 3D vector with itself
	 * 
	 * @param a
	 *        a 3-coordinate vector
	 * @param out
	 *        a 3x3 matrix as a column-major array
	 */
	public static void selfOuterProduct3( float[ ] a , float[ ] out )
	{
		out[ 0 ] = a[ 0 ] * a[ 0 ];
		out[ 1 ] = a[ 1 ] * a[ 0 ];
		out[ 2 ] = a[ 2 ] * a[ 0 ];
		out[ 3 ] = out[ 1 ];
		out[ 4 ] = a[ 1 ] * a[ 1 ];
		out[ 5 ] = a[ 2 ] * a[ 1 ];
		out[ 6 ] = out[ 2 ];
		out[ 7 ] = out[ 5 ];
		out[ 8 ] = a[ 2 ] * a[ 2 ];
	}
	
	/**
	 * Multiples a 3x3 matrix by a column vector, outputting a column vector.
	 * 
	 * @param m
	 *        a 3x3 matrix as a column-major array
	 * @param v
	 *        a 3-coordinate vector
	 * @param out
	 *        the output, a 3-coordinate vector
	 */
	public static void mvmul3x3( float[ ] m , float[ ] v , float[ ] out )
	{
		out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 3 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
		out[ 1 ] = m[ 1 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 7 ] * v[ 2 ];
		out[ 2 ] = m[ 2 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
	}
	
	/**
	 * Computes v transpose * m * v, where m is a 3x3 matrix and v is a column vector.
	 * 
	 * @param m
	 *        a 3x3 matrix as a column-major array
	 * @param v
	 *        a 3-coordinate vector
	 */
	public static float conjugate3( float[ ] m , float[ ] v )
	{
		float x = m[ 0 ] * v[ 0 ] + m[ 3 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
		float y = m[ 1 ] * v[ 0 ] + m[ 4 ] * v[ 1 ] + m[ 7 ] * v[ 2 ];
		float z = m[ 2 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 8 ] * v[ 2 ];
		
		return v[ 0 ] * x + v[ 1 ] * y + v[ 2 ] * z;
	}
}
