package org.andork.jogl.util;

import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

public class JOGLUtils
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
		
		int[ ] params = new int[ 1 ];
		gl.glGetShaderiv( shader , GL2ES2.GL_COMPILE_STATUS , params , 0 );
		
		if( params[ 0 ] == GL2ES2.GL_FALSE )
		{
			gl.glGetShaderiv( shader , GL2ES2.GL_INFO_LOG_LENGTH , params , 0 );
			byte[ ] bytes = new byte[ params[ 0 ] ];
			gl.glGetShaderInfoLog( shader , params[ 0 ] , params , 0 , bytes , 0 );
			
			bytes = Arrays.copyOf( bytes , params[ 0 ] );
			throw new RuntimeException( new String( bytes ) );
		}
		
		return shader;
	}
	
	public static int loadProgram( GL2ES2 gl , String vertexShaderCode , String fragmentShaderCode )
	{
		int vertexShader = loadShader( gl , GL2ES2.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = loadShader( gl , GL2ES2.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		int program = gl.glCreateProgram( );
		
		gl.glAttachShader( program , vertexShader );
		gl.glAttachShader( program , fragmentShader );
		gl.glLinkProgram( program );
		
		int[ ] params = new int[ 1 ];
		gl.glGetProgramiv( program , GL2ES2.GL_LINK_STATUS , params , 0 );
		
		if( params[ 0 ] == GL2ES2.GL_FALSE )
		{
			gl.glGetProgramiv( program , GL2ES2.GL_INFO_LOG_LENGTH , params , 0 );
			byte[ ] bytes = new byte[ params[ 0 ] ];
			gl.glGetProgramInfoLog( program , params[ 0 ] , params , 0 , bytes , 0 );
			
			bytes = Arrays.copyOf( bytes , params[ 0 ] );
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
