package org.andork.torquescape.jogl.render;

import static org.andork.jogl.util.JOGLUtils.checkGLError;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.util.JOGLUtils;
import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.slice.ColorWaveSlice;


public class ColorWaveSliceRenderer implements ISliceRenderer<ColorWaveSlice>
{
	public static final Factory	FACTORY				= new Factory( );
	
	private final String		vertexShaderCode	=
															// This matrix member variable provides a hook to manipulate
															// the coordinates of the objects that use this vertex shader
															"precision highp float;" +
																	"uniform mat4 m;" +
																	"uniform mat4 v;" +
																	"uniform mat4 p;" +
																	"uniform float time;" +
																	"uniform float wavelength;" +
																	"uniform float velocity;" +
																	"uniform vec4 vAmbientColor1;" +
																	"uniform vec4 vAmbientColor2;" +
																	"uniform vec4 vDiffuseColor1;" +
																	"uniform vec4 vDiffuseColor2;" +
																	"attribute vec4 vPosition;" +
																	"attribute vec3 vNormal;" +
																	"attribute float vParam;" +
																	"varying vec3 v_fxnormal;" +
																	"varying vec4 vAmbientColor;" +
																	"varying vec4 vDiffuseColor;" +
																	"void main() {" +
																	// "  float f = sin(vParam * 6.28318512 / wavelength + velocity * time) * 0.5 + 0.5;" +
																	"  float f = sin(vParam * 6.28318512 / wavelength + velocity * time) * 0.5 + 0.5;" +
																	"  float f2 = 1.0 - f * f;" +
																	"  vAmbientColor = mix(vAmbientColor1, vAmbientColor2, f2);" +
																	"  vDiffuseColor = mix(vDiffuseColor1, vDiffuseColor2, f2);" +
																	// "  vAmbientColor = vAmbientColor1;" +
																	// "  vDiffuseColor = vDiffuseColor1;" +
																	"  v_fxnormal = normalize((v * m * vec4(vNormal, 0)).xyz);" +
																	"  gl_Position = p * v * m * vPosition;" +
																	"}";
	
	private final String		fragmentShaderCode	=
															"varying vec3 v_fxnormal;" +
																	"varying vec4 vAmbientColor;" +
																	"varying vec4 vDiffuseColor;" +
																	"void main() {" +
																	"  float intensity = dot(v_fxnormal, vec3(0.0, 0.0, 1.0));" +
																	"  gl_FragColor = mix(vAmbientColor, vDiffuseColor, intensity);" +
																	"}";
	
	private int					mProgram;
	
	private int					indexEbo;
	
	private Zone				zone;
	private ZoneRenderer		zoneRenderer;
	private ColorWaveSlice		slice;
	
	public ColorWaveSliceRenderer( ZoneRenderer zoneRenderer , ColorWaveSlice slice )
	{
		this.zoneRenderer = zoneRenderer;
		this.zone = zoneRenderer.zone;
		this.slice = slice;
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
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , zoneRenderer.vertVbo );
		
		int vPositionLoc = gl.glGetAttribLocation( mProgram , "vPosition" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vPositionLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vPositionLoc , 3 , GL2ES2.GL_FLOAT , false , zone.getBytesPerVertex( ) , 0 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		int vNormalLoc = gl.glGetAttribLocation( mProgram , "vNormal" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vNormalLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vNormalLoc , 3 , GL2ES2.GL_FLOAT , false , zone.getBytesPerVertex( ) , 12 );
		checkGLError( gl , "glVertexAttribPointer" );
		
		int vParamLoc = gl.glGetAttribLocation( mProgram , "vParam" );
		checkGLError( gl , "glGetAttribLocation" );
		gl.glEnableVertexAttribArray( vParamLoc );
		checkGLError( gl , "glEnableVertexAttribArray" );
		gl.glVertexAttribPointer( vParamLoc , 1 , GL2ES2.GL_FLOAT , false , zone.getBytesPerVertex( ) , 24 );
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
	
	long firstTime = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.torquescape.SliceRenderer#draw(float[], float[], org.andork.torquescape.model.Zone, org.andork.torquescape.model.ColorWaveSlice)
	 */
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n, float[ ] v, float[ ] p )
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
		
		int wavelengthLoc = gl.glGetUniformLocation( mProgram , "wavelength" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform1f( wavelengthLoc , slice.wavelength );
		checkGLError( gl , "glUniform1f" );
		
		int velocityLoc = gl.glGetUniformLocation( mProgram , "velocity" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform1f( velocityLoc , slice.velocity );
		checkGLError( gl , "glUniform1f" );
		
		if (firstTime == 0) {
			firstTime = System.currentTimeMillis( );
		}
		
		int timeLoc = gl.glGetUniformLocation( mProgram , "time" );
		checkGLError( gl , "glGetUniformLocation" );
		float time = ( float ) ( System.currentTimeMillis( ) - firstTime ) * 0.001f;
		System.out.println("time * velocity: " + (time * slice.velocity));
		gl.glUniform1f( timeLoc , time );
		checkGLError( gl , "glUniform1f" );
		
		int ambientLoc1 = gl.glGetUniformLocation( mProgram , "vAmbientColor1" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( ambientLoc1 , 1 , slice.ambientColor , 0 );
		checkGLError( gl , "glUniform4fv" );
		
		int ambientLoc2 = gl.glGetUniformLocation( mProgram , "vAmbientColor2" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( ambientLoc2 , 1 , slice.ambientColor , 4 );
		checkGLError( gl , "glUniform4fv" );
		
		int diffuseLoc1 = gl.glGetUniformLocation( mProgram , "vDiffuseColor1" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( diffuseLoc1 , 1 , slice.diffuseColor , 0 );
		checkGLError( gl , "glUniform4fv" );
		
		int diffuseLoc2 = gl.glGetUniformLocation( mProgram , "vDiffuseColor2" );
		checkGLError( gl , "glGetUniformLocation" );
		gl.glUniform4fv( diffuseLoc2 , 1 , slice.diffuseColor , 4 );
		checkGLError( gl , "glUniform4fv" );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , indexEbo );
		checkGLError( gl , "glBindBuffer" );

		gl.glDrawElements( GL2ES2.GL_TRIANGLES , slice.indexBuffer.capacity( ) , GL2ES2.GL_UNSIGNED_SHORT , 0 );
		checkGLError( gl , "glDrawElements" );
		
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl , "glBindBuffer" );
	}
	
	public static class Factory implements ISliceRendererFactory<ColorWaveSlice>
	{
		private Factory( )
		{
			
		}
		
		@Override
		public ISliceRenderer<ColorWaveSlice> create( ZoneRenderer zoneRenderer , ColorWaveSlice slice )
		{
			return new ColorWaveSliceRenderer( zoneRenderer , slice );
		}
	}
}
