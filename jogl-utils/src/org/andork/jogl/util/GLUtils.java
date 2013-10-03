package org.andork.jogl.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

public class GLUtils
{
	public static void checkGLError( GL3 gl , String glOperation )
	{
		int error;
		while( ( error = gl.glGetError( ) ) != GL3.GL_NO_ERROR )
		{
			throw new RuntimeException( glOperation + ": glError " + error );
		}
	}
	
	public static void checkGLError( GL3 gl , boolean debug )
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
	
	public static int loadShader( GL3 gl , int type , String shaderCode )
	{
		return loadShader( gl , type , shaderCode , true );
	}
	
	public static int loadShader( GL3 gl , int type , String shaderCode , boolean debug )
	{
		// create a vertex shader type (gl.GL_VERTEX_SHADER)
		// or a fragment shader type (gl.GL_FRAGMENT_SHADER)
		int shader = gl.glCreateShader( type );
		
		// add the source code to the shader and compile it
		gl.glShaderSource( shader , 1 , new String[ ] { shaderCode } , new int[ ] { shaderCode.length( ) } , 0 );
		gl.glCompileShader( shader );
		
		if( debug )
		{
			int[ ] params = new int[ 1 ];
			gl.glGetShaderiv( shader , GL3.GL_COMPILE_STATUS , params , 0 );
			
			if( params[ 0 ] == GL3.GL_FALSE )
			{
				byte[ ] infoLog = new byte[ 2048 ];
				gl.glGetShaderInfoLog( shader , 2048 , params , 0 , infoLog , 0 );
				
				String s = new String( infoLog );
				throw new RuntimeException( "Shader failed to compile.  Log: " + s );
			}
		}
		
		return shader;
	}
	
	public static int loadProgram( GL3 gl , String vertexShaderCode , String fragmentShaderCode , boolean debug )
	{
		int vertexShader = loadShader( gl , GL3.GL_VERTEX_SHADER , vertexShaderCode , debug );
		int fragmentShader = loadShader( gl , GL3.GL_FRAGMENT_SHADER , fragmentShaderCode , debug );
		
		int program = gl.glCreateProgram( );
		checkGLError( gl , debug );
		
		gl.glAttachShader( program , vertexShader );
		checkGLError( gl , debug );
		
		gl.glAttachShader( program , fragmentShader );
		checkGLError( gl , debug );
		
		gl.glLinkProgram( program );
		checkGLError( gl , debug );
		
		if( debug )
		{
			int[ ] params = new int[ 1 ];
			gl.glGetProgramiv( program , GL3.GL_LINK_STATUS , params , 0 );
			
			if( params[ 0 ] == GL3.GL_FALSE )
			{
				byte[ ] infoLog = new byte[ 2048 ];
				gl.glGetProgramInfoLog( program , 2048 , params , 0 , infoLog , 0 );
				
				String s = new String( infoLog );
				throw new RuntimeException( "Program failed to link.  Log: " + s );
			}
		}
		
		return program;
	}
	
	public static int genBuffer( GL3 gl )
	{
		int[ ] result = new int[ 1 ];
		gl.glGenBuffers( 1 , result , 0 );
		return result[ 0 ];
	}
	
	public static int genVertexArray( GL3 gl )
	{
		int[ ] result = new int[ 1 ];
		gl.glGenVertexArrays( 1 , result , 0 );
		return result[ 0 ];
	}
	
	public static int genTexture( GL3 gl )
	{
		int[ ] result = new int[ 1 ];
		gl.glGenTextures( 1 , result , 0 );
		return result[ 0 ];
	}
	
	public static void vertexAttribPointer( GL3 gl , int program , String attrName , int size , int type , boolean normalized , int stride , int offset , boolean debug )
	{
		int index = gl.glGetAttribLocation( program , attrName );
		checkGLError( gl , debug );
		
		gl.glEnableVertexAttribArray( index );
		checkGLError( gl , debug );
		
		gl.glVertexAttribPointer( index , size , type , normalized , stride , offset );
		checkGLError( gl , debug );
	}
}
