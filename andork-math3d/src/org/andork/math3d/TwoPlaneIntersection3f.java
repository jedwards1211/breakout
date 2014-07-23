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

import java.util.Arrays;
import java.util.Random;

import javax.swing.RepaintManager;

import org.andork.util.Reparam;

import static org.andork.math3d.Vecmath.*;

public class TwoPlaneIntersection3f
{
	public final float[ ]	m	= new float[ 18 ];
	
	public float[ ]			s1	= new float[ 2 ];
	public float[ ]			t1	= new float[ 2 ];
	public float[ ]			s2	= new float[ 2 ];
	public float[ ]			t2	= new float[ 2 ];
	
	public ResultType		intersectionType;
	
	public enum ResultType
	{
		NOT_TESTED , NO_INTERSECTION , LINEAR_INTERSECTION , PLANAR_INTERSECTION , INVALID_INPUT;
	}
	
	public void plane1FromPoints( float[ ] p0 , float[ ] p1 , float[ ] p2 )
	{
		sub3( p1 , 0 , p0 , 0 , m , 0 );
		sub3( p2 , 0 , p0 , 0 , m , 3 );
		set3( m , 12 , p0 , 0 );
		intersectionType = ResultType.NOT_TESTED;
	}
	
	public void plane2FromPoints( float[ ] p0 , float[ ] p1 , float[ ] p2 )
	{
		// note this is the reverse of plane1FromPoints
		sub3( p0 , 0 , p1 , 0 , m , 6 );
		sub3( p0 , 0 , p2 , 0 , m , 9 );
		negate3( p0 , 0 , m , 15 );
		intersectionType = ResultType.NOT_TESTED;
	}
	
	public void twoTriangleIntersection( )
	{
		Vecmath.fullGauss( m , 3 , 6 );
		
		if( m[ 0 ] == 0 || m[ 4 ] == 0 )
		{
			intersectionType = ResultType.INVALID_INPUT;
		}
		
		if( m[ 8 ] == 0 && m[ 11 ] == 0 )
		{
			if( m[ 14 ] + m[ 17 ] == 0 )
			{
				intersectionType = ResultType.PLANAR_INTERSECTION;
			}
			else
			{
				intersectionType = ResultType.NO_INTERSECTION;
			}
			return;
		}
		// at least one s1, t1 should be dependent upon t2
		if( m[ 6 ] == 0 && m[ 7 ] == 0 && m[ 9 ] == 0 && m[ 10 ] == 0 )
		{
			intersectionType = ResultType.INVALID_INPUT;
			return;
		}
		
		boolean s2independent = m[ 8 ] == 0;
		
		if( s2independent )
		{
			swapCols( m , 3 , 2 , 3 );
		}
		
		// t2 >= 0
		t2[ 0 ] = 0;
		t2[ 1 ] = 1;
		
		// s1 >= 0
		if( -m[ 9 ] > 0 )
		{
			t2[ 0 ] = Math.max( t2[ 0 ] , ( m[ 12 ] + m[ 15 ] ) / -m[ 9 ] );
		}
		else if( -m[ 9 ] < 0 )
		{
			t2[ 1 ] = Math.min( t2[ 1 ] , ( m[ 12 ] + m[ 15 ] ) / -m[ 9 ] );
		}
		
		// t1 >= 0
		if( -m[ 10 ] > 0 )
		{
			t2[ 0 ] = Math.max( t2[ 0 ] , ( m[ 13 ] + m[ 16 ] ) / -m[ 10 ] );
		}
		else if( -m[ 10 ] < 0 )
		{
			t2[ 1 ] = Math.min( t2[ 1 ] , ( m[ 13 ] + m[ 16 ] ) / -m[ 10 ] );
		}
		
		// s1 + t1 <= 1
		if( -m[ 9 ] - m[ 10 ] > 0 )
		{
			t2[ 1 ] = Math.min( t2[ 1 ] , ( 1 + m[ 12 ] + m[ 13 ] + m[ 15 ] + m[ 16 ] ) / ( -m[ 9 ] - m[ 10 ] ) );
		}
		else if( -m[ 9 ] - m[ 10 ] < 0 )
		{
			t2[ 0 ] = Math.max( t2[ 0 ] , ( 1 + m[ 12 ] + m[ 13 ] + m[ 15 ] + m[ 16 ] ) / ( -m[ 9 ] - m[ 10 ] ) );
		}
		
		// s2 >= 0
		if( -m[ 11 ] > 0 )
		{
			t2[ 0 ] = Math.max( t2[ 0 ] , ( m[ 14 ] + m[ 17 ] ) / -m[ 11 ] );
		}
		else if( -m[ 11 ] < 0 )
		{
			t2[ 1 ] = Math.min( t2[ 1 ] , ( m[ 14 ] + m[ 17 ] ) / -m[ 11 ] );
		}
		
		// s2 + t2 <= 1
		if( 1 - m[ 11 ] > 0 )
		{
			t2[ 1 ] = Math.min( t2[ 1 ] , ( 1 + m[ 14 ] + m[ 17 ] ) / ( 1 - m[ 11 ] ) );
		}
		else if( 1 - m[ 11 ] < 0 )
		{
			t2[ 0 ] = Math.max( t2[ 0 ] , ( 1 + m[ 14 ] + m[ 17 ] ) / ( 1 - m[ 11 ] ) );
		}
		
		if( t2[ 0 ] > t2[ 1 ] )
		{
			intersectionType = ResultType.NO_INTERSECTION;
			return;
		}
		
		intersectionType = ResultType.LINEAR_INTERSECTION;
		
		s2[ 0 ] = -m[ 11 ] * t2[ 0 ] - m[ 14 ] - m[ 17 ];
		s2[ 1 ] = -m[ 11 ] * t2[ 1 ] - m[ 14 ] - m[ 17 ];
		
		if( s2independent )
		{
			swapCols( m , 3 , 2 , 3 );
			float[ ] swap = s2;
			s2 = t2;
			t2 = swap;
		}
	}
	
	public void calc_s1_and_t1( )
	{
		s1[ 0 ] = -m[ 9 ] * t2[ 0 ] - m[ 12 ] - m[ 15 ];
		s1[ 1 ] = -m[ 9 ] * t2[ 1 ] - m[ 12 ] - m[ 15 ];
		t1[ 0 ] = -m[ 10 ] * t2[ 0 ] - m[ 13 ] - m[ 16 ];
		t1[ 1 ] = -m[ 10 ] * t2[ 1 ] - m[ 13 ] - m[ 16 ];
	}
	
	public void calcIntersectionPoint( float t2value , float[ ] out )
	{
		float s2value = Reparam.linear( t2value , t2[ 0 ] , t2[ 1 ] , s2[ 0 ] , s2[ 1 ] );
		out[ 0 ] = -s2value * m[ 6 ] - t2value * m[ 9 ] - m[ 15 ];
		out[ 1 ] = -s2value * m[ 7 ] - t2value * m[ 10 ] - m[ 16 ];
		out[ 2 ] = -s2value * m[ 8 ] - t2value * m[ 11 ] - m[ 17 ];
	}
	
	private static void swapCols( float[ ] m , int rowCount , int col1 , int col2 )
	{
		for( int row = 0 ; row < rowCount ; row++ )
		{
			float swap = m[ row + col1 * rowCount ];
			m[ row + col1 * rowCount ] = m[ row + col2 * rowCount ];
			m[ row + col2 * rowCount ] = swap;
		}
	}
	
	public String toString( )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( getClass( ).getSimpleName( ) ).append( "[\n  " );
		sb.append( Vecmath.prettyPrint( m , 6 ).replaceAll( "\\n" , "\n  " ) );
		sb.append( "\n  intersectionType: " ).append( intersectionType );
		sb.append( "\n  s1: " ).append( Arrays.toString( s1 ) );
		sb.append( "\n  t1: " ).append( Arrays.toString( t1 ) );
		sb.append( "\n  s2: " ).append( Arrays.toString( s2 ) );
		sb.append( "\n  t2: " ).append( Arrays.toString( t2 ) );
		sb.append( "\n]" );
		return sb.toString( );
	}
	
	public static void main( String[ ] args )
	{
		Random rand = new Random( 2 );
		
		TwoPlaneIntersection3f tpx = new TwoPlaneIntersection3f( );
		
		tpx.plane1FromPoints(
				new float[ ] { 0 , 0 , 0 } ,
				new float[ ] { 1 , 0 , 0 } ,
				new float[ ] { 0 , 0 , -1 } );
		
		tpx.plane2FromPoints(
				new float[ ] { 0 , 1 , 0.5f } ,
				new float[ ] { 0.5f , 1 , -2 } ,
				new float[ ] { 0 , -1 , 0.5f } );
		
		System.out.println( tpx );
		tpx.twoTriangleIntersection( );
		System.out.println( tpx );
		
		tpx.plane1FromPoints(
				new float[ ] { 0 , 0 , 0 } ,
				new float[ ] { 1 , 0 , 0 } ,
				new float[ ] { 0 , 0 , -1 } );
		
		tpx.plane2FromPoints(
				new float[ ] { 1 , 0 , -0.5f } ,
				new float[ ] { 0 , 0 , -0.4f } ,
				new float[ ] { 0.9f , 0 , -1.5f } );
		
		System.out.println( tpx );
		tpx.twoTriangleIntersection( );
		System.out.println( tpx );
		tpx.plane1FromPoints(
				new float[ ] { 0 , 0 , 0 } ,
				new float[ ] { 1 , 0 , 0 } ,
				new float[ ] { 0 , 0 , -1 } );
		
		tpx.plane2FromPoints(
				new float[ ] { 1 , 1 , -0.5f } ,
				new float[ ] { 0 , 1 , -0.5f } ,
				new float[ ] { 1f , 1 , -1.5f } );
		
		System.out.println( tpx );
		tpx.twoTriangleIntersection( );
		System.out.println( tpx );
	}
}
