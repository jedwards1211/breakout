package org.andork.torquescape;

import javax.media.opengl.GL3;

public class Loaders
{
	public static int loadShader( GL3 gl , int type , String shaderCode )
	{
		
		// create a vertex shader type (gl.GL_VERTEX_SHADER)
		// or a fragment shader type (gl.GL_FRAGMENT_SHADER)
		int shader = gl.glCreateShader( type );
		
		// add the source code to the shader and compile it
		gl.glShaderSource( shader , 1 , new String[ ] { shaderCode } , new int[ ] { shaderCode.length( ) } , 0 );
		gl.glCompileShader( shader );
		
		return shader;
	}
}
