package org.andork.torquescape.jogl.render;

import static org.andork.jogl.util.GLUtils.checkGLError;

import javax.media.opengl.GL3;

import org.andork.jogl.util.GLUtils;
import org.andork.torquescape.model.RainbowSlice;
import org.andork.torquescape.model.Zone;

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
		checkGLError( gl , "glCreateProgram" );
		gl.glAttachShader( mProgram , vertexShader ); // add the vertex shader
		checkGLError( gl , "glAttachShader" );
		// to program
		gl.glAttachShader( mProgram , fragmentShader ); // add the fragment
		checkGLError( gl , "glAttachShader" );
		// shader to program
		gl.glLinkProgram( mProgram ); // creates OpenGL ES program executables
		checkGLError( gl , "glLinkProgram" );
		
		int[ ] vaos = new int[ 1 ];
		int[ ] vbos = new int[ 1 ];
		
		gl.glGenVertexArrays( 1 , vaos , 0 );
		checkGLError( gl , "glGenVertexArrays" );
		
		gl.glGenBuffers( 1 , vbos , 0 );
		checkGLError( gl , "glGenBuffers" );
		
		vao = vaos[ 0 ];
		indexEbo = vbos[ 0 ];
		
		gl.glBindVertexArray( vao );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , zoneRenderer.vertVbo );
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "aPosition" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 0 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		int aNormalLoc = gl.glGetAttribLocation( mProgram , "aNormal" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aNormalLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aNormalLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 12 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		int aUvecLoc = gl.glGetAttribLocation( mProgram , "aUvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aUvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aUvecLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 28 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		int aVvecLoc = gl.glGetAttribLocation( mProgram , "aVvec" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( aVvecLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( aVvecLoc , 3 , GL3.GL_FLOAT , false , zone.getBytesPerVertex( ) , 40 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		
		slice.indexBuffer.position( 0 );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , indexEbo );
		checkGLError( gl , "glBindBuffer" );
		gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER , slice.indexBuffer.capacity( ) * 2 , slice.indexBuffer , GL3.GL_STATIC_DRAW );
		checkGLError( gl , "glBufferData" );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , "glBindVertexArray" );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl , "glBindBuffer" );
	}
	
	long	firstTime	= 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[], org.andork.torquescape.model.Zone, org.andork.torquescape.model.RainbowSlice)
	 */
	@Override
	public void draw( GL3 gl , float[ ] m , float[ ] v , float[ ] p )
	{
		gl.glUseProgram( mProgram );
		checkGLError( gl , "glUseProgram" );
		
		int m_loc = gl.glGetUniformLocation( mProgram , "m" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( m_loc , 1 , false , m , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		int v_loc = gl.glGetUniformLocation( mProgram , "v" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( v_loc , 1 , false , v , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		int p_loc = gl.glGetUniformLocation( mProgram , "p" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniformMatrix4fv( p_loc , 1 , false , p , 0 );
		checkGLError( gl , "glUniformMatrix4fv" );
		
		if( firstTime == 0 )
		{
			firstTime = System.currentTimeMillis( );
		}
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , "glBindVertexArray" );
		
		gl.glDrawElements( GL3.GL_TRIANGLES , slice.indexBuffer.capacity( ) , GL3.GL_UNSIGNED_SHORT , 0 );
		checkGLError( gl , "glDrawElements" );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , "glBindVertexArray" );
		
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
