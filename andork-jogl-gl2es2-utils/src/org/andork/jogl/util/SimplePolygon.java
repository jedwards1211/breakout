package org.andork.jogl.util;

import java.nio.Buffer;

import static org.andork.jogl.util.JOGLUtils.*;

import javax.media.opengl.GL2ES2;

public class SimplePolygon
{
	public int					coordsType			= GL2ES2.GL_FLOAT;
	public int					coordsStride		= 12;
	public int					vertexCount;
	public Buffer				coords;
	public final float[ ]		color				= { 1 , 0 , 0 , 1 };
	
	int							vbo;
	
	static int					program;
	static int					vertexShader;
	static int					fragmentShader;
	
	private static final String	vertexShaderCode	=
															"uniform mat4 mvpMatrix;" +
																	"attribute vec3 coord;" +
																	"void main() {" +
																	"  gl_Position = mvpMatrix * vec4(coord, 1.0);" +
																	"}";
	
	private static final String	fragmentShaderCode	=
															"uniform vec4 color;" +
																	"void main() {" +
																	"  gl_FragColor = color;" +
																	"}";
	
	public static void globalInit( GL2ES2 gl )
	{
		vertexShader = JOGLUtils.loadShader( gl , GL2ES2.GL_VERTEX_SHADER , vertexShaderCode );
		fragmentShader = JOGLUtils.loadShader( gl , GL2ES2.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		program = gl.glCreateProgram( );
		gl.glAttachShader( program , vertexShader );
		checkGLError( gl , "glAttachShader" );
		gl.glAttachShader( program , fragmentShader );
		checkGLError( gl , "glAttachShader" );
		gl.glLinkProgram( program );
		checkGLError( gl , "glLinkProgram" );
	}
	
	public void init( GL2ES2 gl )
	{
		int[ ] temp = new int[ 1 ];
		
		gl.glGenBuffers( 1 , temp , 0 );
		checkGLError( gl , "glGenBuffers" );
		vbo = temp[ 0 ];
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl , "glBindBuffer" );
		coords.position( 0 );
		gl.glBufferData( GL2ES2.GL_ARRAY_BUFFER , coords.capacity( ) , coords , GL2ES2.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
	}
	
	public void draw( GL2ES2 gl , float[ ] mvpMatrix )
	{
		gl.glUseProgram( program );
		checkGLError( gl , "glUseProgram" );
		
		int mvpMatrixIndex = gl.glGetUniformLocation( program , "mvpMatrix" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( mvpMatrixIndex , 1 , false , mvpMatrix , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		int colorLocation = gl.glGetUniformLocation( program , "color" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( colorLocation , 1 , color , 0 );
		checkGLError( gl , "glUniform4fv" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl , "glBindBuffer" );
		
		int coordIndex = gl.glGetAttribLocation( program , "coord" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( coordIndex );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( coordIndex , 3 , coordsType , false , coordsStride , 0 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glDrawArrays( GL2ES2.GL_LINE_STRIP , 0 , vertexCount );
		checkGLError( gl , "glDrawArrays" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
	}
}
