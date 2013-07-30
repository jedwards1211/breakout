package org.andork.torquescape.jogl.render;

import static org.andork.torquescape.jogl.GLUtils.checkGlError;

import javax.media.opengl.GL3;

import org.andork.torquescape.jogl.GLUtils;
import org.andork.torquescape.model.StandardSlice;
import org.andork.torquescape.model.Zone;

public class StandardSliceRenderer implements ISliceRenderer<StandardSlice>
{
	public static final Factory	FACTORY				= new Factory( );
	
	private final String		vertexShaderCode	=
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
	
	private final String		fragmentShaderCode	=
															"varying vec3 v_fxnormal;" +
																	"uniform vec4 vAmbientColor;" +
																	"uniform vec4 vDiffuseColor;" +
																	"void main() {" +
																	"  float intensity = dot(v_fxnormal, vec3(0.0, 0.0, 1.0));" +
																	"  gl_FragColor = mix(vAmbientColor, vDiffuseColor, intensity);" +
																	"}";
	
	private int					mProgram;
	
	private int					vao;
	private int					indexEbo;
	
	private Zone				zone;
	private ZoneRenderer		zoneRenderer;
	private StandardSlice		slice;
	
	public StandardSliceRenderer( ZoneRenderer zoneRenderer , StandardSlice slice )
	{
		this.zoneRenderer = zoneRenderer;
		this.zone = zoneRenderer.zone;
		this.slice = slice;
	}
	
	public void init( GL3 gl )
	{
		int vertexShader = GLUtils.loadShader( gl , GL3.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = GLUtils.loadShader( gl , GL3.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
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
		
		int[ ] vaos = new int[ 1 ];
		int[ ] vbos = new int[ 1 ];
		
		gl.glGenVertexArrays( 1 , vaos , 0 );
		checkGlError( gl , "glGenVertexArrays" );
		
		gl.glGenBuffers( 1 , vbos , 0 );
		checkGlError( gl , "glGenBuffers" );
		
		vao = vaos[ 0 ];
		indexEbo = vbos[ 0 ];
		
		gl.glBindVertexArray( vao );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , zoneRenderer.vertVbo );
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "vPosition" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL3.GL_FLOAT , false , 24 , 0 );
		checkGlError( gl , "glVertexAttribPointer" );
		
		int vNormalLoc = gl.glGetAttribLocation( mProgram , "vNormal" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vNormalLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vNormalLoc , 3 , GL3.GL_FLOAT , false , 24 , 12 );
		checkGlError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		
		slice.indexBuffer.position( 0 );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , indexEbo );
		checkGlError( gl , "glBindBuffer" );
		gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER , slice.indexBuffer.capacity( ) * 2 , slice.indexBuffer , GL3.GL_STATIC_DRAW );
		checkGlError( gl , "glBufferData" );
		
		gl.glBindVertexArray( 0 );
		checkGlError( gl , "glBindVertexArray" );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGlError( gl , "glBindBuffer" );
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
		
		int ambientLoc = gl.glGetUniformLocation( mProgram , "vAmbientColor" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( ambientLoc , 1 , slice.ambientColor , 0 );
		checkGlError( gl , "glUniform4fv" );
		
		int diffuseLoc = gl.glGetUniformLocation( mProgram , "vDiffuseColor" );
		checkGlError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( diffuseLoc , 1 , slice.diffuseColor , 0 );
		checkGlError( gl , "glUniform4fv" );
		
		gl.glBindVertexArray( vao );
		checkGlError( gl , "glBindVertexArray" );
		
		gl.glDrawElements( GL3.GL_TRIANGLES , slice.indexBuffer.capacity( ), GL3.GL_UNSIGNED_SHORT , 0 );
		checkGlError( gl , "glDrawElements" );
		
		gl.glBindVertexArray( 0 );
		checkGlError( gl , "glBindVertexArray" );
		
	}
	
	public static class Factory implements ISliceRendererFactory<StandardSlice>
	{
		private Factory( )
		{
			
		}
		
		@Override
		public ISliceRenderer<StandardSlice> create( ZoneRenderer zoneRenderer , StandardSlice slice )
		{
			return new StandardSliceRenderer( zoneRenderer , slice );
		}
	}
}
