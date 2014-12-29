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

import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class JoglUtils
{
	public static void checkGLError( GL2ES2 gl , String glOperation )
	{
		int error;
		while( ( error = gl.glGetError( ) ) != GL2ES2.GL_NO_ERROR )
		{
			throw new RuntimeException( glOperation + ": glError " + error );
		}
	}
	
	public static void checkGLError( GL2ES2 gl , boolean debug )
	{
		if( debug )
		{
			int error;
			while( ( error = gl.glGetError( ) ) != GL.GL_NO_ERROR )
			{
				throw new RuntimeException( "glError " + error );
			}
		}
	}
	
	public static void checkGLError( GL gl )
	{
		int error;
		while( ( error = gl.glGetError( ) ) != GL.GL_NO_ERROR )
		{
			throw new RuntimeException( "glError " + error );
		}
	}
	
	public static int loadShader( GL2ES2 gl , int type , String shaderCode )
	{
		// create a vertex shader type (gl.GL_VERTEX_SHADER)
		// or a fragment shader type (gl.GL_FRAGMENT_SHADER)
		int shader = gl.glCreateShader( type );
		
		// add the source code to the shader and compile it
		gl.glShaderSource( shader , 1 , new String[ ] { shaderCode } , new int[ ] { shaderCode.length( ) } , 0 );
		gl.glCompileShader( shader );
		
		int[ ] params = new int[ 2 ];
		gl.glGetShaderiv( shader , GL2ES2.GL_COMPILE_STATUS , params , 0 );
			gl.glGetShaderiv( shader , GL2ES2.GL_INFO_LOG_LENGTH , params , 1 );
		
		if( params[ 0 ] == GL2ES2.GL_FALSE )
		{
			byte[ ] bytes = new byte[ params[ 1 ] ];
			gl.glGetShaderInfoLog( shader , params[ 1 ] , params , 1 , bytes , 0 );
			throw new RuntimeException( new String( bytes ) );
		}
		
		return shader;
	}
	
	public static int loadProgram( GL2ES2 gl , String vertexShaderCode , String fragmentShaderCode )
	{
		int vertexShader = loadShader( gl , GL2ES2.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = loadShader( gl , GL2ES2.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		return loadProgram( gl , vertexShader , fragmentShader );
	}
	
	public static int loadProgram( GL2ES2 gl , int vertexShader , int fragmentShader )
	{
		int program = gl.glCreateProgram( );
		
		gl.glAttachShader( program , vertexShader );
		gl.glAttachShader( program , fragmentShader );
		gl.glLinkProgram( program );
		
		int[ ] params = new int[ 2 ];
		gl.glGetProgramiv( program , GL2ES2.GL_LINK_STATUS , params , 0 );
		gl.glGetProgramiv( program , GL2ES2.GL_INFO_LOG_LENGTH , params , 1 );
		
		if( params[ 0 ] == GL2ES2.GL_FALSE )
		{
			byte[ ] bytes = new byte[ params[ 1 ] ];
			gl.glGetProgramInfoLog( program , params[ 1 ] , params , 1 , bytes , 0 );
			throw new RuntimeException( new String( bytes ) );
		}
		
		return program;
	}
	
	public static int genBuffer( GL2ES2 gl )
	{
		int[ ] result = new int[ 1 ];
		gl.glGenBuffers( 1 , result , 0 );
		return result[ 0 ];
	}
	
	public static int genTexture( GL2ES2 gl )
	{
		int[ ] result = new int[ 1 ];
		gl.glGenTextures( 1 , result , 0 );
		return result[ 0 ];
	}
	
	public static void vertexAttribPointer( GL2ES2 gl , int program , String attrName , int size , int type , boolean normalized , int stride , int offset )
	{
		int index = gl.glGetAttribLocation( program , attrName );
		gl.glEnableVertexAttribArray( index );
		gl.glVertexAttribPointer( index , size , type , normalized , stride , offset );
	}
}
