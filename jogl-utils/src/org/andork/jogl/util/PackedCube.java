package org.andork.jogl.util;

import static org.andork.jogl.util.GLUtils.checkGLError;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.media.opengl.GL3;

public class PackedCube
{
	private final String	vertexShaderCode	=
														// This matrix member variable provides a hook to manipulate
														// the coordinates of the objects that use this vertex shader
														"uniform mat4 uMVMatrix;" +
																"uniform mat4 uPMatrix;" +
																"attribute vec4 vPosition;" +
																"attribute vec3 vNormal;" +
																"attribute vec4 vColor;" +
																"varying vec3 v_fxpos;" +
																"varying vec3 v_fxlightpos;" +
																"varying vec3 v_fxnormal;" +
																"varying vec4 v_fcolor;" +
																"void main() {" +
																"  v_fcolor = vColor;" +
																"  v_fxpos = (uMVMatrix * vPosition).xyz;" +
																"  v_fxlightpos = (uMVMatrix * vec4(1.1, 1.8, 3.5, 1.0)).xyz;" +
																"  v_fxnormal = normalize((uMVMatrix * vec4(vNormal, 0)).xyz);" +
																"  gl_Position = uPMatrix * uMVMatrix * vPosition;" +
																"}";
	
	private final String	fragmentShaderCode	=
														"precision lowp float;" +
																"varying vec3 v_fxpos;" +
																"varying vec3 v_fxlightpos;" +
																"varying vec3 v_fxnormal;" +
																"varying vec4 v_fcolor;" +
																"void main() {" +
																"  vec3 incident = v_fxpos - v_fxlightpos;" +
																"  vec3 ref_light = reflect(normalize(incident), v_fxnormal);" +
																"  float intensity = dot(ref_light, vec3(0.0, 0.0, 1.0)) * 25.0 / (dot(incident, incident) + dot(v_fxpos, v_fxpos));" +
																"  if (dot(v_fxnormal, ref_light) <= 0.0)" +
																"    intensity = 0.0;" +
																"  vec4 color2 = v_fcolor * clamp(intensity, 0.2, 1.0);" +
																"  gl_FragColor = color2;" +
																"}";
	
	private FloatBuffer		vertexBuffer;
	private int				mProgram;
	private int				mPositionHandle;
	private int				mNormalHandle;
	private int				mMVMatrixHandle;
	private int				mPMatrixHandle;
	private int				mColorHandle;
	
	private int				vao;
	private int				vbo;
	
	static float			verts[]				= {
												// top (red)
												1 , 1 , 1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												1 , 1 , -1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												-1 , 1 , -1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												-1 , 1 , -1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												-1 , 1 , 1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												1 , 1 , 1 , 0 , 1 , 0 , 1 , 0 , 0 , 1 ,
												// bottom (green)
												1 , -1 , 1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												-1 , -1 , 1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												-1 , -1 , -1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												-1 , -1 , -1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												1 , -1 , -1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												1 , -1 , 1 , 0 , -1 , 0 , 0 , 1 , 0 , 1 ,
												// front (blue)
												1 , -1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												1 , 1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												-1 , -1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												-1 , -1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												1 , 1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												-1 , 1 , 1 , 0 , 0 , 1 , 0 , 0 , 1 , 1 ,
												// back (yellow)
												1 , -1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												-1 , -1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												1 , 1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												1 , 1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												-1 , -1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												-1 , 1 , -1 , 0 , 0 , -1 , 1 , 1 , 0 , 1 ,
												// left (cyan)
												-1 , 1 , 1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												-1 , 1 , -1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												-1 , -1 , 1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												-1 , -1 , 1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												-1 , 1 , -1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												-1 , -1 , -1 , -1 , 0 , 0 , 0 , 1 , 1 , 1 ,
												// right (magenta)
												1 , -1 , 1 , 1 , 0 , 0 , 1 , 0 , 1 , 1 ,
												1 , -1 , -1 , 1 , 0 , 0 , 1 , 0 , 1 , 1 ,
												1 , 1 , 1 , 1 , 0 , 0 , 1 , 0 , 1 , 1 ,
												1 , 1 , 1 , 1 , 0 , 0 , 1 , 0 , 1 , 1 ,
												1 , -1 , -1 , 1 , 0 , 0 , 1 , 0 , 1 , 1 ,
												1 , 1 , -1 , 1 , 0 , 0 , 1 , 0 , 1 , 1
												};
	
	// number of coordinates per vertex in this array
	static final int		COORDS_PER_VERTEX	= 3;
	static final int		NORMALS_PER_VERTEX	= 3;
	static final int		COLORS_PER_VERTEX	= 4;
	static final int		VALUES_PER_VERTEX	= COORDS_PER_VERTEX + NORMALS_PER_VERTEX + COLORS_PER_VERTEX;
	
	static final int		VERTEX_STRIDE		= VALUES_PER_VERTEX * 4;
	static final int		NORMAL_OFFSET		= COORDS_PER_VERTEX;
	static final int		COLOR_OFFSET		= NORMAL_OFFSET + NORMALS_PER_VERTEX;
	
	static final int		VERTEX_COUNT		= verts.length / VALUES_PER_VERTEX;
	
	public PackedCube( )
	{
	}
	
	public void init( GL3 gl )
	{
		int vertexShader = GLUtils.loadShader( gl , GL3.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = GLUtils.loadShader( gl , GL3.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		mProgram = gl.glCreateProgram( ); // create empty OpenGL ES Program
		checkGLError( gl , "glCreateProgram" );
		gl.glAttachShader( mProgram , vertexShader ); // add the vertex shader
		checkGLError( gl , "glAttachShader" );
		// to program
		gl.glAttachShader( mProgram , fragmentShader ); // add the fragment
		checkGLError( gl , "glAttachShader" );
		// shader to program
		gl.glLinkProgram( mProgram ); // creates OpenGL ES program executables
		checkGLError( gl , "glLinkProgram" );
		
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				verts.length * 4 );
		// use the device hardware's native byte order
		bb.order( ByteOrder.nativeOrder( ) );
		
		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer( );
		// add the coordinates to the FloatBuffer
		vertexBuffer.put( verts );
		// set the buffer to read the first coordinate
		vertexBuffer.position( 0 );
		
		int[ ] vaos = new int[ 1 ];
		gl.glGenVertexArrays( 1 , vaos , 0 );
		vao = vaos[ 0 ];
		
		int[ ] vbos = new int[ 1 ];
		gl.glGenBuffers( 1 , vbos , 0 );
		vbo = vbos[ 0 ];
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , "glBindVertexArray" );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl , "glBindBuffer" );
		
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , vertexBuffer.capacity( ) * 4 , vertexBuffer , GL3.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		
		// get handle to vertex shader's vPosition member
		mPositionHandle = gl.glGetAttribLocation( mProgram , "vPosition" );
		checkGLError( gl , "glGetAttribLocation" );
		
		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray( mPositionHandle );
		checkGLError( gl , "glEnableVertexAttribArray" );
		
		vertexBuffer.position( 0 );
		
		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer( mPositionHandle , COORDS_PER_VERTEX , GL3.GL_FLOAT , false , VERTEX_STRIDE , 0 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		// get handle to fragment shader's vColor member
		mColorHandle = gl.glGetAttribLocation( mProgram , "vColor" );
		checkGLError( gl , "glGetAttribLocation" );
		
		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray( mColorHandle );
		checkGLError( gl , "glEnableVertexAttribArray" );
		
		vertexBuffer.position( COLOR_OFFSET );
		
		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer( mColorHandle , COLORS_PER_VERTEX , GL3.GL_FLOAT , false , VERTEX_STRIDE , COLOR_OFFSET * 4 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		// get handle to fragment shader's vNormal member
		mNormalHandle = gl.glGetAttribLocation( mProgram , "vNormal" );
		checkGLError( gl , "glGetAttribLocation" );
		
		// Enable a handle to the triangle vertices
		gl.glEnableVertexAttribArray( mNormalHandle );
		checkGLError( gl , "glEnableVertexAttribArray" );
		
		vertexBuffer.position( NORMAL_OFFSET );
		
		// Prepare the triangle coordinate data
		gl.glVertexAttribPointer( mNormalHandle , NORMALS_PER_VERTEX , GL3.GL_FLOAT , false , VERTEX_STRIDE , NORMAL_OFFSET * 4 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		checkGLError( gl , "glBindBuffer" );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , "glBindVertexArray" );
	}
	
	public void draw( GL3 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
		// Add program to OpenGL ES environment
		gl.glUseProgram( mProgram );
		checkGLError( gl , "glUseProgram" );
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , "glBindVertexArray" );
		//
		// gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		// checkGLError( gl , "glBindBuffer" );
		//
		// // get handle to vertex shader's vPosition member
		// mPositionHandle = gl.glGetAttribLocation( mProgram , "vPosition" );
		// checkGLError( gl , "glGetAttribLocation" );
		//
		// // Enable a handle to the triangle vertices
		// gl.glEnableVertexAttribArray( mPositionHandle );
		// checkGLError( gl , "glEnableVertexAttribArray" );
		//
		// vertexBuffer.position( 0 );
		//
		// // Prepare the triangle coordinate data
		// gl.glVertexAttribPointer( mPositionHandle , COORDS_PER_VERTEX , gl.GL_FLOAT , false , VERTEX_STRIDE , 0 );
		// checkGLError( gl , "glVertexAttribPointer" );
		//
		// // get handle to fragment shader's vColor member
		// mColorHandle = gl.glGetAttribLocation( mProgram , "vColor" );
		// checkGLError( gl , "glGetAttribLocation" );
		//
		// // Enable a handle to the triangle vertices
		// gl.glEnableVertexAttribArray( mColorHandle );
		// checkGLError( gl , "glEnableVertexAttribArray" );
		//
		// vertexBuffer.position( 6 );
		//
		// // Prepare the triangle coordinate data
		// gl.glVertexAttribPointer( mColorHandle , COLORS_PER_VERTEX , gl.GL_FLOAT , false , VERTEX_STRIDE , 0 );
		// checkGLError( gl , "glVertexAttribPointer" );
		//
		// // get handle to fragment shader's vNormal member
		// mNormalHandle = gl.glGetAttribLocation( mProgram , "vNormal" );
		// checkGLError( gl , "glGetAttribLocation" );
		//
		// // Enable a handle to the triangle vertices
		// gl.glEnableVertexAttribArray( mNormalHandle );
		// checkGLError( gl , "glEnableVertexAttribArray" );
		//
		// vertexBuffer.position( NORMAL_OFFSET );
		//
		// // Prepare the triangle coordinate data
		// gl.glVertexAttribPointer( mNormalHandle , NORMALS_PER_VERTEX , gl.GL_FLOAT , false , VERTEX_STRIDE , 0 );
		// checkGLError( gl , "glVertexAttribPointer" );
		
		mMVMatrixHandle = gl.glGetUniformLocation( mProgram , "uMVMatrix" );
		checkGLError( gl , "glGetUniformLocation" );
		
		gl.glUniformMatrix4fv( mMVMatrixHandle , 1 , false , mvMatrix , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		mPMatrixHandle = gl.glGetUniformLocation( mProgram , "uPMatrix" );
		checkGLError( gl , "glGetUniformLocation" );
		
		gl.glUniformMatrix4fv( mPMatrixHandle , 1 , false , pMatrix , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		// Draw
		gl.glDrawArrays( GL3.GL_TRIANGLES , 0 , VERTEX_COUNT );
		checkGLError( gl , "glDrawArrays" );
		
		// gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		// checkGLError( gl , "glBindBuffer" );
		//
		gl.glBindVertexArray( 0 );
		checkGLError( gl , "glBindVertexArray" );
		
		// // Disable vertex array
		// gl.glDisableVertexAttribArray( mPositionHandle );
		// checkGLError( gl , "glDisableVertexAttribArray" );
		// gl.glDisableVertexAttribArray( mNormalHandle );
		// checkGLError( gl , "glDisableVertexAttribArray" );
		// gl.glDisableVertexAttribArray( mColorHandle );
		// checkGLError( gl , "glDisableVertexAttribArray" );
	}
}