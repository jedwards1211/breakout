package org.andork.jogl.util;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

public class GLUtils
{
	
	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call just after making it:
	 * 
	 * <pre>
	 * mColorHandle = gl.glGetUniformLocation( mProgram , &quot;vColor&quot; );
	 * MyGLRenderer.checkGlError( &quot;glGetUniformLocation&quot; );
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param gl
	 *            TODO
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGLError( GL3 gl , String glOperation )
	{
		int error;
		while( ( error = gl.glGetError( ) ) != GL3.GL_NO_ERROR )
		{
			throw new RuntimeException( glOperation + ": glError " + error );
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
		
		// create a vertex shader type (gl.GL_VERTEX_SHADER)
		// or a fragment shader type (gl.GL_FRAGMENT_SHADER)
		int shader = gl.glCreateShader( type );
		
		// add the source code to the shader and compile it
		gl.glShaderSource( shader , 1 , new String[ ] { shaderCode } , new int[ ] { shaderCode.length( ) } , 0 );
		gl.glCompileShader( shader );
		
		int[ ] params = new int[ 1 ];
		gl.glGetShaderiv( shader , GL3.GL_COMPILE_STATUS , params , 0 );
		
		if( params[ 0 ] == GL3.GL_FALSE )
		{
			byte[ ] infoLog = new byte[ 2048 ];
			gl.glGetShaderInfoLog( shader , 2048 , params , 0 , infoLog , 0 );
			
			String s = new String( infoLog );
			throw new RuntimeException( "Shader failed to compile.  Log: " + s );
		}
		
		return shader;
	}
	
}
