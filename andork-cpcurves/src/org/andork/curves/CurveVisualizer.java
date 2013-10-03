package org.andork.curves;

import static org.andork.jogl.util.GLUtils.checkGLError;
import static org.andork.jogl.util.GLUtils.loadShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL3;

import org.andork.math.discrete.DiscreteMathUtils;
import org.andork.util.ArrayUtils;
import org.andork.vecmath.MatrixUtils;

public class CurveVisualizer
{
	
	// //////////////////////////////////////////////////////////////////////////////
	
	private String			vShaderCode;
	private String			fShaderCode;
	
	private static int		program;
	
	private int				vao;
	private int				vbo;
	private int				ebo;
	
	private ByteBuffer		verts;
	private ByteBuffer		indices;
	
	private int[ ]			xpow;
	private int[ ]			ypow;
	
	// //////////////////////////////////////////////////////////////////////////////
	
	public final int		degree;
	public final int		numDimensions;
	public final int		numPoints;
	
	public final double[ ]	controlPoints;
	
	// //////////////////////////////////////////////////////////////////////////////
	
	private int[ ][ ]		monomials;
	
	private double[ ]		solnMatrix;
	
	private double[ ]		coefficients;
	
	private float[ ]		floatCoefficients;
	
	public CurveVisualizer( int degree , int numDimensions )
	{
		this.degree = degree;
		this.numDimensions = numDimensions;
		
		monomials = DiscreteMathUtils.generateMonomials( degree , numDimensions + 1 );
		
		numPoints = monomials.length - 1;
		
		controlPoints = new double[ numDimensions * numPoints ];
		
		solnMatrix = new double[ monomials.length * numPoints ];
		
		coefficients = new double[ numPoints ];
		floatCoefficients = new float[ numPoints ];
		xpow = new int[ numPoints ];
		ypow = new int[ numPoints ];
		
		for( int i = 0 ; i < numPoints ; i++ )
		{
			xpow[ i ] = monomials[ i ][ 0 ];
			ypow[ i ] = monomials[ i ][ 1 ];
		}
	}
	
	public double eval( double[ ] point )
	{
		double sum = 0;
		
		for( int i = 0 ; i < numPoints ; i++ )
		{
			double product = coefficients[ i ];
			int[ ] monomial = monomials[ i ];
			for( int term = 0 ; term < monomial.length - 1 ; term++ )
			{
				product *= Math.pow( point[ term ] , monomial[ term ] );
			}
			sum += product;
		}
		
		return sum + 1;
	}
	
	public void recalculate( )
	{
		int m = monomials.length - 1;
		int n = monomials.length;
		
		for( int row = 0 ; row < m ; row++ )
		{
			solnMatrix[ row * n + m ] = 1;
		}
		
		for( int col = 0 ; col < m ; col++ )
		{
			int[ ] monomial = monomials[ col ];
			
			for( int row = 0 ; row < m ; row++ )
			{
				double product = 1;
				for( int dim = 0 ; dim < numDimensions ; dim++ )
				{
					product *= Math.pow( controlPoints[ row * numDimensions + dim ] , monomial[ dim ] );
				}
				solnMatrix[ row * n + col ] = product;
			}
		}
		
		int[ ] row_perms = new int[ n ];
		for( int i = 0 ; i < n ; i++ )
		{
			row_perms[ i ] = i;
		}
		
		MatrixUtils.gauss( solnMatrix , m , n , row_perms );
		
		MatrixUtils.backsubstitute( solnMatrix , m , n , row_perms , coefficients );
		
		for( int i = 0 ; i < m ; i++ )
		{
			floatCoefficients[ i ] = ( float ) coefficients[ i ];
		}
	}
	
	public void initGL( GL3 gl )
	{
		vShaderCode =
				"uniform mat4 u_mvpMatrix;" +
						"attribute vec3 a_position;" +
						"varying vec2 v_texcoord;" +
						"void main() {" +
						"  v_texcoord = vec2(a_position);" +
						"  gl_Position = u_mvpMatrix * vec4(a_position, 1.0);" +
						"}";
		fShaderCode =
				"uniform float u_coefficients[" + numPoints + "];" +
						"uniform int u_xpow[" + numPoints + "];" +
						"uniform int u_ypow[" + numPoints + "];" +
						"varying vec2 v_texcoord;" +
						"void main() {" +
						"  float value = 1.0;" +
						"  for (int i = 0; i < " + numPoints + "; i++) {" +
						"    float product = u_coefficients[i];" +
						"    for (int j = 0; j < u_xpow[i]; j++) {" +
						"      product *= v_texcoord.x;" +
						"    }" +
						"    for (int j = 0; j < u_ypow[i]; j++) {" +
						"      product *= v_texcoord.y;" +
						"    }" +
						// "    product *= pow(v_texcoord.x, float(u_xpow[i]));" +
						// "    product *= pow(v_texcoord.y, float(u_ypow[i]));" +
						"    value += product;" +
						"  }" +
						"  float intensity = atan(value) * 0.6366;" +
						// "  float intensity = 1.0 - abs(atan(value)) * 0.6366;" +
						
						// "  if (intensity < 0.333) {" +
						// "    gl_FragColor = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(0.45, 0.0, 0.5, 1.0), intensity * 3.0);" +
						// "  } else if (intensity < 0.666) {" +
						// "    gl_FragColor = mix(vec4(0.45, 0.0, 0.5, 1.0), vec4(0.8, 0.2, 0.0, 1.0), (intensity - 0.333) * 3.0);" +
						// "  } else {" +
						// "    gl_FragColor = mix(vec4(0.8, 0.2, 0.0, 1.0), vec4(1.0, 0.9, 0.0, 1.0), (intensity - 0.666) * 3.0);" +
						// "  }" +
						
						// "  float red = exp(-value*value/0.1);" +
						
						"  intensity = abs(intensity);" +
						"  float red = 0.0;" +
						"  if (intensity > 0.0) {" +
						"    red = intensity;" +
						"  }" +
						"  float blue = 0.0;" +
						"  if (intensity < 0.0) {" +
						"    blue = -intensity;" +
						"  }" +
						"  gl_FragColor = vec4(red, 0.0, blue, 1.0);" +
						"}";
		
		program = gl.glCreateProgram( );
		checkGLError( gl );
		
		int vShader = loadShader( gl , GL3.GL_VERTEX_SHADER , vShaderCode );
		checkGLError( gl );
		int fShader = loadShader( gl , GL3.GL_FRAGMENT_SHADER , fShaderCode );
		checkGLError( gl );
		
		gl.glAttachShader( program , vShader );
		checkGLError( gl );
		gl.glAttachShader( program , fShader );
		checkGLError( gl );
		gl.glLinkProgram( program );
		checkGLError( gl );
		
		int[ ] temp = new int[ 2 ];
		gl.glGenVertexArrays( 1 , temp , 0 );
		checkGLError( gl );
		vao = temp[ 0 ];
		
		gl.glBindVertexArray( vao );
		checkGLError( gl );
		
		gl.glGenBuffers( 2 , temp , 0 );
		checkGLError( gl );
		vbo = temp[ 0 ];
		ebo = temp[ 1 ];
		
		int vertexCount = 4;
		int bytesPerVertex = 4 * 3;
		int vertexBytes = vertexCount * bytesPerVertex;
		
		verts = ByteBuffer.allocate( vertexBytes );
		verts.order( ByteOrder.nativeOrder( ) );
		
		verts.putFloat( -100 ).putFloat( -100 ).putFloat( 0 );
		verts.putFloat( 100 ).putFloat( -100 ).putFloat( 0 );
		verts.putFloat( 100 ).putFloat( 100 ).putFloat( 0 );
		verts.putFloat( -100 ).putFloat( 100 ).putFloat( 0 );
		verts.position( 0 );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbo );
		checkGLError( gl );
		gl.glBufferData( GL3.GL_ARRAY_BUFFER , verts.capacity( ) , verts , GL3.GL_STATIC_DRAW );
		checkGLError( gl );
		
		int positionLoc = gl.glGetAttribLocation( program , "a_position" );
		checkGLError( gl );
		gl.glEnableVertexAttribArray( positionLoc );
		checkGLError( gl );
		gl.glVertexAttribPointer( positionLoc , 3 , GL3.GL_FLOAT , false , bytesPerVertex , 0 );
		checkGLError( gl );
		
		int indexCount = 6;
		int indexBytes = 2 * indexCount;
		
		indices = ByteBuffer.allocate( indexBytes );
		indices.order( ByteOrder.nativeOrder( ) );
		indices.putChar( ( char ) 0 ).putChar( ( char ) 1 ).putChar( ( char ) 2 );
		indices.putChar( ( char ) 2 ).putChar( ( char ) 3 ).putChar( ( char ) 0 );
		indices.position( 0 );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , ebo );
		checkGLError( gl );
		gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER , indices.capacity( ) , indices , GL3.GL_STATIC_DRAW );
		checkGLError( gl );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		checkGLError( gl );
	}
	
	public void draw( GL3 gl , float[ ] mvpMatrix )
	{
		gl.glUseProgram( program );
		checkGLError( gl );
		
		int mvpMatrixLoc = gl.glGetUniformLocation( program , "u_mvpMatrix" );
		checkGLError( gl );
		gl.glUniformMatrix4fv( mvpMatrixLoc , 1 , false , mvpMatrix , 0 );
		checkGLError( gl );
		
		int coefLoc = gl.glGetUniformLocation( program , "u_coefficients" );
		checkGLError( gl );
		gl.glUniform1fv( coefLoc , numPoints , floatCoefficients , 0 );
		checkGLError( gl );
		
		int xpowLoc = gl.glGetUniformLocation( program , "u_xpow" );
		checkGLError( gl );
		gl.glUniform1iv( xpowLoc , numPoints , xpow , 0 );
		checkGLError( gl );
		
		int ypowLoc = gl.glGetUniformLocation( program , "u_ypow" );
		checkGLError( gl );
		gl.glUniform1iv( ypowLoc , numPoints , ypow , 0 );
		checkGLError( gl );
		
		gl.glBindVertexArray( vao );
		checkGLError( gl );
		
		gl.glDrawElements( GL3.GL_TRIANGLES , 6 , GL3.GL_UNSIGNED_SHORT , 0 );
		checkGLError( gl );
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl );
	}
	
	public void printCoefficients( )
	{
		System.out.println( ArrayUtils.prettyPrint( coefficients , coefficients.length , 0 , coefficients.length , 0 , "%12.2f" ) );
	}
}
