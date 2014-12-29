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
package org.andork.breakout.model;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_STATIC_DRAW;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_UNSIGNED_INT;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.math3d.Vecmath.subDot3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglDrawable;
import org.andork.jogl.JoglResource;
import org.andork.jogl.util.JoglUtils;

import com.jogamp.common.nio.PointerBuffer;

public class TransparentTerrain implements JoglDrawable , JoglResource
{
	float[ ][ ][ ]	vertices;
	
	int				numVertexRows;
	int				numVertexCols;
	
	int				numCellRows;
	int				numCellCols;
	
	float[ ][ ]		corners	= new float[ 4 ][ 3 ];
	
	IntBuffer		counts;
	IntBuffer		indices;
	PointerBuffer	indexPointers;
	
	boolean			initialized;
	
	int				program;
	int[ ]			vbo		= new int[ 1 ];
	int[ ]			ebo		= new int[ 1 ];
	
	float[ ]		color	= { 0f , 1f , 0f , 0.5f };
	
	public TransparentTerrain( float[ ][ ][ ] vertices )
	{
		super( );
		this.vertices = vertices;
		numVertexRows = vertices.length;
		numVertexCols = vertices[ 0 ].length;
		
		numCellRows = numVertexRows - 1;
		numCellCols = numVertexCols - 1;
		
		setf( corners[ 0 ] , vertices[ 0 ][ 0 ] );
		setf( corners[ 1 ] , vertices[ 0 ][ numCellCols ] );
		setf( corners[ 2 ] , vertices[ numCellRows ][ 0 ] );
		setf( corners[ 3 ] , vertices[ numCellRows ][ numCellCols ] );
	}
	
	public static void randomVerts( float[ ][ ][ ] vertices , float[ ] bounds , Random rand )
	{
		for( int row = 0 ; row < vertices.length ; row++ )
		{
			for( int col = 0 ; col < vertices[ 0 ].length ; col++ )
			{
				float[ ] v = vertices[ row ][ col ];
				float r = ( float ) row / ( vertices.length - 1 );
				float c = ( float ) col / ( vertices[ 0 ].length - 1 );
				v[ 0 ] = bounds[ 0 ] + c * ( bounds[ 3 ] - bounds[ 0 ] );
				v[ 1 ] = bounds[ 1 ] + ( float ) Math.sin( ( r + c ) * 10 ) * ( bounds[ 4 ] - bounds[ 1 ] );
				v[ 2 ] = bounds[ 2 ] + r * ( bounds[ 5 ] - bounds[ 2 ] );
			}
		}
	}
	
	/**
	 * @return {@code true} iff any of the terrain is in front of the camera.
	 */
	private boolean calcOrder( JoglDrawContext context )
	{
		float bestDist = -1f;
		int bestCorner = -1;
		
		float[ ] vi = context.inverseViewXform( );
		
		for( int i = 0 ; i < corners.length ; i++ )
		{
			float dist = subDot3( vi , 12 , corners[ i ] , 0 , vi , 8 );
			if( dist > bestDist )
			{
				bestDist = dist;
				bestCorner = i;
			}
		}
		
		if( bestDist < 0f )
		{
			return false;
		}
		
		int firstRow;
		int lastRow;
		int rowStep;
		int firstCol;
		int lastCol;
		int colStep;
		
		if( bestCorner < 2 )
		{
			firstRow = 0;
			rowStep = numCellCols * 6;
			lastRow = rowStep * ( numCellRows - 1 );
		}
		else
		{
			rowStep = -numCellCols * 6;
			firstRow = -rowStep * ( numCellRows - 1 );
			lastRow = 0;
		}
		
		if( ( bestCorner & 0x1 ) == 0 )
		{
			firstCol = 0;
			colStep = 6;
			lastCol = ( numCellCols - 1 ) * 6;
		}
		else
		{
			firstCol = ( numCellCols - 1 ) * 6;
			colStep = -6;
			lastCol = 0;
		}
		
		indexPointers.position( 0 );
		for( int row = firstRow ; row <= lastRow ; row += rowStep )
		{
			for( int col = firstCol ; col <= lastCol ; col += colStep )
			{
				//				indices.position( row + col );
				indexPointers.put( ( row + col ) * 4 );
			}
		}
		
		indices.position( 0 );
		indexPointers.position( 0 );
		
		return true;
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		if( initialized )
		{
			return;
		}
		
		initialized = true;
		
		String vertexShader = "uniform mat4 p;" +
				"uniform mat4 v;" +
				"uniform mat4 m;" +
				"uniform mat3 n;" +
				
				"attribute vec4 a_pos;" +
				
				"void main() {" +
				"  gl_Position = p * v * m * a_pos;" +
				"}";
		
		String fragmentShader = "uniform vec4 u_color;" +
				
				"void main() {" +
				"  gl_FragColor = u_color;" +
				"}";
		
		program = JoglUtils.loadProgram( gl , vertexShader , fragmentShader );
		
		ByteBuffer b;
		b = ByteBuffer.allocateDirect( numVertexRows * numVertexCols * 16 );
		b.order( ByteOrder.nativeOrder( ) );
		
		for( int row = 0 ; row < numVertexRows ; row++ )
		{
			for( int col = 0 ; col < numVertexCols ; col++ )
			{
				float[ ] v = vertices[ row ][ col ];
				b.putFloat( v[ 0 ] );
				b.putFloat( v[ 1 ] );
				b.putFloat( v[ 2 ] );
				b.putFloat( 1f );
			}
		}
		
		b.position( 0 );
		
		gl.glGenBuffers( 1 , vbo , 0 );
		gl.glBindBuffer( GL_ARRAY_BUFFER , vbo[ 0 ] );
		gl.glBufferData( GL_ARRAY_BUFFER , b.capacity( ) , b , GL_STATIC_DRAW );
		gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
		
		b = ByteBuffer.allocateDirect( numCellRows * numCellCols * 24 );
		b.order( ByteOrder.nativeOrder( ) );
		indices = b.asIntBuffer( );
		
		for( int row = 0 ; row < numCellRows ; row++ )
		{
			int rowStart = row * numVertexCols;
			int nextRowStart = rowStart + numVertexCols;
			for( int col = 0 ; col < numCellCols ; col++ )
			{
				indices.put( rowStart + col );
				indices.put( nextRowStart + col + 1 );
				indices.put( nextRowStart + col );
				indices.put( nextRowStart + col + 1 );
				indices.put( rowStart + col );
				indices.put( rowStart + col + 1 );
			}
		}
		
		indices.position( 0 );
		
		gl.glGenBuffers( 1 , ebo , 0 );
		gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , ebo[ 0 ] );
		gl.glBufferData( GL_ELEMENT_ARRAY_BUFFER , indices.capacity( ) * 4 , indices , GL_STATIC_DRAW );
		gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , 0 );
		
		b = ByteBuffer.allocateDirect( numCellRows * numCellCols * 4 );
		b.order( ByteOrder.nativeOrder( ) );
		counts = b.asIntBuffer( );
		while( counts.hasRemaining( ) )
		{
			counts.put( 6 );
		}
		counts.position( 0 );
		
		indexPointers = PointerBuffer.allocateDirect( numCellRows * numCellCols );
	}
	
	@Override
	public void dispose( GL2ES2 gl )
	{
		if( !initialized )
		{
			return;
		}
		initialized = false;
		
		gl.glDeleteProgram( program );
		
		gl.glDeleteBuffers( 1 , vbo , 0 );
		gl.glDeleteBuffers( 1 , ebo , 0 );
	}
	
	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		if( !calcOrder( context ) )
		{
			return;
		}
		
		gl.glUseProgram( program );
		
		gl.glEnable( GL_BLEND );
		gl.glBlendFunc( GL_SRC_ALPHA , GL_ONE_MINUS_SRC_ALPHA );
		
		int loc;
		
		loc = gl.glGetUniformLocation( program , "p" );
		gl.glUniformMatrix4fv( loc , 1 , false , context.projXform( ) , 0 );
		loc = gl.glGetUniformLocation( program , "v" );
		gl.glUniformMatrix4fv( loc , 1 , false , context.viewXform( ) , 0 );
		loc = gl.glGetUniformLocation( program , "m" );
		gl.glUniformMatrix4fv( loc , 1 , false , m , 0 );
		
		int posLoc = gl.glGetAttribLocation( program , "a_pos" );
		gl.glEnableVertexAttribArray( posLoc );
		gl.glBindBuffer( GL_ARRAY_BUFFER , vbo[ 0 ] );
		gl.glVertexAttribPointer( posLoc , 4 , GL_FLOAT , false , 16 , 0 );
		
		loc = gl.glGetUniformLocation( program , "u_color" );
		gl.glUniform4fv( loc , 1 , color , 0 );
		
		gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , ebo[ 0 ] );
		
		//		( ( GL3 ) gl ).glMultiDrawArrays( GL_TRIANGLES , first , 0 , count , 0 , first.length );
		( ( GL3 ) gl ).glMultiDrawElements( GL_TRIANGLES , counts , GL_UNSIGNED_INT , indexPointers , numCellRows * numCellCols );
		//		gl.glDrawElements( GL_TRIANGLES , numCellRows * numCellCols * 6 , GL_UNSIGNED_INT , 0 );
		
		gl.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER , 0 );
		gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
		
		gl.glDisableVertexAttribArray( posLoc );
		
		gl.glDisable( GL_BLEND );
		
		gl.glUseProgram( 0 );
	}
}
