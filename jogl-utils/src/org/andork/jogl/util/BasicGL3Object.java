package org.andork.jogl.util;

import static org.andork.jogl.util.GLUtils.checkGLError;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL3;

public class BasicGL3Object implements GL3Object
{
	ByteBuffer[ ]			vertexBuffers	= new ByteBuffer[ 0 ];
	int[ ]					offsets;
	int[ ]					strides;
	int[ ]					vbos;
	int						vertexCount;
	
	Buffer					indexBuffer;
	int						indexType;
	int						ebo;
	
	int						vao;
	
	int						program;
	
	String					vertexShaderCode;
	String					fragmentShaderCode;
	
	String					modelMatrixName	= "m";
	String					viewMatrixName	= "v";
	String					projMatrixName	= "p";
	
	boolean					transpose		= false;
	
	final List<Uniform>		uniforms		= new ArrayList<Uniform>( );
	final List<Attribute>	attributes		= new ArrayList<Attribute>( );
	final List<GL3Modifier>	modifiers		= new ArrayList<GL3Modifier>( );
	
	boolean					debug;
	
	int						drawMode;
	
	public ByteBuffer addVertexBuffer( int capacity )
	{
		ByteBuffer newBuffer = ByteBuffer.allocateDirect( capacity );
		newBuffer.order( ByteOrder.nativeOrder( ) );
		vertexBuffers = Arrays.copyOf( vertexBuffers , vertexBuffers.length + 1 );
		vertexBuffers[ vertexBuffers.length - 1 ] = newBuffer;
		return newBuffer;
	}
	
	public BasicGL3Object addVertexBuffer( ByteBuffer newBuffer )
	{
		vertexBuffers = Arrays.copyOf( vertexBuffers , vertexBuffers.length + 1 );
		vertexBuffers[ vertexBuffers.length - 1 ] = newBuffer;
		return this;
	}
	
	public ByteBuffer vertexBuffer( int index )
	{
		return vertexBuffers[ index ];
	}
	
	public BasicGL3Object vertexBuffer( int index , ByteBuffer newBuffer )
	{
		vertexBuffers[ index ] = newBuffer;
		return this;
	}
	
	public BasicGL3Object vertexShaderCode( String vertexShaderCode )
	{
		this.vertexShaderCode = vertexShaderCode;
		return this;
	}
	
	public BasicGL3Object fragmentShaderCode( String fragmentShaderCode )
	{
		this.fragmentShaderCode = fragmentShaderCode;
		return this;
	}
	
	public BasicGL3Object modelMatrixName( String modelMatrixName )
	{
		this.modelMatrixName = modelMatrixName;
		return this;
	}
	
	public BasicGL3Object viewMatrixName( String viewMatrixName )
	{
		this.viewMatrixName = viewMatrixName;
		return this;
	}
	
	public BasicGL3Object projMatrixName( String projMatrixName )
	{
		this.projMatrixName = projMatrixName;
		return this;
	}
	
	public BasicGL3Object vertexCount( int vertexCount )
	{
		this.vertexCount = vertexCount;
		return this;
	}
	
	public BasicGL3Object debug( boolean debug )
	{
		this.debug = debug;
		return this;
	}
	
	public BasicGL3Object transpose( boolean transpose )
	{
		this.transpose = transpose;
		return this;
	}
	
	public BasicGL3Object drawMode( int drawMode )
	{
		this.drawMode = drawMode;
		return this;
	}
	
	public BasicGL3Object add( Uniform uniform )
	{
		uniforms.add( uniform );
		return this;
	}
	
	public BasicGL3Object add( Attribute attribute )
	{
		attributes.add( attribute );
		return this;
	}
	
	public BasicGL3Object add( GL3Modifier modifier )
	{
		modifiers.add( modifier );
		return this;
	}
	
	public void init( GL3 gl )
	{
		program = GLUtils.loadProgram( gl , vertexShaderCode , fragmentShaderCode , debug );
		
		int[ ] temp = new int[ 1 ];
		
		gl.glGenVertexArrays( 1 , temp , 0 );
		vao = temp[ 0 ];
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , debug );
		
		vbos = new int[ vertexBuffers.length ];
		
		gl.glGenBuffers( vertexBuffers.length , vbos , 0 );
		checkGLError( gl , debug );
		
		rebufferVertices( gl );
		
		offsets = new int[ vertexBuffers.length ];
		strides = new int[ vertexBuffers.length ];
		
		for( Attribute attribute : attributes )
		{
			int bi = attribute.getBufferIndex( );
			int bytes = attribute.getNumBytes( );
			strides[ bi ] += bytes;
		}
		
		for( Attribute attribute : attributes )
		{
			int bi = attribute.getBufferIndex( );
			int bytes = attribute.getNumBytes( );
			
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbos[ bi ] );
			
			attribute.put( gl , strides[ bi ] , offsets[ bi ] );
			
			offsets[ bi ] += bytes;
		}
		
		if( indexBuffer != null )
		{
			gl.glGenBuffers( 1 , temp , 0 );
			checkGLError( gl , debug );
			
			ebo = temp[ 0 ];
			
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , ebo );
			checkGLError( gl , debug );
			
			indexBuffer.position( 0 );
			
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER , indexBuffer.capacity( ) , indexBuffer , GL3.GL_STATIC_DRAW );
			checkGLError( gl , debug );
		}
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , debug );
		
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , 0 );
		checkGLError( gl , debug );
		
		gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER , 0 );
		checkGLError( gl , debug );
	}
	
	public void draw( GL3 gl , float[ ] m , float[ ] v , float[ ] p )
	{
		for( GL3Modifier modifier : modifiers )
		{
			modifier.beforeDraw( gl , this );
		}
		
		gl.glUseProgram( program );
		checkGLError( gl , debug );
		
		gl.glBindVertexArray( vao );
		checkGLError( gl , debug );
		
		for( Uniform uniform : uniforms )
		{
			uniform.put( gl );
		}
		
		if( modelMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , modelMatrixName );
			checkGLError( gl , debug );
			
			gl.glUniformMatrix4fv( m_location , 1 , transpose , m , 0 );
			checkGLError( gl , debug );
		}
		
		if( viewMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , viewMatrixName );
			checkGLError( gl , debug );
			
			gl.glUniformMatrix4fv( m_location , 1 , transpose , v , 0 );
			checkGLError( gl , debug );
		}
		
		if( projMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , projMatrixName );
			checkGLError( gl , debug );
			
			gl.glUniformMatrix4fv( m_location , 1 , transpose , p , 0 );
			checkGLError( gl , debug );
		}
		
		if( indexBuffer != null )
		{
			gl.glDrawElements( drawMode , indexBuffer.capacity( ) , indexType , indexBuffer );
		}
		else
		{
			gl.glDrawArrays( drawMode , 0 , vertexCount );
		}
		
		gl.glBindVertexArray( 0 );
		checkGLError( gl , debug );
		
		for( GL3Modifier modifier : modifiers )
		{
			modifier.afterDraw( gl , this );
		}
	}
	
	public void rebufferVertices( GL3 gl )
	{
		for( int i = 0 ; i < vertexBuffers.length ; i++ )
		{
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER , vbos[ i ] );
			checkGLError( gl , debug );
			
			vertexBuffers[ i ].position( 0 );
			
			gl.glBufferData( GL3.GL_ARRAY_BUFFER , vertexBuffers[ i ].capacity( ) , vertexBuffers[ i ] , GL3.GL_STATIC_DRAW );
			checkGLError( gl , debug );
		}
	}
	
	public abstract class Uniform
	{
		protected String	name;
		
		public String getName( )
		{
			return name;
		}
		
		public abstract void put( GL3 gl );
	}
	
	public class UniformMatrix4fv extends Uniform
	{
		int			count;
		boolean		transpose;
		float[ ]	value;
		int			value_offset;
		
		public UniformMatrix4fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public UniformMatrix4fv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public UniformMatrix4fv transpose( boolean transpose )
		{
			this.transpose = transpose;
			return this;
		}
		
		public UniformMatrix4fv value( float[ ] value )
		{
			this.value = value;
			return this;
		}
		
		public UniformMatrix4fv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL3 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			gl.glUniformMatrix4fv( location , count , transpose , value , value_offset );
			checkGLError( gl );
		}
	}
	
	public abstract class Attribute
	{
		protected String	name;
		protected int		bufferIndex;
		
		public String getName( )
		{
			return name;
		}
		
		public int getBufferIndex( )
		{
			return bufferIndex;
		}
		
		public abstract int getNumBytes( );
		
		public abstract void put( GL3 gl , int stride , int offset );
	}
	
	public class Attribute1fv extends Attribute
	{
		public int getNumBytes( )
		{
			return 4;
		}
		
		public Attribute1fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Attribute1fv bufferIndex( int bufferIndex )
		{
			this.bufferIndex = bufferIndex;
			return this;
		}
		
		@Override
		public void put( GL3 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 1 , GL3.GL_FLOAT , false , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x )
		{
			vertexBuffers[ bufferIndex ].putFloat( x );
		}
	}
	
	public class AttributeVec3fv extends Attribute
	{
		boolean	normalized;
		
		public int getNumBytes( )
		{
			return 12;
		}
		
		public AttributeVec3fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public AttributeVec3fv bufferIndex( int bufferIndex )
		{
			this.bufferIndex = bufferIndex;
			return this;
		}
		
		public AttributeVec3fv normalized( boolean normalized )
		{
			this.normalized = normalized;
			return this;
		}
		
		@Override
		public void put( GL3 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 3 , GL3.GL_FLOAT , normalized , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x , float y , float z )
		{
			vertexBuffers[ bufferIndex ].putFloat( x ).putFloat( y ).putFloat( z );
		}
		
		public void put( float[ ] values , int offset )
		{
			vertexBuffers[ bufferIndex ].putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] );
		}
	}
	
	public static class BasicVertexShader
	{
		int		posDim					= 3;
		boolean	passPosToFragmentShader	= false;
		
		public BasicVertexShader posDim( int posDim )
		{
			this.posDim = posDim;
			return this;
		}
		
		public BasicVertexShader passPosToFragmentShader( boolean passPosToFragmentShader )
		{
			this.passPosToFragmentShader = passPosToFragmentShader;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m;" );
			sb.append( "uniform mat4 v;" );
			sb.append( "uniform mat4 p;" );
			sb.append( "attribute vec" ).append( posDim ).append( " a_pos;" );
			if( passPosToFragmentShader )
			{
				sb.append( "varying vec" ).append( posDim ).append( " v_pos;" );
			}
			sb.append( "void main() {" );
			sb.append( "  mat4 mvp = p*v*m;" );
			switch( posDim )
			{
				case 2:
					sb.append( "  gl_Position = mvp * vec4(a_pos, 0.0, 1.0);" );
					break;
				case 3:
					sb.append( "  gl_Position = mvp * vec4(a_pos, 1.0);" );
					break;
				case 4:
					sb.append( "  gl_Position = mvp * a_pos;" );
					break;
			}
			if( passPosToFragmentShader )
			{
				sb.append( "  v_pos = vec3(gl_Position);" );
			}
			sb.append( "}" );
			
			return sb.toString( );
		}
	}
	
	public static class FlatFragmentShader
	{
		final float[ ]	color	= { 1f , 1f , 1f , 1f };
		
		public FlatFragmentShader color( float r , float g , float b , float a )
		{
			color[ 0 ] = r;
			color[ 1 ] = g;
			color[ 2 ] = b;
			color[ 3 ] = a;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "void main() {" );
			sb.append( "  gl_FragColor = vec4(" );
			sb.append( String.format( "%.4f" , color[ 0 ] ) );
			for( int i = 1 ; i < 4 ; i++ )
			{
				sb.append( String.format( ", %.4f" , color[ i ] ) );
			}
			sb.append( ");" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
	
	public static class DepthFragmentShader
	{
		final float[ ]	color	= { 1f , 1f , 1f , 1f };
		
		public DepthFragmentShader color( float r , float g , float b , float a )
		{
			color[ 0 ] = r;
			color[ 1 ] = g;
			color[ 2 ] = b;
			color[ 3 ] = a;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "varying vec3 v_pos;" );
			sb.append( "void main() {" );
			sb.append( "  float intensity = 1.0 / (v_pos.z / 2.0 + 1.0);" );
			sb.append( "  gl_FragColor = intensity * vec4(" );
			sb.append( String.format( "%.4f" , color[ 0 ] ) );
			for( int i = 1 ; i < 4 ; i++ )
			{
				sb.append( String.format( ", %.4f" , color[ i ] ) );
			}
			sb.append( ");" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
}