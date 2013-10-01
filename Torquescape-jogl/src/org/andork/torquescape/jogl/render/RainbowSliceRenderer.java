package org.andork.torquescape.jogl.render;

import static org.andork.torquescape.jogl.GLUtils.checkGlError;

import javax.media.opengl.GL3;

import org.andork.torquescape.jogl.GLUtils;
import org.andork.torquescape.model.RainbowSlice;
import org.andork.torquescape.model.Zone;

public class RainbowSliceRenderer implements ISliceRenderer<RainbowSlice>
{
	public static final Factory	FACTORY				= new Factory( );
	
	private final String		vertexShaderCode	=
															// This matrix member variable provides a hook to manipulate
															// the coordinates of the objects that use this vertex shader
															"precision highp float;" +
																	"uniform mat4 uMVMatrix;" +
																	"uniform mat4 uPMatrix;" +
																	"attribute vec3 aPosition;" +
																	"attribute vec3 aNormal;" +
																	"attribute vec3 aUvec;" +
																	"attribute vec3 aVvec;" +
																	"varying vec3 vNormal;" +
																	"varying vec3 vUvec;" +
																	"varying vec3 vVvec;" +
																	"void main() {" +
																	"  vNormal = normalize((uMVMatrix * vec4(aNormal, 0)).xyz);" +
																	"  vUvec = normalize((uMVMatrix * vec4(aUvec, 0)).xyz);" +
																	"  vVvec = normalize((uMVMatrix * vec4(aVvec, 0)).xyz);" +
																	"  gl_Position = uPMatrix * uMVMatrix * vec4(aPosition, 1.0);" +
																	"}";
	
	private final String		fragmentShaderCode	=
															"varying vec3 vNormal;" +
																	"varying vec3 vUvec;" +
																	"varying vec3 vVvec;" +
																	"void main() {" +
																	"  float intensity = 0.1 + 0.9 * vNormal.z;" +
																	"  float red = (0.5 + vUvec.z * 0.5) * intensity;" + 
																	"  float blue = (0.5 + vVvec.z * 0.5) * intensity;" +
																	"  gl_FragColor = vec4(0.0, red, blue, 1.0);" +
																	"}";
	
	private int					mProgram;
	
	private int					vao;
	private int					indexEbo;
	
	private Zone				zone;
	private ZoneRenderer		zoneRenderer;
	private RainbowSlice		slice;
	
	public RainbowSliceRenderer( ZoneRenderer zoneRenderer , RainbowSlice slice )
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
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "aPosition" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 0 );
		checkGlError( gl , "glVertexAttribPointer" );
		
		int aNormalLoc = gl.glGetAttribLocation( mProgram , "aNormal" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aNormalLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aNormalLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 12 );
		checkGlError( gl , "glVertexAttribPointer" );
		
		int aUvecLoc = gl.glGetAttribLocation( mProgram , "aUvec" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aUvecLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aUvecLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 28 );
		checkGlError( gl , "glVertexAttribPointer" );
		
		int aVvecLoc = gl.glGetAttribLocation( mProgram , "aVvec" );
		checkGlError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aVvecLoc );
		checkGlError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aVvecLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 40 );
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
	
	long	firstTime	= 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[], org.andork.torquescape.model.Zone, org.andork.torquescape.model.RainbowSlice)
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
		
		if( firstTime == 0 )
		{
			firstTime = System.currentTimeMillis( );
		}
		
		gl.glBindVertexArray( vao );
		checkGlError( gl , "glBindVertexArray" );
		
		gl.glDrawElements( GL3.GL_TRIANGLES , slice.indexBuffer.capacity( ) , GL3.GL_UNSIGNED_SHORT , 0 );
		checkGlError( gl , "glDrawElements" );
		
		gl.glBindVertexArray( 0 );
		checkGlError( gl , "glBindVertexArray" );
		
	}
	
	public static class Factory implements ISliceRendererFactory<RainbowSlice>
	{
		private Factory( )
		{
			
		}
		
		@Override
		public ISliceRenderer<RainbowSlice> create( ZoneRenderer zoneRenderer , RainbowSlice slice )
		{
			return new RainbowSliceRenderer( zoneRenderer , slice );
		}
	}
}
