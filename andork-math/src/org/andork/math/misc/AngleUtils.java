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
	
	public static double angle( double a , double b )
	{
		double result = Math.abs( a - b ) % ( Math.PI * 2.0 );
		return result > Math.PI ? Math.PI * 2.0 - result : result;
	}
}
