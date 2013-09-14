package org.andork.jogl.util;

import java.nio.Buffer;

import static org.andork.jogl.util.GLUtils.*;
import javax.media.opengl.GL3;

public class SimplePolygon
{
	public int					coordsType			= GL3.GL_FLOAT;
	public int					coordsStride		= 12;
	public int					vertexCount;
	public Buffer				coords;
	public final float[ ]		color				= { 1 , 0 , 0 , 1 };
	
	int							vao;
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
	
	public static void globalInit( GL3 gl )
	{
		vertexShader = GLUtils.loadShader( gl , GL3.GL_VERTEX_SHADER , vertexShaderCode );
		fragmentShader = GLUtils.loadShader( gl , GL3.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		program = gl.glCreateProgram( );
		gl.glAttachShader( program , vertexShader );
		checkGLError( gl , "glAttachShader" );
		gl.glAttachShader( program , fragmentShader );
		checkGLError( gl , "glAttachShader" );
		gl.glLinkProgram( program );
		checkGLError( gl , "glLinkProgram" );
	}
	
	public void init( GL3 gl )
	{
		int[ ] temp = new int[ 1 ];
		
		gl.glGenVertexArrays( 1 , temp , 0 );
		checkGLError( gl , "glGenVertexArrays" );
		vao = temp[ 0 ];
		
		gl.glGenBuffers( 1 , temp , 0 );
		checkGLError( gl , "glGenBuffers" );
		vbo = temp[ 0 ];
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , "glBindVertexArray" );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl , "glBindBuffer" );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , coords.capacity( ) , coords , GL3.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		
		int coordIndex = gl.glGetAttribLocation( program , "coord" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( coordIndex );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( coordIndex , 3 , coordsType , false , coordsStride , 0 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindVertexArray( 0 );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
	}
	
	public void draw( GL3 gl , float[ ] mvpMatrix )
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
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , "glBindVertexArray" );
		
		gl.glDrawArrays( GL3.GL_LINE_STRIP , 0 , vertexCount );
		checkGLError( gl , "glDrawArrays" );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , "glBindVertexArray" );
	}
}
