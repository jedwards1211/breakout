package org.andork.jogl.basic;

import static org.andork.jogl.util.JOGLUtils.checkGLError;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

import org.andork.jogl.util.JOGLUtils;

public class BasicJOGLObject implements JOGLObject
{
	SharedBuffer[ ]				vertexBuffers			= new SharedBuffer[ 0 ];
	int[ ]						offsets;
	int[ ]						strides;
	int							vertexCount;
	
	SharedBuffer				indexBuffer;
	int							indexCount;
	int							indexType;
	
	int							program;
	int							vertexShader;
	int							fragmentShader;
	
	String						vertexShaderCode;
	String						fragmentShaderCode;
	
	String						modelMatrixName			= "m";
	String						normalMatrixName		= null;
	String						viewMatrixName			= "v";
	String						projMatrixName			= "p";
	
	boolean						transpose				= false;
	
	final List<Uniform>			uniforms				= new ArrayList<Uniform>( );
	final List<Attribute>		attributes				= new ArrayList<Attribute>( );
	final List<JOGLModifier>	modifiers				= new ArrayList<JOGLModifier>( );
	
	int							drawMode;
	
	private boolean				initialized				= false;
	
	boolean						ignoreMissingLocations	= false;
	
	public ByteBuffer addVertexBuffer( int capacity )
	{
		ByteBuffer newBuffer = ByteBuffer.allocateDirect( capacity );
		newBuffer.order( ByteOrder.nativeOrder( ) );
		addVertexBuffer( newBuffer );
		return newBuffer;
	}
	
	public BasicJOGLObject addVertexBuffer( ByteBuffer newBuffer )
	{
		return addVertexBuffer( new SharedBuffer( ).buffer( newBuffer ) );
	}
	
	public BasicJOGLObject addVertexBuffer( SharedBuffer newBuffer )
	{
		vertexBuffers = Arrays.copyOf( vertexBuffers , vertexBuffers.length + 1 );
		vertexBuffers[ vertexBuffers.length - 1 ] = newBuffer;
		return this;
	}
	
	public ByteBuffer vertexBuffer( int index )
	{
		return vertexBuffers[ index ].buffer( );
	}
	
	public BasicJOGLObject vertexBuffer( int index , ByteBuffer newBuffer )
	{
		vertexBuffers[ index ].buffer( newBuffer );
		return this;
	}
	
	public SharedBuffer indexBuffer( )
	{
		return indexBuffer;
	}
	
	public BasicJOGLObject indexBuffer( SharedBuffer newBuffer )
	{
		indexBuffer = newBuffer;
		return this;
	}
	
	public BasicJOGLObject indexBuffer( ByteBuffer newBuffer )
	{
		return indexBuffer( new SharedBuffer( ).target( GL.GL_ELEMENT_ARRAY_BUFFER ).buffer( newBuffer ) );
	}
	
	public BasicJOGLObject indexType( int indexType )
	{
		this.indexType = indexType;
		return this;
	}
	
	public BasicJOGLObject indexCount( int indexCount )
	{
		this.indexCount = indexCount;
		return this;
	}
	
	public BasicJOGLObject vertexShaderCode( String vertexShaderCode )
	{
		this.vertexShaderCode = vertexShaderCode;
		return this;
	}
	
	public BasicJOGLObject fragmentShaderCode( String fragmentShaderCode )
	{
		this.fragmentShaderCode = fragmentShaderCode;
		return this;
	}
	
	public BasicJOGLObject normalMatrixName( String normalMatrixName )
	{
		this.normalMatrixName = normalMatrixName;
		return this;
	}
	
	public BasicJOGLObject modelMatrixName( String modelMatrixName )
	{
		this.modelMatrixName = modelMatrixName;
		return this;
	}
	
	public BasicJOGLObject viewMatrixName( String viewMatrixName )
	{
		this.viewMatrixName = viewMatrixName;
		return this;
	}
	
	public BasicJOGLObject projMatrixName( String projMatrixName )
	{
		this.projMatrixName = projMatrixName;
		return this;
	}
	
	public BasicJOGLObject vertexCount( int vertexCount )
	{
		this.vertexCount = vertexCount;
		return this;
	}
	
	public BasicJOGLObject transpose( boolean transpose )
	{
		this.transpose = transpose;
		return this;
	}
	
	public BasicJOGLObject ignoreMissingLocations( boolean ignoreMissingLocations )
	{
		this.ignoreMissingLocations = ignoreMissingLocations;
		return this;
	}
	
	public BasicJOGLObject drawMode( int drawMode )
	{
		this.drawMode = drawMode;
		return this;
	}
	
	public BasicJOGLObject add( Uniform uniform )
	{
		uniforms.add( uniform );
		return this;
	}
	
	public BasicJOGLObject add( Attribute attribute )
	{
		attributes.add( attribute );
		return this;
	}
	
	public BasicJOGLObject add( JOGLModifier modifier )
	{
		modifiers.add( modifier );
		return this;
	}
	
	public void init( GL2ES2 gl )
	{
		if( initialized )
		{
			return;
		}
		initialized = true;
		
		vertexShader = JOGLUtils.loadShader( gl , GL2ES2.GL_VERTEX_SHADER , vertexShaderCode );
		fragmentShader = JOGLUtils.loadShader( gl , GL2ES2.GL_FRAGMENT_SHADER , fragmentShaderCode );
		program = JOGLUtils.loadProgram( gl , vertexShader , fragmentShader );
		
		int[ ] temp = new int[ 1 ];
		
		for( SharedBuffer buffer : vertexBuffers )
		{
			buffer.init( gl );
		}
		
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
			
			gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , vertexBuffers[ bi ].id( ) );
			
			attribute.put( gl , strides[ bi ] , offsets[ bi ] );
			
			offsets[ bi ] += bytes;
		}
		
		if( indexBuffer != null )
		{
			indexBuffer.init( gl );
		}
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
		gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , 0 );
	}
	
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		for( JOGLModifier modifier : modifiers )
		{
			modifier.beforeDraw( gl , this );
		}
		
		gl.glUseProgram( program );
		
		for( Uniform uniform : uniforms )
		{
			uniform.put( gl );
		}
		
		if( modelMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , modelMatrixName );
			gl.glUniformMatrix4fv( m_location , 1 , transpose , m , 0 );
		}
		
		if( viewMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , viewMatrixName );
			gl.glUniformMatrix4fv( m_location , 1 , transpose , v , 0 );
		}
		
		if( projMatrixName != null )
		{
			int m_location = gl.glGetUniformLocation( program , projMatrixName );
			gl.glUniformMatrix4fv( m_location , 1 , transpose , p , 0 );
		}
		if( normalMatrixName != null )
		{
			int n_location = gl.glGetUniformLocation( program , normalMatrixName );
			gl.glUniformMatrix3fv( n_location , 1 , transpose , n , 0 );
		}
		
		Arrays.fill( offsets , 0 );
		
		for( Attribute attribute : attributes )
		{
			int bi = attribute.getBufferIndex( );
			int bytes = attribute.getNumBytes( );
			
			gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , vertexBuffers[ bi ].id( ) );
			
			attribute.put( gl , strides[ bi ] , offsets[ bi ] );
			
			offsets[ bi ] += bytes;
		}
		
		if( indexBuffer != null )
		{
			gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , indexBuffer.id( ) );
			gl.glDrawElements( drawMode , indexCount , indexType , 0 );
			gl.glBindBuffer( GL2ES2.GL_ELEMENT_ARRAY_BUFFER , 0 );
		}
		else
		{
			gl.glDrawArrays( drawMode , 0 , vertexCount );
		}
		
		gl.glBindBuffer( GL2ES2.GL_ARRAY_BUFFER , 0 );
		
		for( JOGLModifier modifier : modifiers )
		{
			modifier.afterDraw( gl , this );
		}
	}
	
	public void destroy( GL2ES2 gl )
	{
		if( initialized )
		{
			for( SharedBuffer buffer : vertexBuffers )
			{
				buffer.destroy( gl );
			}
			
			if( indexBuffer != null )
			{
				indexBuffer.destroy( gl );
			}
			
			gl.glDetachShader( program , vertexShader );
			gl.glDetachShader( program , fragmentShader );
			gl.glDeleteShader( vertexShader );
			gl.glDeleteShader( fragmentShader );
			gl.glDeleteProgram( program );
			
			initialized = false;
		}
	}
	
	public void rebufferVertices( GL2ES2 gl )
	{
		for( SharedBuffer buffer : vertexBuffers )
		{
			buffer.rebuffer( gl );
		}
	}
	
	public abstract class Uniform
	{
		protected String	name;
		
		public String getName( )
		{
			return name;
		}
		
		public abstract void put( GL2ES2 gl );
	}
	
	public class Uniform1iv extends Uniform
	{
		int		count	= 1;
		int[ ]	value;
		int		value_offset;
		
		public Uniform1iv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Uniform1iv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public Uniform1iv value( int ... value )
		{
			this.value = value;
			return this;
		}
		
		public Uniform1iv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glUniform1iv( location , count , value , value_offset );
			checkGLError( gl );
		}
	}
	
	public class Uniform1fv extends Uniform
	{
		int			count	= 1;
		float[ ]	value;
		int			value_offset;
		
		public Uniform1fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Uniform1fv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public Uniform1fv value( float ... value )
		{
			this.value = value;
			return this;
		}
		
		public Uniform1fv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glUniform1fv( location , count , value , value_offset );
			checkGLError( gl );
		}
	}
	
	public class Uniform2fv extends Uniform
	{
		int			count	= 1;
		float[ ]	value;
		int			value_offset;
		
		public Uniform2fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Uniform2fv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public Uniform2fv value( float ... value )
		{
			this.value = value;
			return this;
		}
		
		public Uniform2fv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glUniform2fv( location , count , value , value_offset );
			checkGLError( gl );
		}
	}
	
	public class Uniform3fv extends Uniform
	{
		int			count	= 1;
		float[ ]	value;
		int			value_offset;
		
		public Uniform3fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Uniform3fv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public Uniform3fv value( float ... value )
		{
			this.value = value;
			return this;
		}
		
		public Uniform3fv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glUniform3fv( location , count , value , value_offset );
			checkGLError( gl );
		}
	}
	
	public class Uniform4fv extends Uniform
	{
		int			count	= 1;
		float[ ]	value;
		int			value_offset;
		
		public Uniform4fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Uniform4fv count( int count )
		{
			this.count = count;
			return this;
		}
		
		public Uniform4fv value( float ... value )
		{
			this.value = value;
			return this;
		}
		
		public Uniform4fv value_offset( int value_offset )
		{
			this.value_offset = value_offset;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glUniform4fv( location , count , value , value_offset );
			checkGLError( gl );
		}
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
		public void put( GL2ES2 gl )
		{
			int location = gl.glGetUniformLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
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
		
		public abstract void put( GL2ES2 gl , int stride , int offset );
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
		public void put( GL2ES2 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 1 , GL2ES2.GL_FLOAT , false , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( x );
		}
	}
	
	public class Attribute2fv extends Attribute
	{
		boolean	normalized;
		
		public int getNumBytes( )
		{
			return 8;
		}
		
		public Attribute2fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Attribute2fv bufferIndex( int bufferIndex )
		{
			this.bufferIndex = bufferIndex;
			return this;
		}
		
		public Attribute2fv normalized( boolean normalized )
		{
			this.normalized = normalized;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 2 , GL2ES2.GL_FLOAT , normalized , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x , float y )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( x ).putFloat( y );
		}
		
		public void put( float[ ] values , int offset )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] );
		}
	}
	
	public class Attribute3fv extends Attribute
	{
		boolean	normalized;
		
		public int getNumBytes( )
		{
			return 12;
		}
		
		public Attribute3fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Attribute3fv bufferIndex( int bufferIndex )
		{
			this.bufferIndex = bufferIndex;
			return this;
		}
		
		public Attribute3fv normalized( boolean normalized )
		{
			this.normalized = normalized;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 3 , GL2ES2.GL_FLOAT , normalized , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x , float y , float z )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( x ).putFloat( y ).putFloat( z );
		}
		
		public void put( float[ ] values , int offset )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] );
		}
	}
	
	public class Attribute4fv extends Attribute
	{
		boolean	normalized;
		
		public int getNumBytes( )
		{
			return 16;
		}
		
		public Attribute4fv name( String name )
		{
			this.name = name;
			return this;
		}
		
		public Attribute4fv bufferIndex( int bufferIndex )
		{
			this.bufferIndex = bufferIndex;
			return this;
		}
		
		public Attribute4fv normalized( boolean normalized )
		{
			this.normalized = normalized;
			return this;
		}
		
		@Override
		public void put( GL2ES2 gl , int stride , int offset )
		{
			int location = gl.glGetAttribLocation( program , name );
			checkGLError( gl );
			
			if( ignoreMissingLocations && location < 0 )
			{
				return;
			}
			
			gl.glEnableVertexAttribArray( location );
			checkGLError( gl );
			
			gl.glVertexAttribPointer( location , 4 , GL2ES2.GL_FLOAT , normalized , stride , offset );
			checkGLError( gl );
		}
		
		public void put( float x , float y , float z , float w )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( x ).putFloat( y ).putFloat( z ).putFloat( w );
		}
		
		public void put( float[ ] values , int offset )
		{
			vertexBuffers[ bufferIndex ].buffer( ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] ).putFloat( values[ offset++ ] );
		}
	}
	
	public class PlaceholderAttribute extends Attribute
	{
		int	numBytes;
		
		public PlaceholderAttribute( int numBytes )
		{
			super( );
			this.numBytes = numBytes;
		}
		
		@Override
		public int getNumBytes( )
		{
			return numBytes;
		}
		
		@Override
		public void put( GL2ES2 gl , int stride , int offset )
		{
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
			sb.append("  gl_Position.z += 0.05;");
			if( passPosToFragmentShader )
			{
				sb.append( "  v_pos = vec3(a_pos);" );
			}
			sb.append( "}" );
			
			return sb.toString( );
		}
	}
	
	public static class PerVertexDiffuseVertexShader
	{
		int	nlights	= 1;
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m, v, p;" );
			sb.append( "uniform mat3 n;" );
			sb.append( "attribute vec3 a_pos;" );
			sb.append( "attribute vec3 a_norm;" );
			sb.append( "uniform vec4 u_color;" );
			
			sb.append( "varying vec4 v_color;" );
			
			sb.append( "uniform int u_nlights;" );
			sb.append( "uniform vec4 u_lightpos[" ).append( nlights ).append( "];" );
			sb.append( "uniform vec4 u_lightcolor[" ).append( nlights ).append( "];" );
			sb.append( "uniform float u_constantAttenuation[" ).append( nlights ).append( "];" );
			sb.append( "uniform float u_linearAttenuation[" ).append( nlights ).append( "];" );
			sb.append( "uniform float u_quadraticAttenuation[" ).append( nlights ).append( "];" );
			
			sb.append( "void main(void)" );
			sb.append( "{" );
			sb.append( "  mat4 mvp = p*v*m;" );
			sb.append( "  vec3 normalDirection = normalize(n * a_norm);" );
			sb.append( "  vec3 lightDirection;" );
			sb.append( "  float attenuation;" );
			sb.append( "  v_color = vec4(0.0, 0.0, 0.0, 1.0);" );
			
			sb.append( "  for (int i = 0; i < u_nlights; i++) {" );
			sb.append( "    if (u_lightpos[i].w == 0.0)" );
			sb.append( "    {" );
			sb.append( "      attenuation = 1.0;" );
			sb.append( "      lightDirection = normalize(vec3(u_lightpos[i]));" );
			sb.append( "    }" );
			sb.append( "    else" );
			sb.append( "    {" );
			sb.append( "      vec3 vertexToLightSource = vec3(u_lightpos[i] - m * vec4(a_pos, 1.0));" );
			sb.append( "      float distance = length(vertexToLightSource);" );
			sb.append( "      lightDirection = normalize(vertexToLightSource);" );
			sb.append( "      attenuation = 1.0 / (u_constantAttenuation[i]" );
			sb.append( "                         + u_linearAttenuation[i] * distance" );
			sb.append( "                         + u_quadraticAttenuation[i] * distance * distance);" );
			sb.append( "    }" );
			
			sb.append( "    vec3 diffuseReflection = attenuation" );
			sb.append( "      * vec3(u_lightcolor[i]) * vec3(u_color)" );
			sb.append( "      * max(0.0, dot(normalDirection, lightDirection));" );
			sb.append( "    v_color += vec4(diffuseReflection, 0.0);" );
			sb.append( "  }" );
			
			sb.append( "  gl_Position = mvp * vec4(a_pos, 1.0);" );
			sb.append( "}" );
			
			return sb.toString( );
		}
	}
	
	public static class VaryingColorFragmentShader
	{
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "varying vec4 v_color;" );
			sb.append( "void main() {" );
			sb.append( "  gl_FragColor = v_color;" );
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
	
	public static class DistanceFragmentShader
	{
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m;" );
			sb.append( "uniform mat4 v;" );
			sb.append( "uniform mat4 p;" );
			sb.append( "uniform float nearDist;" );
			sb.append( "uniform vec4 nearColor;" );
			sb.append( "uniform float farDist;" );
			sb.append( "uniform vec4 farColor;" );
			sb.append( "varying vec3 v_pos;" );
			sb.append( "void main() {" );
			sb.append( "  float frag_depth = -(v * m * vec4(v_pos, 1.0)).z;" );
			sb.append( "  float f;" );
			sb.append( "  if (frag_depth > farDist) { f = 1.0; }" );
			sb.append( "  else if (frag_depth < nearDist) { f = 0.0; }" );
			sb.append( "  else { f = (frag_depth - nearDist) / (farDist - nearDist); }" );
			sb.append( "  gl_FragColor = mix(nearColor, farColor, f); " );
			sb.append( "}" );
			return sb.toString( );
		}
	}
	
	public static class DepthFragmentShader
	{
		final float[ ]	farColor	= { 0.1f , 0.1f , 0.1f , 1f };
		final float[ ]	nearColor	= { 1f , 1f , 1f , 1f };
		final float[ ]	center		= { 0 , 0 , 0 };
		float			radius		= 1;
		
		public DepthFragmentShader nearColor( float r , float g , float b , float a )
		{
			nearColor[ 0 ] = r;
			nearColor[ 1 ] = g;
			nearColor[ 2 ] = b;
			nearColor[ 3 ] = a;
			return this;
		}
		
		public DepthFragmentShader farColor( float r , float g , float b , float a )
		{
			farColor[ 0 ] = r;
			farColor[ 1 ] = g;
			farColor[ 2 ] = b;
			farColor[ 3 ] = a;
			return this;
		}
		
		public DepthFragmentShader center( float x , float y , float z )
		{
			center[ 0 ] = x;
			center[ 1 ] = y;
			center[ 2 ] = z;
			return this;
		}
		
		public DepthFragmentShader radius( float radius )
		{
			if( radius < 0 )
			{
				throw new IllegalArgumentException( "radius must be >= 0" );
			}
			this.radius = radius;
			return this;
		}
		
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m;" );
			sb.append( "uniform mat4 v;" );
			sb.append( "uniform mat4 p;" );
			sb.append( "varying vec3 v_pos;" );
			sb.append( "void main() {" );
			sb.append( "  float center_depth = (v * m * vec4(" );
			sb.append( format( "%.4f" , center[ 0 ] , center[ 1 ] , center[ 2 ] , 1f ) ).append( ")).z;" );
			sb.append( "  float radius = " ).append( format( "%.4f" , radius ) ).append( ";" );
			sb.append( "  float frag_depth = (v * m * vec4(v_pos, 1.0)).z;" );
			sb.append( "  float f;" );
			sb.append( "  if (frag_depth > center_depth + radius) { f = 1.0; }" );
			sb.append( "  else if (frag_depth < center_depth - radius) { f = 0.0; }" );
			sb.append( "  else { f = (frag_depth - center_depth + radius) / (radius * 2.0); }" );
			sb.append( "  gl_FragColor = mix(vec4(" ).append( format( "%.4f" , farColor ) ).append( "), " );
			sb.append( "vec4(" ).append( format( "%.4f" , nearColor ) ).append( "), f);" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
	
	public static class DebugUVNVertexShader
	{
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "uniform mat4 m, v, p;" );
			sb.append( "uniform mat3 n;" );
			sb.append( "attribute vec3 a_pos;" );
			sb.append( "attribute vec3 a_norm;" );
			sb.append( "attribute vec3 a_u;" );
			sb.append( "attribute vec3 a_v;" );
			sb.append( "varying vec3 v_vmpos;" );
			sb.append( "varying vec3 v_norm;" );
			sb.append( "varying vec3 v_u;" );
			sb.append( "varying vec3 v_v;" );
			sb.append( "void main(void)" );
			sb.append( "{" );
			sb.append( "  v_vmpos = (v * m * vec4(a_pos, 1.0)).xyz;" );
			sb.append( "  v_norm = (v * vec4(n * a_norm, 0.0)).xyz;" );
			sb.append( "  v_u = (v * vec4(n * a_u, 0.0)).xyz;" );
			sb.append( "  v_v = (v * vec4(n * a_v, 0.0)).xyz;" );
			sb.append( "  gl_Position = p * v * m * vec4(a_pos, 1.0);" );
			sb.append( "}" );
			
			return sb.toString( );
		}
	}
	
	/**
	 * Shades according to normal, u, and v vectors on the object. Normals facing the camera are red, facing away are cyan. u facing the camera are green,
	 * facing away are magenta. v facing the camera are blue, facing away are yellow.
	 * 
	 * @author Andy
	 */
	public static class DebugUVNFragmentShader
	{
		public String toString( )
		{
			StringBuffer sb = new StringBuffer( );
			sb.append( "varying vec3 v_vmpos;" );
			sb.append( "varying vec3 v_norm;" );
			sb.append( "varying vec3 v_u;" );
			sb.append( "varying vec3 v_v;" );
			sb.append( "void main() {" );
			sb.append( "  vec3 topos = normalize(v_vmpos);" );
			sb.append( "  float norm = dot(v_norm, topos);" );
			sb.append( "  float u = dot(v_u, topos);" );
			sb.append( "  float v = dot(v_v, topos);" );
			sb.append( "  vec4 color = vec4(0.0, 0.0, 0.0, 1.0);" );
			sb.append( "  if (norm > 0.0) {" );
			sb.append( "    color += vec4(norm, 0.0, 0.0, 0.0);" );
			sb.append( "  }" );
			sb.append( "  else {" );
			sb.append( "    color += vec4(0.0, -norm, -norm, 0.0);" );
			sb.append( "  }" );
			sb.append( "  if (u > 0.0) {" );
			sb.append( "    color += vec4(0.0, u, 0.0, 0.0);" );
			sb.append( "  }" );
			sb.append( "  else {" );
			sb.append( "    color += vec4(-u, 0.0, -u, 0.0);" );
			sb.append( "  }" );
			sb.append( "  if (v > 0.0) {" );
			sb.append( "    color += vec4(0.0, 0.0, v, 0.0);" );
			sb.append( "  }" );
			sb.append( "  else {" );
			sb.append( "    color += vec4(-v, -v, 0.0, 0.0);" );
			sb.append( "  }" );
			sb.append( "  gl_FragColor = color;" );
			sb.append( "}" );
			return sb.toString( );
		}
	}
	
	private static String format( String valueFormat , float ... values )
	{
		StringBuffer sb = new StringBuffer( );
		if( values.length > 0 )
		{
			sb.append( String.format( valueFormat , values[ 0 ] ) );
		}
		for( int i = 1 ; i < values.length ; i++ )
		{
			sb.append( ", " );
			sb.append( String.format( valueFormat , values[ i ] ) );
		}
		return sb.toString( );
	}
}