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
package org.andork.jogl.util;

import java.nio.ByteBuffer;

import static org.andork.math3d.Vecmath.*;

public class UVGenerator
{
	/**
	 * Generates UV vectors. This only works for indexed triangle geometries where every pair of triangles forms a quad and all pairs of triangles have the same
	 * relative orientation.
	 * 
	 * @param verts
	 *            the vertex buffer, where floats 0-3 are the coordinate, 4-6 are the normal, 7-9 should be set to the U vector, and 10-12 should be set to the
	 *            V vector.
	 * @param vertsStride
	 * @param indices
	 * @param quadCornerIndices
	 *            a 4-int array specifying the offsets of the quad corner indices within each group of 6 triangle vertex indices, in clockwise or
	 *            counterclockwise order.
	 */
	public static void generateUV3fi( ByteBuffer verts , int vertsStride , ByteBuffer indices , int[ ] quadCornerIndices )
	{
		generateUV3fi( verts , 0 , vertsStride ,
				verts , 12 , vertsStride ,
				verts , 24 , vertsStride ,
				verts , 36 , vertsStride ,
				indices , 0 , indices.capacity( ) / 4 ,
				quadCornerIndices );
	}
	
	/**
	 * Generates UV vectors. This only works for indexed triangle geometries where every pair of triangles forms a quad and all pairs of triangles have the same
	 * relative orientation.
	 * 
	 * @param verts
	 * @param vertsOffset
	 * @param vertsStride
	 * @param norms
	 * @param normsOffset
	 * @param normsStride
	 * @param us
	 * @param usOffset
	 * @param usStride
	 * @param vs
	 * @param vsOffset
	 * @param vsStride
	 * @param indices
	 * @param indicesStart
	 * @param indicesEnd
	 * @param quadCornerIndices
	 *            a 4-int array specifying the offsets of the quad corner indices within each group of 6 triangle vertex indices, in clockwise or
	 *            counterclockwise order.
	 */
	public static void generateUV3fi( ByteBuffer verts , int vertsOffset , int vertsStride ,
			ByteBuffer norms , int normsOffset , int normsStride ,
			ByteBuffer us , int usOffset , int usStride ,
			ByteBuffer vs , int vsOffset , int vsStride ,
			ByteBuffer indices , int indicesStart , int indicesEnd ,
			int[ ] quadCornerIndices )
	{
		int prevIndicesPosition = indices.position( );
		int prevVertsPosition = verts.position( );
		int prevNormsPosition = norms.position( );
		int prevUsPosition = us.position( );
		int prevVsPosition = vs.position( );
		
		indices.position( indicesStart * 4 );
		
		float[ ][ ] v = new float[ 4 ][ 3 ];
		float[ ][ ] n = new float[ 4 ][ 3 ];
		
		float[ ] uv = new float[ 3 ];
		
		int[ ] triIndices = new int[ 6 ];
		
		for( int i = indicesStart ; i < indicesEnd ; i += 6 )
		{
			triIndices[ 0 ] = indices.getInt( );
			triIndices[ 1 ] = indices.getInt( );
			triIndices[ 2 ] = indices.getInt( );
			triIndices[ 3 ] = indices.getInt( );
			triIndices[ 4 ] = indices.getInt( );
			triIndices[ 5 ] = indices.getInt( );
			
			for( int c = 0 ; c < 4 ; c++ )
			{
				verts.position( vertsOffset + triIndices[ quadCornerIndices[ c ] ] * vertsStride );
				v[ c ][ 0 ] = verts.getFloat( );
				v[ c ][ 1 ] = verts.getFloat( );
				v[ c ][ 2 ] = verts.getFloat( );
				
				norms.position( normsOffset + triIndices[ quadCornerIndices[ c ] ] * normsStride );
				n[ c ][ 0 ] = norms.getFloat( );
				n[ c ][ 1 ] = norms.getFloat( );
				n[ c ][ 2 ] = norms.getFloat( );
			}
			
			putUV( us , usOffset , usStride , n , triIndices , quadCornerIndices , 0 , v[ 1 ] , v[ 0 ] , uv );
			putUV( us , usOffset , usStride , n , triIndices , quadCornerIndices , 1 , v[ 1 ] , v[ 0 ] , uv );
			putUV( us , usOffset , usStride , n , triIndices , quadCornerIndices , 2 , v[ 2 ] , v[ 3 ] , uv );
			putUV( us , usOffset , usStride , n , triIndices , quadCornerIndices , 3 , v[ 2 ] , v[ 3 ] , uv );
			putUV( vs , vsOffset , vsStride , n , triIndices , quadCornerIndices , 0 , v[ 3 ] , v[ 0 ] , uv );
			putUV( vs , vsOffset , vsStride , n , triIndices , quadCornerIndices , 1 , v[ 2 ] , v[ 1 ] , uv );
			putUV( vs , vsOffset , vsStride , n , triIndices , quadCornerIndices , 2 , v[ 2 ] , v[ 1 ] , uv );
			putUV( vs , vsOffset , vsStride , n , triIndices , quadCornerIndices , 3 , v[ 3 ] , v[ 0 ] , uv );
		}
		
		indices.position( prevIndicesPosition );
		verts.position( prevVertsPosition );
		norms.position( prevNormsPosition );
		us.position( prevUsPosition );
		vs.position( prevVsPosition );
	}
	
	private static void putUV( ByteBuffer uvs , int uvsOffset , int uvsStride ,
			float[ ][ ] n , int[ ] triIndices , int[ ] quadCornerIndices , int cornerIndex , float[ ] v0 , float[ ] v1 , float[ ] uv )
	{
		sub3( v1 , v0 , uv );
		normalize3( uv );
		cross( uv , n[ cornerIndex ] , uv );
		cross( n[ cornerIndex ] , uv , uv );
		
		uvs.position( uvsOffset + triIndices[ quadCornerIndices[ cornerIndex ] ] * uvsStride );
		uvs.putFloat( uv[ 0 ] );
		uvs.putFloat( uv[ 1 ] );
		uvs.putFloat( uv[ 2 ] );
	}
	
	{
		
	}
	
	private static void makePerp( float[ ] uv , float[ ] n )
	{
		cross( uv , n , uv );
		cross( n , uv , uv );
	}
}
