package org.andork.torquescape;

import javax.media.opengl.GL3;

import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;

public class StandardSliceRenderer implements ISliceRenderer<StandardSlice>
{
	private final String	vertexShaderCode	=
														// This matrix member variable provides a hook to manipulate
														// the coordinates of the objects that use this vertex shader
														"uniform mat4 uMVMatrix;" +
																"uniform mat4 uPMatrix;" +
																"attribute vec4 vPosition;" +
																"attribute vec3 vNormal;" +
																"varying vec3 v_fxnormal;" +
																"void main() {" +
																"  v_fxnormal = normalize((uMVMatrix * vec4(vNormal, 0)).xyz);" +
																"  gl_Position = uPMatrix * uMVMatrix * vPosition;" +
																"}";
	
	private final String	fragmentShaderCode	=
														"varying vec3 v_fxnormal;" +
																"uniform vec4 vAmbientColor;" +
																"uniform vec4 vDiffuseColor;" +
																"void main() {" +
																"  float intensity = dot(v_fxnormal, vec3(0.0, 0.0, 1.0));" +
																"  gl_FragColor = mix(vAmbientColor, vDiffuseColor, intensity);" +
																"}";
	
	private ZoneRenderer	zoneRenderer;
	private StandardSlice	slice;
	
	private int				mProgram;
	private int				vao;
	
	public StandardSliceRenderer( ZoneRenderer zoneRenderer , StandardSlice slice )
	{
		this.zoneRenderer = zoneRenderer;
		this.slice = slice;
	}
	
	public void init( GL3 gl )
	{
		int vertexShader = Loaders.loadShader( gl , GL3.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = Loaders.loadShader( gl , GL3.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
		mProgram = gl.glCreateProgram( ); // create empty OpenGL ES Program
		checkGlError( gl , "glCreateProgram" );
		gl.glAttachShader( mProgram , vertexShader ); // add the vertex shader
		checkGlError( gl , "glAttachShader" );
		// to program
		gl.glAttachShader( mProgram , fragmentShader ); // add the fragment
		checkGlError( gl , "glAttachShader" );
		// shader to program
		gl.glLinkProgram( mProgram ); // creates OpenGL ES program executables
		checkGlError( gl , "glLinkProgram" );
		
//		int[ ] varrays = new int[ 1 ];
//		
//		gl.glGenVertexArrays( 1 , varrays , 0 );
//		checkGlError( gl , "glGenVertexArrays" );
//		
//		vao = varrays[ 0 ];
//		
//		gl.glBindVertexArray( vao );
//		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , zoneRenderer.vertVBO );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[], org.andork.torquescape.model.Zone, org.andork.torquescape.model.StandardSlice)
	 */
	@Override
	public void draw( GL3 gl , float[ ] mvMatrix , float[ ] pMatrix )
	{
		gl.glUseProgram( mProgram );
		checkGlError( gl , "glUseProgram" );
		
		int mvMatrixLoc = gl.glGetUniformLocation( mProgram , "uMVMatrix" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( mvMatrixLoc , 1 , false , mvMatrix , 0 );
		checkGlError( gl , "glUniformMatrix4fv" );
		
		int pMatrixLoc = gl.glGetUniformLocation( mProgram , "uPMatrix" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( pMatrixLoc , 1 , false , pMatrix , 0 );
		checkGlError( gl , "glUniformMatrix4fv" );
		
		Zone zone = zoneRenderer.zone;
		
		zone.vertBuffer.position( 0 );
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "vPosition" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL3.GL_FLOAT , false , 24 , zone.vertBuffer );
		checkGlError( gl , "glVertexAttribPointer" );
		
		zone.vertBuffer.position( 3 );
		
		int vNormalLoc = gl.glGetAttribLocation( mProgram , "vNormal" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glVertexAttribPointer( vNormalLoc , 3 , GL3.GL_FLOAT , false , 24 , zone.vertBuffer );
		checkGlError( gl , "glVertexAttribPointer" );
		
		int ambientLoc = gl.glGetUniformLocation( mProgram , "vAmbientColor" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( ambientLoc , 4 , slice.ambientColor , 0 );
		checkGlError( gl , "glUniform4fv" );
		
		int diffuseLoc = gl.glGetUniformLocation( mProgram , "vDiffuseColor" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( diffuseLoc , 4 , slice.diffuseColor , 0 );
		checkGlError( gl , "glUniform4fv" );
		
		slice.indexBuffer.position( 0 );
		gl.glDrawElements( GL3.GL_TRIANGLES , slice.indexBuffer.length( ) / 3 , GL3.GL_UNSIGNED_SHORT , slice.indexBuffer );
		checkGlError( gl , "glDrawElements" );
	}
	
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
