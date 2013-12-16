package org.andork.torquescape.jogl.render;

import static org.andork.jogl.util.JOGLUtils.checkGLError;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.util.JOGLUtils;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.slice.RainbowSlice;

public class RainbowSliceRenderer implements ISliceRenderer<RainbowSlice>
{
	public static final Factory	FACTORY				= new Factory( );
	
	private final String		vertexShaderCode	=
															// This matrix member variable provides a hook to manipulate
															// the coordinates of the objects that use this vertex shader
															"precision highp float;" +
																	"uniform mat4 m;" +
																	"uniform mat4 v;" +
																	"uniform mat4 p;" +
																	"attribute vec3 aPosition;" +
																	"attribute vec3 aNormal;" +
																	"attribute vec3 aUvec;" +
																	"attribute vec3 aVvec;" +
																	"varying vec3 vNormal;" +
																	"varying vec3 vUvec;" +
																	"varying vec3 vVvec;" +
																	"void main() {" +
																	"  vNormal = normalize((v * m * vec4(aNormal, 0)).xyz);" +
																	"  vUvec = normalize((v * m * vec4(aUvec, 0)).xyz);" +
																	"  vVvec = normalize((v * m * vec4(aVvec, 0)).xyz);" +
																	"  gl_Position = p * v * m * vec4(aPosition, 1.0);" +
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
	
	private int					indexEbo;
	
	private Zone				zone;
	private ZoneRenderer		zoneRenderer;
	private RainbowSlice		slice;
	
	private boolean				transpose			= false;
	
	public RainbowSliceRenderer( ZoneRenderer zoneRenderer , RainbowSlice slice )
	{
		this.zoneRenderer = zoneRenderer;
		this.zone = zoneRenderer.zone;
		this.slice = slice;
	}
	
	public RainbowSliceRenderer transpose( boolean transpose )
	{
		this.transpose = transpose;
		return this;
	}
	
	public void init( GL2ES2 gl )
	{
		int vertexShader = JOGLUtils.loadShader( gl , GL2ES2.GL_VERTEX_SHADER , vertexShaderCode );
		int fragmentShader = JOGLUtils.loadShader( gl , GL2ES2.GL_FRAGMENT_SHADER , fragmentShaderCode );
		
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
		
		int[ ] vbos = new int[ 1 ];
		
		gl.glGenBuffers( 1 , vbos , 0 );
		checkGLError( gl , "glGenBuffers" );
		
		indexEbo = vbos[ 0 ];
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.coordBufferKey ) );
		int coordStride = slice.coordStride < 0 ? zone.bytesPerVertexMap.get( slice.coordBufferKey ) : slice.coordStride;
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "aPosition" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL2ES2.GL_FLOAT , false , coordStride , slice.coordOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.normalBufferKey ) );
		int normalStride = slice.normalStride < 0 ? zone.bytesPerVertexMap.get( slice.normalBufferKey ) : slice.normalStride;
		
		int aNormalLoc = gl.glGetAttribLocation( mProgram , "aNormal" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aNormalLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aNormalLoc , 3 , GL2ES2.GL_FLOAT , false , normalStride , slice.normalOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.uBufferKey ) );
		int uStride = slice.uStride < 0 ? zone.bytesPerVertexMap.get( slice.uBufferKey ) : slice.uStride;
		
		int aUvecLoc = gl.glGetAttribLocation( mProgram , "aUvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aUvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aUvecLoc , 3 , GL2ES2.GL_FLOAT , false , uStride , slice.uOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.vBufferKey ) );
		int vStride = slice.vStride < 0 ? zone.bytesPerVertexMap.get( slice.vBufferKey ) : slice.vStride;
		
		int aVvecLoc = gl.glGetAttribLocation( mProgram , "aVvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aVvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aVvecLoc , 3 , GL2ES2.GL_FLOAT , false , vStride , slice.vOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
		
		slice.indexBuffer.position( 0 );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , indexEbo );
		checkGLError( gl , "glBindBuffer" );
		gl.glBufferData( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , slice.indexBuffer.capacity( ) * 2 , slice.indexBuffer , GL2ES2.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl , "glBindBuffer" );
	}
	
	long	firstTime	= 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[], org.andork.torquescape.model.Zone, org.andork.torquescape.model.RainbowSlice)
	 */
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v, float[ ] p )
	{
		gl.glUseProgram( mProgram );
		checkGLError( gl , "glUseProgram" );
		
		int m_loc = gl.glGetUniformLocation( mProgram , "m" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( m_loc , 1 , transpose , m , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		int v_loc = gl.glGetUniformLocation( mProgram , "v" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( v_loc , 1 , transpose , v , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		int p_loc = gl.glGetUniformLocation( mProgram , "p" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( p_loc , 1 , transpose , p , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		if( firstTime == 0 )
		{
			firstTime = System.currentTimeMillis( );
		}
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.coordBufferKey ) );
		int coordStride = slice.coordStride < 0 ? zone.bytesPerVertexMap.get( slice.coordBufferKey ) : slice.coordStride;
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "aPosition" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL2ES2.GL_FLOAT , false , coordStride , slice.coordOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.normalBufferKey ) );
		int normalStride = slice.normalStride < 0 ? zone.bytesPerVertexMap.get( slice.normalBufferKey ) : slice.normalStride;
		
		int aNormalLoc = gl.glGetAttribLocation( mProgram , "aNormal" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aNormalLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aNormalLoc , 3 , GL2ES2.GL_FLOAT , false , normalStride , slice.normalOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.uBufferKey ) );
		int uStride = slice.uStride < 0 ? zone.bytesPerVertexMap.get( slice.uBufferKey ) : slice.uStride;
		
		int aUvecLoc = gl.glGetAttribLocation( mProgram , "aUvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aUvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aUvecLoc , 3 , GL2ES2.GL_FLOAT , false , uStride , slice.uOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbos.get( slice.vBufferKey ) );
		int vStride = slice.vStride < 0 ? zone.bytesPerVertexMap.get( slice.vBufferKey ) : slice.vStride;
		
		int aVvecLoc = gl.glGetAttribLocation( mProgram , "aVvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aVvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aVvecLoc , 3 , GL2ES2.GL_FLOAT , false , vStride , slice.vOffset );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , indexEbo );
		checkGLError( gl , "glBindBuffer" );
		
		gl.glDrawElements( GL2ES2.GL_TRIANGLES , slice.indexBuffer.capacity( ) , GL2ES2.GL_UNSIGNED_SHORT , 0 );
		checkGLError( gl , "glDrawElements" );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl , "glBindBuffer" );
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
	}
	
	public static class Factory implements ISliceRendererFactory<RainbowSlice>
	{
		private Factory( )
		{
			
		}
		
		@Override
		public ISliceRenderer<RainbowSlice> create( ZoneRenderer zoneRenderer , RainbowSlice slice )
		{
			return new RainbowSliceRenderer( zoneRenderer , slice ).transpose( true );
		}
	}
}
