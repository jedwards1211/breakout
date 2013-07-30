package org.andork.torquescape;

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
	public static void checkGlError( GL3 gl , String glOperation )
	{
		int error;
		while( ( error = gl.glGetError( ) ) != GL3.GL_NO_ERROR )
		{
			throw new RuntimeException( glOperation + ": glError " + error );
		}
	}
	
}
