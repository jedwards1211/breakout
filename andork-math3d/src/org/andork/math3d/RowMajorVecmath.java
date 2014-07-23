/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.math3d;

public class RowMajorVecmath
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
	
	public static double distance3( double[ ] a , int ai , double[ ] b , int bi )
	{
		double dx = a[ ai ] - b[ bi ];
		double dy = a[ ai + 1 ] - b[ bi + 1 ];
		double dz = a[ ai + 2 ] - b[ bi + 2 ];
		
		return ( double ) Math.sqrt( dx * dx + dy * dy + dz * dz );
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
	
	public static void cross( double[ ] a , double x , double y , double z , double[ ] out )
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
		double rw = 1 / ( m[ 12 ] * p[ 0 ] + m[ 13 ] * p[ 1 ] + m[ 14 ] * p[ 2 ] + m[ 15 ] );
		double x = rw * ( m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ] );
		double y = rw * ( m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ] );
		p[ 2 ] = rw * ( m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ] );
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmul( double[ ] m , double[ ] p , double[ ] out )
	{
		if( p != out )
		{
			double rw = 1 / ( m[ 12 ] * p[ 0 ] + m[ 13 ] * p[ 1 ] + m[ 14 ] * p[ 2 ] + m[ 15 ] );
			out[ 0 ] = rw * ( m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ] );
			out[ 1 ] = rw * ( m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ] );
			out[ 2 ] = rw * ( m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ] );
		}
		else
		{
			mpmul( m , p );
		}
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p )
	{
		double x = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
		double y = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
		p[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , int vi )
	{
		double x = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
		double y = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
		p[ vi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		p[ vi + 1 ] = y;
		p[ vi ] = x;
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , double[ ] out )
	{
		if( p != out )
		{
			out[ 0 ] = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
			out[ 1 ] = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
			out[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p );
		}
	}
	
	public static void mpmulAffine( double[ ] m , double x , double y , double z , double[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 1 ] * y + m[ 2 ] * z + m[ 3 ];
		out[ 1 ] = m[ 4 ] * x + m[ 5 ] * y + m[ 6 ] * z + m[ 7 ];
		out[ 2 ] = m[ 8 ] * x + m[ 9 ] * y + m[ 10 ] * z + m[ 11 ];
	}
	
	public static void mpmulAffine( double[ ] m , double[ ] p , int vi , double[ ] out , int outi )
	{
		if( p != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
			out[ outi + 1 ] = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
			out[ outi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p , vi );
		}
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v )
	{
		double x = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
		double y = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
		v[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		v[ 1 ] = y;
		v[ 0 ] = x;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , int vi )
	{
		double x = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
		double y = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
		v[ vi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		v[ vi + 1 ] = y;
		v[ vi ] = x;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , double[ ] out )
	{
		if( v != out )
		{
			out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
			out[ 1 ] = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
			out[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		}
		else
		{
			mvmulAffine( m , v );
		}
	}
	
	public static void mvmulAffine( double[ ] m , double x , double y , double z , double[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 1 ] * y + m[ 2 ] * z;
		out[ 1 ] = m[ 4 ] * x + m[ 5 ] * y + m[ 6 ] * z;
		out[ 2 ] = m[ 8 ] * x + m[ 9 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( double[ ] m , double[ ] v , int vi , double[ ] out , int outi )
	{
		if( v != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
			out[ outi + 1 ] = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
			out[ outi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
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
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			double m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			double m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			double m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			double m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			double m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			double m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			double m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			double m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			double m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			double m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			double m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			double m30 = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			double m31 = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			double m32 = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			double m33 = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
			out[ 12 ] = m30;
			out[ 13 ] = m31;
			out[ 14 ] = m32;
			out[ 15 ] = m33;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			out[ 12 ] = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			out[ 13 ] = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			out[ 14 ] = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			out[ 15 ] = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
		}
	}
	
	public static void mmulAffine( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			double m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			double m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			double m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			double m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			double m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			double m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			double m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			double m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			double m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			double m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			double m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
		}
	}
	
	public static void mmulRotational( double[ ] ma , double[ ] mb , double[ ] out )
	{
		if( out == ma || out == mb )
		{
			double m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			double m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			double m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			
			double m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			double m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			double m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			
			double m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			double m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			double m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
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
	
	public static void setIdentity( double[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
		
		m[ 12 ] = 0;
		m[ 13 ] = 0;
		m[ 14 ] = 0;
		m[ 15 ] = 1;
	}
	
	public static void setIdentityAffine( double[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double[ ] v )
	{
		rowIndex *= 4;
		m[ rowIndex ] = v[ 0 ];
		m[ rowIndex + 1 ] = v[ 1 ];
		m[ rowIndex + 2 ] = v[ 2 ];
		m[ rowIndex + 3 ] = v[ 3 ];
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double[ ] v , int vi )
	{
		rowIndex *= 4;
		m[ rowIndex ] = v[ vi + 0 ];
		m[ rowIndex + 1 ] = v[ vi + 1 ];
		m[ rowIndex + 2 ] = v[ vi + 2 ];
		m[ rowIndex + 3 ] = v[ vi + 3 ];
	}
	
	public static void setRow4( double[ ] m , int rowIndex , double a , double b , double c , double d )
	{
		rowIndex *= 4;
		m[ rowIndex ] = a;
		m[ rowIndex + 1 ] = b;
		m[ rowIndex + 2 ] = c;
		m[ rowIndex + 3 ] = d;
	}
	
	public static void setColumn3( double[ ] m , int colIndex , double a , double b , double c )
	{
		m[ colIndex ] = a;
		m[ colIndex + 4 ] = b;
		m[ colIndex + 8 ] = c;
	}
	
	public static void setColumn3( double[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 4 ] = v[ 1 ];
		m[ colIndex + 8 ] = v[ 2 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 4 ] = v[ 1 ];
		m[ colIndex + 8 ] = v[ 2 ];
		m[ colIndex + 12 ] = v[ 3 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double[ ] v , int vi )
	{
		m[ colIndex ] = v[ vi + 0 ];
		m[ colIndex + 4 ] = v[ vi + 1 ];
		m[ colIndex + 8 ] = v[ vi + 2 ];
		m[ colIndex + 12 ] = v[ vi + 3 ];
	}
	
	public static void setColumn4( double[ ] m , int colIndex , double a , double b , double c , double d )
	{
		m[ colIndex ] = a;
		m[ colIndex + 4 ] = b;
		m[ colIndex + 8 ] = c;
		m[ colIndex + 12 ] = d;
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
	 *            the angle to rotate about the X axis in radians
	 */
	public static void rotX( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = 1f;
		mat[ 1 ] = 0f;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = -sinAngle;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = sinAngle;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the y axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Y axis in radians
	 */
	public static void rotY( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = 0f;
		mat[ 2 ] = sinAngle;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = 1f;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = -sinAngle;
		mat[ 9 ] = 0f;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the z axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Z axis in radians
	 */
	public static void rotZ( double[ ] mat , double angle )
	{
		double sinAngle = ( double ) Math.sin( angle );
		double cosAngle = ( double ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = -sinAngle;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = sinAngle;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = 0f;
		mat[ 10 ] = 1f;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
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
	 *            a 16-element double array.
	 * @param out
	 *            a 9-element double array.
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
	
	public static void mcopyAffine( double[ ] msrc , double[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 12 );
	}
	
	public static double detAffine( double[ ] m )
	{
		return m[ 0 ] * ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) -
				m[ 1 ] * ( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) +
				m[ 2 ] * ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] );
	}
	
	public static void invAffine( double[ ] m , double[ ] out )
	{
		double determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		double s = ( m[ 0 ] * m[ 0 ] + m[ 1 ] * m[ 1 ] +
				m[ 2 ] * m[ 2 ] + m[ 3 ] * m[ 3 ] ) *
				( m[ 4 ] * m[ 4 ] + m[ 5 ] * m[ 5 ] +
						m[ 6 ] * m[ 6 ] + m[ 7 ] * m[ 7 ] ) *
				( m[ 8 ] * m[ 8 ] + m[ 9 ] * m[ 9 ] +
						m[ 10 ] * m[ 10 ] + m[ 11 ] * m[ 11 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		double tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) * s;
		double tmp1 = -( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) * s;
		double tmp2 = ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] ) * s;
		double tmp4 = -( m[ 4 ] * m[ 10 ] - m[ 8 ] * m[ 6 ] ) * s;
		double tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 8 ] * m[ 2 ] ) * s;
		double tmp6 = -( m[ 0 ] * m[ 6 ] - m[ 4 ] * m[ 2 ] ) * s;
		double tmp8 = ( m[ 4 ] * m[ 9 ] - m[ 8 ] * m[ 5 ] ) * s;
		double tmp9 = -( m[ 0 ] * m[ 9 ] - m[ 8 ] * m[ 1 ] ) * s;
		double tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 4 ] * m[ 1 ] ) * s;
		double tmp3 = -( m[ 3 ] * tmp0 + m[ 7 ] * tmp1 + m[ 11 ] * tmp2 );
		double tmp7 = -( m[ 3 ] * tmp4 + m[ 7 ] * tmp5 + m[ 11 ] * tmp6 );
		out[ 11 ] = -( m[ 3 ] * tmp8 + m[ 7 ] * tmp9 + m[ 11 ] * tmp10 );
		
		out[ 0 ] = tmp0;
		out[ 1 ] = tmp1;
		out[ 2 ] = tmp2;
		out[ 3 ] = tmp3;
		out[ 4 ] = tmp4;
		out[ 5 ] = tmp5;
		out[ 6 ] = tmp6;
		out[ 7 ] = tmp7;
		out[ 8 ] = tmp8;
		out[ 9 ] = tmp9;
		out[ 10 ] = tmp10;
		out[ 12 ] = out[ 13 ] = out[ 14 ] = 0f;
		out[ 15 ] = 1f;
	}
	
	public static void invAffineToTranspose3x3( double[ ] m , double[ ] out )
	{
		double determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		double s = ( m[ 0 ] * m[ 0 ] + m[ 1 ] * m[ 1 ] +
				m[ 2 ] * m[ 2 ] + m[ 3 ] * m[ 3 ] ) *
				( m[ 4 ] * m[ 4 ] + m[ 5 ] * m[ 5 ] +
						m[ 6 ] * m[ 6 ] + m[ 7 ] * m[ 7 ] ) *
				( m[ 8 ] * m[ 8 ] + m[ 9 ] * m[ 9 ] +
						m[ 10 ] * m[ 10 ] + m[ 11 ] * m[ 11 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		double tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) * s;
		double tmp1 = -( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) * s;
		double tmp2 = ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] ) * s;
		double tmp4 = -( m[ 4 ] * m[ 10 ] - m[ 8 ] * m[ 6 ] ) * s;
		double tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 8 ] * m[ 2 ] ) * s;
		double tmp6 = -( m[ 0 ] * m[ 6 ] - m[ 4 ] * m[ 2 ] ) * s;
		double tmp8 = ( m[ 4 ] * m[ 9 ] - m[ 8 ] * m[ 5 ] ) * s;
		double tmp9 = -( m[ 0 ] * m[ 9 ] - m[ 8 ] * m[ 1 ] ) * s;
		double tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 4 ] * m[ 1 ] ) * s;
		
		out[ 0 ] = tmp0;
		out[ 1 ] = tmp4;
		out[ 2 ] = tmp8;
		out[ 3 ] = tmp1;
		out[ 4 ] = tmp5;
		out[ 5 ] = tmp9;
		out[ 6 ] = tmp2;
		out[ 7 ] = tmp6;
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
		double tmp[] = new double[ 16 ];
		int row_perm[] = new int[ 4 ];
		
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
		double row_scale[] = new double[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			double big, temp;
			
			ptr = 0;
			rs = 0;
			
			// For each row ...
			i = 4;
			while( i-- != 0 )
			{
				big = 0f;
				
				// For each column, find the largest element in the row
				j = 4;
				while( j-- != 0 )
				{
					temp = matrix0[ ptr++ ];
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
				row_scale[ rs++ ] = 1f / big;
			}
		}
		
		{
			int j;
			int mtx;
			
			mtx = 0;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				double sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = i;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0f;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = j;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
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
					p1 = mtx + ( 4 * imax );
					p2 = mtx + ( 4 * j );
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1++ ] = matrix0[ p2 ];
						matrix0[ p2++ ] = temp;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( mtx + ( 4 * j ) + j ) ] == 0f )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1f / ( matrix0[ ( mtx + ( 4 * j ) + j ) ] );
					target = mtx + ( 4 * ( j + 1 ) ) + j;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target += 4;
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
				sum = matrix2[ cv + 4 * ip ];
				matrix2[ cv + 4 * ip ] = matrix2[ cv + 4 * i ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i * 4;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j ] * matrix2[ cv + 4 * j ];
					}
				}
				else if( sum != 0f )
				{
					ii = i;
				}
				matrix2[ cv + 4 * i ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3 * 4;
			matrix2[ cv + 4 * 3 ] /= matrix1[ rv + 3 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 2 ] = ( matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 2 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 1 ] = ( matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 1 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 0 ] = ( matrix2[ cv + 4 * 0 ] -
					matrix1[ rv + 1 ] * matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 0 ];
		}
	}
	
	/**
	 * Helping function that specifies the position and orientation of a view matrix. The inverse of this transform can be used to control the ViewPlatform
	 * object within the scene graph.
	 * 
	 * @param eye
	 *            the location of the eye
	 * @param center
	 *            a point in the virtual world where the eye is looking
	 * @param up
	 *            an up vector specifying the frustum's up direction
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
		mat[ 1 ] = sidey;
		mat[ 2 ] = sidez;
		
		mat[ 4 ] = upx;
		mat[ 5 ] = upy;
		mat[ 6 ] = upz;
		
		mat[ 8 ] = forwardx;
		mat[ 9 ] = forwardy;
		mat[ 10 ] = forwardz;
		
		mat[ 3 ] = -eyex * mat[ 0 ] + -eyey * mat[ 1 ] + -eyez * mat[ 2 ];
		mat[ 7 ] = -eyex * mat[ 4 ] + -eyey * mat[ 5 ] + -eyez * mat[ 6 ];
		mat[ 11 ] = -eyex * mat[ 8 ] + -eyey * mat[ 9 ] + -eyez * mat[ 10 ];
		
		mat[ 12 ] = mat[ 13 ] = mat[ 14 ] = 0;
		mat[ 15 ] = 1;
	}
	
	/**
	 * Creates a perspective projection transform that mimics a standard, camera-based, view-model. This transform maps coordinates from Eye Coordinates (EC) to
	 * Clipping Coordinates (CC). Note that unlike the similar function in OpenGL, the clipping coordinates generated by the resulting transform are in a
	 * right-handed coordinate system (as are all other coordinate systems in Java 3D). Also note that the field of view is specified in radians.
	 * 
	 * @param fovx
	 *            specifies the field of view in the x direction, in radians
	 * @param aspect
	 *            specifies the aspect ratio and thus the field of view in the x direction. The aspect ratio is the ratio of x to y, or width to height.
	 * @param zNear
	 *            the distance to the frustum's near clipping plane. This value must be positive, (the value -zNear is the location of the near clip plane).
	 * @param zFar
	 *            the distance to the frustum's far clipping plane
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
		mat[ 11 ] = 2f * zNear * zFar / deltaZ;
		mat[ 14 ] = -1f;
		mat[ 1 ] = mat[ 2 ] = mat[ 3 ] = mat[ 4 ] = mat[ 6 ] = mat[ 7 ] = mat[ 8 ] =
				mat[ 9 ] = mat[ 12 ] = mat[ 13 ] = mat[ 15 ] = 0;
	}
	
	public static void ortho( double[ ] mat , double left , double right , double bottom , double top , double zNear , double zFar )
	{
		mat[ 0 ] = 2 / ( right - left );
		mat[ 3 ] = 1 - mat[ 0 ] * right;
		mat[ 5 ] = 2 / ( top - bottom );
		mat[ 7 ] = 1 - mat[ 5 ] * top;
		mat[ 10 ] = 2 / ( zFar - zNear );
		mat[ 11 ] = 1 - mat[ 10 ] * zFar;
		
		mat[ 15 ] = 1;
		mat[ 1 ] = mat[ 2 ] = mat[ 4 ] = mat[ 6 ] = mat[ 8 ] = mat[ 9 ] = mat[ 13 ] = mat[ 14 ] = 0;
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
	 *            the axis-angle to be converted (x, y, z, angle)
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
			mat[ 1 ] = t * xy - sinTheta * az;
			mat[ 2 ] = t * xz + sinTheta * ay;
			mat[ 3 ] = 0;
			
			mat[ 4 ] = t * xy + sinTheta * az;
			mat[ 5 ] = t * ay * ay + cosTheta;
			mat[ 6 ] = t * yz - sinTheta * ax;
			mat[ 7 ] = 0;
			
			mat[ 8 ] = t * xz - sinTheta * ay;
			mat[ 9 ] = t * yz + sinTheta * ax;
			mat[ 10 ] = t * az * az + cosTheta;
			mat[ 11 ] = 0;
			
			mat[ 12 ] = 0;
			mat[ 13 ] = 0;
			mat[ 14 ] = 0;
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
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// FLOAT METHODS /////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	public static float distance3( float[ ] a , float[ ] b )
	{
		float dx = a[ 0 ] - b[ 0 ];
		float dy = a[ 1 ] - b[ 1 ];
		float dz = a[ 2 ] - b[ 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static float distance3( float[ ] a , int ai , float[ ] b , int bi )
	{
		float dx = a[ ai ] - b[ bi ];
		float dy = a[ ai + 1 ] - b[ bi + 1 ];
		float dz = a[ ai + 2 ] - b[ bi + 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
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
	
	public static float[ ] newMat4f( )
	{
		return new float[ ] { 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 0 , 1 };
	}
	
	public static void mpmul( float[ ] m , float[ ] p )
	{
		float rw = 1 / ( m[ 12 ] * p[ 0 ] + m[ 13 ] * p[ 1 ] + m[ 14 ] * p[ 2 ] + m[ 15 ] );
		float x = rw * ( m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ] );
		float y = rw * ( m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ] );
		p[ 2 ] = rw * ( m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ] );
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmul( float[ ] m , float[ ] p , float[ ] out )
	{
		if( p != out )
		{
			float rw = 1 / ( m[ 12 ] * p[ 0 ] + m[ 13 ] * p[ 1 ] + m[ 14 ] * p[ 2 ] + m[ 15 ] );
			out[ 0 ] = rw * ( m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ] );
			out[ 1 ] = rw * ( m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ] );
			out[ 2 ] = rw * ( m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ] );
		}
		else
		{
			mpmul( m , p );
		}
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p )
	{
		float x = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
		float y = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
		p[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi )
	{
		float x = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
		float y = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
		p[ vi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		p[ vi + 1 ] = y;
		p[ vi ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , float[ ] out )
	{
		if( p != out )
		{
			out[ 0 ] = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
			out[ 1 ] = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
			out[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p );
		}
	}
	
	public static void mpmulAffine( float[ ] m , float x , float y , float z , float[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 1 ] * y + m[ 2 ] * z + m[ 3 ];
		out[ 1 ] = m[ 4 ] * x + m[ 5 ] * y + m[ 6 ] * z + m[ 7 ];
		out[ 2 ] = m[ 8 ] * x + m[ 9 ] * y + m[ 10 ] * z + m[ 11 ];
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi , float[ ] out , int outi )
	{
		if( p != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
			out[ outi + 1 ] = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
			out[ outi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p , vi );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v )
	{
		float x = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
		float y = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
		v[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		v[ 1 ] = y;
		v[ 0 ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi )
	{
		float x = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
		float y = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
		v[ vi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		v[ vi + 1 ] = y;
		v[ vi ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , float[ ] out )
	{
		if( v != out )
		{
			out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
			out[ 1 ] = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
			out[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		}
		else
		{
			mvmulAffine( m , v );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float x , float y , float z , float[ ] out )
	{
		out[ 0 ] = m[ 0 ] * x + m[ 1 ] * y + m[ 2 ] * z;
		out[ 1 ] = m[ 4 ] * x + m[ 5 ] * y + m[ 6 ] * z;
		out[ 2 ] = m[ 8 ] * x + m[ 9 ] * y + m[ 10 ] * z;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi , float[ ] out , int outi )
	{
		if( v != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
			out[ outi + 1 ] = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
			out[ outi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
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
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			float m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			float m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			float m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			float m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			float m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			float m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			float m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			float m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			float m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			float m30 = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			float m31 = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			float m32 = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			float m33 = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
			out[ 12 ] = m30;
			out[ 13 ] = m31;
			out[ 14 ] = m32;
			out[ 15 ] = m33;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			out[ 12 ] = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			out[ 13 ] = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			out[ 14 ] = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			out[ 15 ] = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
		}
	}
	
	public static void mmulAffine( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			float m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			float m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			float m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			float m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			float m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			float m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			float m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			float m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			float m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
		}
	}
	
	public static void mmulRotational( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			
			float m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			float m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			float m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			
			float m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			float m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			float m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
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
		System.arraycopy( values , 0 , array , 0 , values.length );
	}
	
	public static void setIdentity( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
		
		m[ 12 ] = 0;
		m[ 13 ] = 0;
		m[ 14 ] = 0;
		m[ 15 ] = 1;
	}
	
	public static void setIdentityAffine( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float[ ] v )
	{
		rowIndex *= 4;
		m[ rowIndex ] = v[ 0 ];
		m[ rowIndex + 1 ] = v[ 1 ];
		m[ rowIndex + 2 ] = v[ 2 ];
		m[ rowIndex + 3 ] = v[ 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float[ ] v , int vi )
	{
		rowIndex *= 4;
		m[ rowIndex ] = v[ vi + 0 ];
		m[ rowIndex + 1 ] = v[ vi + 1 ];
		m[ rowIndex + 2 ] = v[ vi + 2 ];
		m[ rowIndex + 3 ] = v[ vi + 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , float a , float b , float c , float d )
	{
		rowIndex *= 4;
		m[ rowIndex ] = a;
		m[ rowIndex + 1 ] = b;
		m[ rowIndex + 2 ] = c;
		m[ rowIndex + 3 ] = d;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , float a , float b , float c )
	{
		m[ colIndex ] = a;
		m[ colIndex + 4 ] = b;
		m[ colIndex + 8 ] = c;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , float[ ] v )
	{
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 4 ] = v[ 1 ];
		m[ colIndex + 8 ] = v[ 2 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float[ ] v )
	{
		m[ colIndex ] = v[ 0 ];
		m[ colIndex + 4 ] = v[ 1 ];
		m[ colIndex + 8 ] = v[ 2 ];
		m[ colIndex + 12 ] = v[ 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float[ ] v , int vi )
	{
		m[ colIndex ] = v[ vi + 0 ];
		m[ colIndex + 4 ] = v[ vi + 1 ];
		m[ colIndex + 8 ] = v[ vi + 2 ];
		m[ colIndex + 12 ] = v[ vi + 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , float a , float b , float c , float d )
	{
		m[ colIndex ] = a;
		m[ colIndex + 4 ] = b;
		m[ colIndex + 8 ] = c;
		m[ colIndex + 12 ] = d;
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
	 *            the angle to rotate about the X axis in radians
	 */
	public static void rotX( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = 1f;
		mat[ 1 ] = 0f;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = -sinAngle;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = sinAngle;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the y axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Y axis in radians
	 */
	public static void rotY( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = 0f;
		mat[ 2 ] = sinAngle;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = 1f;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = -sinAngle;
		mat[ 9 ] = 0f;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the z axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Z axis in radians
	 */
	public static void rotZ( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = -sinAngle;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = sinAngle;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = 0f;
		mat[ 10 ] = 1f;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	public static void transpose( float[ ] m , float[ ] out )
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
			float t = m[ 1 ];
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
	 *            a 16-element float array.
	 * @param out
	 *            a 9-element float array.
	 */
	public static void transposeTo3x3( float[ ] mat , float[ ] out )
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
		return m[ 0 ] * ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) -
				m[ 1 ] * ( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) +
				m[ 2 ] * ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] );
	}
	
	public static void invAffine( float[ ] m , float[ ] out )
	{
		float determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		float s = ( m[ 0 ] * m[ 0 ] + m[ 1 ] * m[ 1 ] +
				m[ 2 ] * m[ 2 ] + m[ 3 ] * m[ 3 ] ) *
				( m[ 4 ] * m[ 4 ] + m[ 5 ] * m[ 5 ] +
						m[ 6 ] * m[ 6 ] + m[ 7 ] * m[ 7 ] ) *
				( m[ 8 ] * m[ 8 ] + m[ 9 ] * m[ 9 ] +
						m[ 10 ] * m[ 10 ] + m[ 11 ] * m[ 11 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		float tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) * s;
		float tmp1 = -( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) * s;
		float tmp2 = ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] ) * s;
		float tmp4 = -( m[ 4 ] * m[ 10 ] - m[ 8 ] * m[ 6 ] ) * s;
		float tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 8 ] * m[ 2 ] ) * s;
		float tmp6 = -( m[ 0 ] * m[ 6 ] - m[ 4 ] * m[ 2 ] ) * s;
		float tmp8 = ( m[ 4 ] * m[ 9 ] - m[ 8 ] * m[ 5 ] ) * s;
		float tmp9 = -( m[ 0 ] * m[ 9 ] - m[ 8 ] * m[ 1 ] ) * s;
		float tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 4 ] * m[ 1 ] ) * s;
		float tmp3 = -( m[ 3 ] * tmp0 + m[ 7 ] * tmp1 + m[ 11 ] * tmp2 );
		float tmp7 = -( m[ 3 ] * tmp4 + m[ 7 ] * tmp5 + m[ 11 ] * tmp6 );
		out[ 11 ] = -( m[ 3 ] * tmp8 + m[ 7 ] * tmp9 + m[ 11 ] * tmp10 );
		
		out[ 0 ] = tmp0;
		out[ 1 ] = tmp1;
		out[ 2 ] = tmp2;
		out[ 3 ] = tmp3;
		out[ 4 ] = tmp4;
		out[ 5 ] = tmp5;
		out[ 6 ] = tmp6;
		out[ 7 ] = tmp7;
		out[ 8 ] = tmp8;
		out[ 9 ] = tmp9;
		out[ 10 ] = tmp10;
		out[ 12 ] = out[ 13 ] = out[ 14 ] = 0f;
		out[ 15 ] = 1f;
	}
	
	public static void invAffineToTranspose3x3( float[ ] m , float[ ] out )
	{
		float determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		float s = ( m[ 0 ] * m[ 0 ] + m[ 1 ] * m[ 1 ] +
				m[ 2 ] * m[ 2 ] + m[ 3 ] * m[ 3 ] ) *
				( m[ 4 ] * m[ 4 ] + m[ 5 ] * m[ 5 ] +
						m[ 6 ] * m[ 6 ] + m[ 7 ] * m[ 7 ] ) *
				( m[ 8 ] * m[ 8 ] + m[ 9 ] * m[ 9 ] +
						m[ 10 ] * m[ 10 ] + m[ 11 ] * m[ 11 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		float tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) * s;
		float tmp1 = -( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) * s;
		float tmp2 = ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] ) * s;
		float tmp4 = -( m[ 4 ] * m[ 10 ] - m[ 8 ] * m[ 6 ] ) * s;
		float tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 8 ] * m[ 2 ] ) * s;
		float tmp6 = -( m[ 0 ] * m[ 6 ] - m[ 4 ] * m[ 2 ] ) * s;
		float tmp8 = ( m[ 4 ] * m[ 9 ] - m[ 8 ] * m[ 5 ] ) * s;
		float tmp9 = -( m[ 0 ] * m[ 9 ] - m[ 8 ] * m[ 1 ] ) * s;
		float tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 4 ] * m[ 1 ] ) * s;
		
		out[ 0 ] = tmp0;
		out[ 1 ] = tmp4;
		out[ 2 ] = tmp8;
		out[ 3 ] = tmp1;
		out[ 4 ] = tmp5;
		out[ 5 ] = tmp9;
		out[ 6 ] = tmp2;
		out[ 7 ] = tmp6;
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
		float tmp[] = new float[ 16 ];
		int row_perm[] = new int[ 4 ];
		
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
		float row_scale[] = new float[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			float big, temp;
			
			ptr = 0;
			rs = 0;
			
			// For each row ...
			i = 4;
			while( i-- != 0 )
			{
				big = 0f;
				
				// For each column, find the largest element in the row
				j = 4;
				while( j-- != 0 )
				{
					temp = matrix0[ ptr++ ];
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
				row_scale[ rs++ ] = 1f / big;
			}
		}
		
		{
			int j;
			int mtx;
			
			mtx = 0;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				float sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = i;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0f;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = j;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
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
					p1 = mtx + ( 4 * imax );
					p2 = mtx + ( 4 * j );
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1++ ] = matrix0[ p2 ];
						matrix0[ p2++ ] = temp;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( mtx + ( 4 * j ) + j ) ] == 0f )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1f / ( matrix0[ ( mtx + ( 4 * j ) + j ) ] );
					target = mtx + ( 4 * ( j + 1 ) ) + j;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target += 4;
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
				sum = matrix2[ cv + 4 * ip ];
				matrix2[ cv + 4 * ip ] = matrix2[ cv + 4 * i ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i * 4;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j ] * matrix2[ cv + 4 * j ];
					}
				}
				else if( sum != 0f )
				{
					ii = i;
				}
				matrix2[ cv + 4 * i ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3 * 4;
			matrix2[ cv + 4 * 3 ] /= matrix1[ rv + 3 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 2 ] = ( matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 2 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 1 ] = ( matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 1 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 0 ] = ( matrix2[ cv + 4 * 0 ] -
					matrix1[ rv + 1 ] * matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 0 ];
		}
	}
	
	/**
	 * Helping function that specifies the position and orientation of a view matrix. The inverse of this transform can be used to control the ViewPlatform
	 * object within the scene graph.
	 * 
	 * @param eye
	 *            the location of the eye
	 * @param center
	 *            a point in the virtual world where the eye is looking
	 * @param up
	 *            an up vector specifying the frustum's up direction
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
		mat[ 1 ] = sidey;
		mat[ 2 ] = sidez;
		
		mat[ 4 ] = upx;
		mat[ 5 ] = upy;
		mat[ 6 ] = upz;
		
		mat[ 8 ] = forwardx;
		mat[ 9 ] = forwardy;
		mat[ 10 ] = forwardz;
		
		mat[ 3 ] = -eyex * mat[ 0 ] + -eyey * mat[ 1 ] + -eyez * mat[ 2 ];
		mat[ 7 ] = -eyex * mat[ 4 ] + -eyey * mat[ 5 ] + -eyez * mat[ 6 ];
		mat[ 11 ] = -eyex * mat[ 8 ] + -eyey * mat[ 9 ] + -eyez * mat[ 10 ];
		
		mat[ 12 ] = mat[ 13 ] = mat[ 14 ] = 0;
		mat[ 15 ] = 1;
	}
	
	/**
	 * Creates a perspective projection transform that mimics a standard, camera-based, view-model. This transform maps coordinates from Eye Coordinates (EC) to
	 * Clipping Coordinates (CC). Note that unlike the similar function in OpenGL, the clipping coordinates generated by the resulting transform are in a
	 * right-handed coordinate system (as are all other coordinate systems in Java 3D). Also note that the field of view is specified in radians.
	 * 
	 * @param fovx
	 *            specifies the field of view in the x direction, in radians
	 * @param aspect
	 *            specifies the aspect ratio and thus the field of view in the x direction. The aspect ratio is the ratio of x to y, or width to height.
	 * @param zNear
	 *            the distance to the frustum's near clipping plane. This value must be positive, (the value -zNear is the location of the near clip plane).
	 * @param zFar
	 *            the distance to the frustum's far clipping plane
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
		mat[ 10 ] = ( zFar + zNear ) / deltaZ;
		mat[ 11 ] = 2f * zNear * zFar / deltaZ;
		mat[ 14 ] = -1f;
		mat[ 1 ] = mat[ 2 ] = mat[ 3 ] = mat[ 4 ] = mat[ 6 ] = mat[ 7 ] = mat[ 8 ] =
				mat[ 9 ] = mat[ 12 ] = mat[ 13 ] = mat[ 15 ] = 0;
	}
	
	public static void ortho( float[ ] mat , float left , float right , float bottom , float top , float zNear , float zFar )
	{
		mat[ 0 ] = 2 / ( right - left );
		mat[ 3 ] = 1 - mat[ 0 ] * right;
		mat[ 5 ] = 2 / ( top - bottom );
		mat[ 7 ] = 1 - mat[ 5 ] * top;
		mat[ 10 ] = 2 / ( zFar - zNear );
		mat[ 11 ] = 1 - mat[ 10 ] * zFar;
		
		mat[ 15 ] = 1;
		mat[ 1 ] = mat[ 2 ] = mat[ 4 ] = mat[ 6 ] = mat[ 8 ] = mat[ 9 ] = mat[ 13 ] = mat[ 14 ] = 0;
	}
	
	private static boolean almostZero( float a )
	{
		return( ( a < EPSILON_ABSOLUTE ) && ( a > -EPSILON_ABSOLUTE ) );
	}
	
	/**
	 * Sets the rotational component (upper 3x3) of this transform to the matrix equivalent values of the axis-angle argument; the other elements of this
	 * transform are unchanged; any pre-existing scale in the transform is preserved.
	 * 
	 * @param a1
	 *            the axis-angle to be converted (x, y, z, angle)
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
			mat[ 1 ] = t * xy - sinTheta * az;
			mat[ 2 ] = t * xz + sinTheta * ay;
			mat[ 3 ] = 0;
			
			mat[ 4 ] = t * xy + sinTheta * az;
			mat[ 5 ] = t * ay * ay + cosTheta;
			mat[ 6 ] = t * yz - sinTheta * ax;
			mat[ 7 ] = 0;
			
			mat[ 8 ] = t * xz - sinTheta * ay;
			mat[ 9 ] = t * yz + sinTheta * ax;
			mat[ 10 ] = t * az * az + cosTheta;
			mat[ 11 ] = 0;
			
			mat[ 12 ] = 0;
			mat[ 13 ] = 0;
			mat[ 14 ] = 0;
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
	
	public static void normalize3( float[ ] v , float[ ] out )
	{
		double factor = 1.0 / Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 1 ] * v[ 1 ] + v[ 2 ] * v[ 2 ] );
		out[ 0 ] = ( float ) ( v[ 0 ] * factor );
		out[ 1 ] = ( float ) ( v[ 1 ] * factor );
		out[ 2 ] = ( float ) ( v[ 2 ] * factor );
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
	
	public static float length( float[ ] v , int start , int count )
	{
		float total = 0;
		for( int i = start ; i < count ; i++ )
		{
			total += v[ i ] * v[ i ];
		}
		return ( float ) Math.sqrt( total );
	}
	
	public static float length3( float[ ] v )
	{
		return ( float ) Math.sqrt( dot3( v , v ) );
	}
	
	public static void negate3( float[ ] v )
	{
		v[ 0 ] = -v[ 0 ];
		v[ 1 ] = -v[ 1 ];
		v[ 2 ] = -v[ 2 ];
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
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// MIXED DOUBLE/FLOAT METHODS //////////////////////////////////////////////////////////// 
	//////////////////////////////////////////////////////////////////////////////////////////

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
		rowIndex *= 4;
		m[ rowIndex ] = (float)(float)v[ 0 ];
		m[ rowIndex + 1 ] = (float)v[ 1 ];
		m[ rowIndex + 2 ] = (float)v[ 2 ];
		m[ rowIndex + 3 ] = (float)v[ 3 ];
	}

	public static void setRow4( float[ ] m , int rowIndex , double[ ] v , int vi )
	{
		rowIndex *= 4;
		m[ rowIndex ] = (float)v[ vi + 0 ];
		m[ rowIndex + 1 ] = (float)v[ vi + 1 ];
		m[ rowIndex + 2 ] = (float)v[ vi + 2 ];
		m[ rowIndex + 3 ] = (float)v[ vi + 3 ];
	}

	public static void setRow4( float[ ] m , int rowIndex , double a , double b , double c , double d )
	{
		rowIndex *= 4;
		m[ rowIndex ] = (float) a;
		m[ rowIndex + 1 ] = (float) b;
		m[ rowIndex + 2 ] = (float) c;
		m[ rowIndex + 3 ] = (float) d;
	}

	public static void setColumn3( float[ ] m , int colIndex , double a , double b , double c )
	{
		m[ colIndex ] = (float) a;
		m[ colIndex + 4 ] = (float) b;
		m[ colIndex + 8 ] = (float) c;
	}

	public static void setColumn3( float[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = (float)v[ 0 ];
		m[ colIndex + 4 ] = (float)v[ 1 ];
		m[ colIndex + 8 ] = (float)v[ 2 ];
	}

	public static void setColumn4( float[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = (float)v[ 0 ];
		m[ colIndex + 4 ] = (float)v[ 1 ];
		m[ colIndex + 8 ] = (float)v[ 2 ];
		m[ colIndex + 12 ] = (float)v[ 3 ];
	}

	public static void setColumn4( float[ ] m , int colIndex , double[ ] v , int vi )
	{
		m[ colIndex ] = (float)v[ vi + 0 ];
		m[ colIndex + 4 ] = (float)v[ vi + 1 ];
		m[ colIndex + 8 ] = (float)v[ vi + 2 ];
		m[ colIndex + 12 ] = ( float ) v[ vi + 3 ];
	}

	public static void setColumn4( float[ ] m , int colIndex , double a , double b , double c , double d )
	{
		m[ colIndex ] = (float) a;
		m[ colIndex + 4 ] = (float) b;
		m[ colIndex + 8 ] = (float) c;
		m[ colIndex + 12 ] = (float) d;
	}
}
