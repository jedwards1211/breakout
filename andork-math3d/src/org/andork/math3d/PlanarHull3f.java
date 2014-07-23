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

import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.subDot3;

/**
 * Represents a planar hull and provides methods to test if it contains or intersects bounding boxes and points.
 * For view volume hulls, there are canonical orders and properties:<br>
 * <br>
 * <b>origins</b> and <b>normals</b> (and <b>planeDists</b>):
 * 
 * <pre>
 * [0] left side
 * [1] right side
 * [2] bottom side
 * [3] top side
 * [4] near side (origin must be in the center of the side)
 * [5] far side (origin must be in the center of the side)
 * </pre>
 * 
 * <b>vertices</b>:
 * 
 * <pre>
 * [0] left bottom near corner
 * [1] right bottom near corner
 * [2] left top near corner
 * [3] right top near corner
 * [4] left bottom far corner
 * [5] right bottom far corner
 * [6] left top far corner
 * [7] right top far corner
 * </pre>
 * 
 * <b>triangleIndices / triangleSides</b> (use {@link #setCanonicalTriangleIndicesAndPlanes()}):
 * 
 * <pre>
 * [0] { 0, 6, 4 } / 0
 * [1] { 6, 0, 2 } / 0
 * [2] { 7, 1, 5 } / 1
 * [3] { 1, 7, 3 } / 1
 * [4] { 0, 5, 1 } / 2
 * [5] { 5, 0, 4 } / 2
 * [6] { 7, 2, 3 } / 3
 * [7] { 2, 7, 6 } / 3
 * [8] { 0, 3, 2 } / 4
 * [9] { 3, 0, 1 } / 4
 * [10] { 7, 4, 6 } / 5
 * [11] { 4, 7, 5 } / 5
 * </pre>
 * 
 * The triangle planes should always be in ascending order, and the triangle indices should always be in counterclockwise
 * order when viewed from outside the hull (in case the hull needs to be rendered for debugging purposes).
 * 
 * @author Andy
 */
public class PlanarHull3f
{
	public final float[ ][ ]	vertices;
	public final float[ ][ ]	origins;
	public final float[ ][ ]	normals;
	public final float[ ]		planeDists;
	public final int[ ][ ]		triangleIndices;
	public final int[ ]			triangleSides;
	
	public PlanarHull3f( )
	{
		this( 6 , 8 , 12 );
	}
	
	public PlanarHull3f( int numSides , int numVertices , int numTriangles )
	{
		vertices = new float[ numVertices ][ 3 ];
		origins = new float[ numSides ][ 3 ];
		normals = new float[ numSides ][ 3 ];
		planeDists = new float[ numSides ];
		triangleIndices = new int[ numTriangles ][ 3 ];
		triangleSides = new int[ numTriangles ];
	}
	
	public void setCanonicalTriangleIndicesAndPlanes( )
	{
		set( triangleIndices[ 0 ] , 0 , 6 , 4 );
		set( triangleIndices[ 1 ] , 6 , 0 , 2 );
		set( triangleIndices[ 2 ] , 7 , 1 , 5 );
		set( triangleIndices[ 3 ] , 1 , 7 , 3 );
		set( triangleIndices[ 4 ] , 0 , 5 , 1 );
		set( triangleIndices[ 5 ] , 5 , 0 , 4 );
		set( triangleIndices[ 6 ] , 7 , 2 , 3 );
		set( triangleIndices[ 7 ] , 2 , 7 , 6 );
		set( triangleIndices[ 8 ] , 0 , 3 , 2 );
		set( triangleIndices[ 9 ] , 3 , 0 , 1 );
		set( triangleIndices[ 10 ] , 7 , 4 , 6 );
		set( triangleIndices[ 11 ] , 4 , 7 , 5 );
		
		for( int i = 0 ; i < 12 ; i++ )
		{
			triangleSides[ i ] = i / 2;
		}
	}
	
	private static void set( int[ ] array , int a , int b , int c )
	{
		array[ 0 ] = a;
		array[ 1 ] = b;
		array[ 2 ] = c;
	}
	
	public void calcPlaneDs( )
	{
		for( int side = 0 ; side < origins.length ; side++ )
		{
			planeDists[ side ] = -dot3( normals[ side ] , origins[ side ] );
		}
	}
	
	public boolean containsPoint( float[ ] p )
	{
		return containsPoint( p[ 0 ] , p[ 1 ] , p[ 2 ] );
	}
	
	public boolean containsPoint( float x , float y , float z )
	{
		for( int side = 0 ; side < origins.length ; side++ )
		{
			if( subDot3( x , y , z , origins[ side ] , normals[ side ] ) < 0f )
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean containsBox( float[ ] box )
	{
		for( int xd = 0 ; xd <= 3 ; xd += 3 )
		{
			float x = box[ xd ];
			for( int yd = 1 ; yd <= 4 ; yd += 3 )
			{
				float y = box[ yd ];
				for( int zd = 2 ; zd <= 5 ; zd += 3 )
				{
					float z = box[ zd ];
					if( !containsPoint( x , y , z ) )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean intersectsBox( float[ ] box )
	{
		for( int side = 0 ; side < origins.length ; side++ )
		{
			if( allPointsOutside( side , box ) )
			{
				return false;
			}
		}
		
		for( int dim = 0 ; dim < 3 ; dim++ )
		{
			boolean allOutside = true;
			for( float[ ] vertex : vertices )
			{
				if( vertex[ dim ] >= box[ dim ] )
				{
					allOutside = false;
					break;
				}
			}
			if( allOutside )
			{
				return false;
			}
			
			allOutside = true;
			for( float[ ] vertex : vertices )
			{
				if( vertex[ dim ] <= box[ dim + 3 ] )
				{
					allOutside = false;
					break;
				}
			}
			if( allOutside )
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean allPointsOutside( int side , float[ ] box )
	{
		for( int xd = 0 ; xd <= 3 ; xd += 3 )
		{
			float x = box[ xd ];
			for( int yd = 1 ; yd <= 4 ; yd += 3 )
			{
				float y = box[ yd ];
				for( int zd = 2 ; zd <= 5 ; zd += 3 )
				{
					float z = box[ zd ];
					if( subDot3( x , y , z , origins[ side ] , normals[ side ] ) >= 0 )
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
