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

public class LineLineIntersection2d
{
	public final double[ ]	m	= new double[ 8 ];
	
	public double			t0	= Double.NaN;
	public double			t1	= Double.NaN;
	
	/**
	 * the intersection point.
	 */
	public final double[ ]	x	= new double[ 2 ];
	
	public void setUp( double[ ] lo0 , double[ ] lt0 , double[ ] lo1 , double[ ] lt1 )
	{
		m[ 0 ] = lt0[ 0 ];
		m[ 1 ] = lt0[ 1 ];
		m[ 2 ] = -lt1[ 0 ];
		m[ 3 ] = -lt1[ 1 ];
		m[ 4 ] = lo0[ 0 ];
		m[ 5 ] = lo0[ 1 ];
		m[ 6 ] = -lo1[ 0 ];
		m[ 7 ] = -lo1[ 1 ];
	}
	
	/**
	 * Sets the lines to intersect. This method allows you to take lines that are defined in 2 or more dimensions
	 * and pick 2 of those dimensions to perform the intersection in.
	 * 
	 * @param lo0
	 *        a point on the first line.
	 * @param lt0
	 *        the direction of the first line.
	 * @param lo1
	 *        a point on the second line.
	 * @param lt1
	 *        the direction of the second line.
	 * @param da
	 *        the index of the first dimension to use.
	 * @param db
	 *        the index of the second dimension to use.
	 */
	public void setUp( double[ ] lo0 , double[ ] lt0 , double[ ] lo1 , double[ ] lt1 , int da , int db )
	{
		m[ 0 ] = lt0[ da ];
		m[ 1 ] = lt0[ db ];
		m[ 2 ] = -lt1[ da ];
		m[ 3 ] = -lt1[ db ];
		m[ 4 ] = lo0[ da ];
		m[ 5 ] = lo0[ db ];
		m[ 6 ] = -lo1[ da ];
		m[ 7 ] = -lo1[ db ];
	}
	
	public void setUp( double lo0x , double lo0y , double lt0x , double lt0y , double lo1x , double lo1y , double lt1x , double lt1y )
	{
		m[ 0 ] = lt0x;
		m[ 1 ] = lt0y;
		m[ 2 ] = -lt1x;
		m[ 3 ] = -lt1y;
		m[ 4 ] = lo0x;
		m[ 5 ] = lo0y;
		m[ 6 ] = -lo1x;
		m[ 7 ] = -lo1y;
	}
	
	public boolean findIntersection( )
	{
		double lt0x = m[ 0 ];
		double lt0y = m[ 1 ];
		double lo0x = m[ 4 ];
		double lo0y = m[ 5 ];
		
		if( Math.abs( m[ 0 ] ) < Math.abs( m[ 1 ] ) )
		{
			for( int i = 0 ; i < 8 ; i += 2 )
			{
				double swap = m[ i ];
				m[ i ] = m[ i + 1 ];
				m[ i + 1 ] = swap;
			}
		}
		
		if( m[ 0 ] == 0 )
		{
			return false;
		}
		
		if( m[ 1 ] != 0 )
		{
			double multiplier = -m[ 1 ] / m[ 0 ];
			m[ 1 ] += m[ 0 ] * multiplier;
			m[ 3 ] += m[ 2 ] * multiplier;
			m[ 5 ] += m[ 4 ] * multiplier;
			m[ 7 ] += m[ 6 ] * multiplier;
		}
		
		if( m[ 3 ] == 0 )
		{
			return false;
		}
		
		t1 = ( -m[ 5 ] - m[ 7 ] ) / m[ 3 ];
		t0 = ( -m[ 2 ] * t1 - m[ 4 ] - m[ 6 ] ) / m[ 0 ];
		
		x[ 0 ] = lo0x + t0 * lt0x;
		x[ 1 ] = lo0y + t0 * lt0y;
		return true;
	}
}
