package org.andork.torquescape;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLES20;

public class GLUtils
{

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 * 
	 * <pre>
	 * mColorHandle = gl.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param gl
	 *            TODO
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation)
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	public static int loadShader(int type, String shaderCode)
	{

		// create a vertex shader type (gl.GL_VERTEX_SHADER)
		// or a fragment shader type (gl.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);
		checkGlError("glCreateShader");

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		checkGlError("glShaderSource");
		GLES20.glCompileShader(shader);
		checkGlError("glCompileShader");

		checkShaderCompileStatus(shader);

		return shader;
	}

	public static void checkShaderCompileStatus(int shader) {
		int[] params = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
		if (params[0] == GLES20.GL_FALSE) {
			throw new RuntimeException("Shader failed to compile.  Log: " + GLES20.glGetShaderInfoLog(shader));
		}
	}

	public static void checkProgramLinkStatus(int program) {
		int[] params = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, params, 0);
		if (params[0] == GLES20.GL_FALSE) {
			throw new RuntimeException("Program failed to link.  Log: " + GLES20.glGetProgramInfoLog(program));
		}
	}
}
