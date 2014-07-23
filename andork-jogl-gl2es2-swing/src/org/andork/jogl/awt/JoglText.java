/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.jogl.awt;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_STATIC_DRAW;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL2ES2.*;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.BufferHelper;
import org.andork.jogl.Dumps;
import org.andork.jogl.neu.JoglDrawContext;
import org.andork.jogl.neu.JoglDrawable;
import org.andork.jogl.neu.JoglManagedResource;
import org.andork.jogl.neu.JoglResourceManager;
import org.andork.jogl.util.JOGLUtils;

public class JoglText extends JoglManagedResource implements JoglDrawable
{
	private final List<Segment>	segments	= new LinkedList<Segment>( );
	
	private int					program;
	
	private final float[ ]		origin		= new float[ 3 ];
	
	private static int			BILLBOARD_PROGRAM;
	
	private JoglText( JoglResourceManager manager )
	{
		super( manager );
	}
	
	public static class Builder
	{
		private final float[ ]					lrtb		= new float[ 4 ];
		
		private Map<SegmentKey, BufferHelper>	buffers		= new HashMap<SegmentKey, BufferHelper>( );
		private float[ ]						dot			= new float[ 3 ];
		private float[ ]						nextDot		= new float[ 3 ];
		private final float[ ]					baseline	= new float[ 3 ];
		private final float[ ]					ascent		= new float[ 3 ];
		
		public Builder( )
		{
			baseline[ 0 ] = 1;
			ascent[ 1 ] = 1;
		}
		
		public Builder baseline( float ... baseline )
		{
			System.arraycopy( baseline , 0 , this.baseline , 0 , 3 );
			return this;
		}
		
		public Builder ascent( float ... ascent )
		{
			System.arraycopy( ascent , 0 , this.ascent , 0 , 3 );
			return this;
		}
		
		public Builder dot( float ... dot )
		{
			System.arraycopy( dot , 0 , this.dot , 0 , 3 );
			return this;
		}
		
		public Builder add( String text , GlyphCache cache , float ... color )
		{
			SegmentKey key = null;
			BufferHelper buffer = null;
			for( int i = 0 ; i < text.length( ) ; i++ )
			{
				char c = text.charAt( i );
				GlyphPage page = cache.getPage( c );
				if( key == null || key.page != page )
				{
					key = new SegmentKey( page , color );
					buffer = buffers.get( key );
					if( buffer == null )
					{
						buffer = new BufferHelper( );
						buffers.put( key , buffer );
					}
				}
				
				page.getTexcoordBounds( c , lrtb );
				
				float scale = 1f / page.metrics.getAscent( );
				
				float width = page.metrics.charWidth( c ) * scale;
				nextDot[ 0 ] = dot[ 0 ] + baseline[ 0 ] * width;
				nextDot[ 1 ] = dot[ 1 ] + baseline[ 1 ] * width;
				nextDot[ 2 ] = dot[ 2 ] + baseline[ 2 ] * width;
				
				float ascentScale = page.metrics.getMaxAscent( ) * scale;
				float descentScale = -page.metrics.getMaxDescent( ) * scale;
				
				buffer.putAsFloats( dot[ 0 ] + ascent[ 0 ] * descentScale );
				buffer.putAsFloats( dot[ 1 ] + ascent[ 1 ] * descentScale );
				buffer.putAsFloats( dot[ 2 ] + ascent[ 2 ] * descentScale );
				buffer.putAsFloats( lrtb[ 0 ] , lrtb[ 3 ] );
				
				buffer.putAsFloats( dot[ 0 ] + ascent[ 0 ] * ascentScale );
				buffer.putAsFloats( dot[ 1 ] + ascent[ 1 ] * ascentScale );
				buffer.putAsFloats( dot[ 2 ] + ascent[ 2 ] * ascentScale );
				buffer.putAsFloats( lrtb[ 0 ] , lrtb[ 2 ] );
				
				buffer.putAsFloats( nextDot[ 0 ] + ascent[ 0 ] * descentScale );
				buffer.putAsFloats( nextDot[ 1 ] + ascent[ 1 ] * descentScale );
				buffer.putAsFloats( nextDot[ 2 ] + ascent[ 2 ] * descentScale );
				buffer.putAsFloats( lrtb[ 1 ] , lrtb[ 3 ] );
				
				buffer.putAsFloats( dot[ 0 ] + ascent[ 0 ] * ascentScale );
				buffer.putAsFloats( dot[ 1 ] + ascent[ 1 ] * ascentScale );
				buffer.putAsFloats( dot[ 2 ] + ascent[ 2 ] * ascentScale );
				buffer.putAsFloats( lrtb[ 0 ] , lrtb[ 2 ] );
				
				buffer.putAsFloats( nextDot[ 0 ] + ascent[ 0 ] * descentScale );
				buffer.putAsFloats( nextDot[ 1 ] + ascent[ 1 ] * descentScale );
				buffer.putAsFloats( nextDot[ 2 ] + ascent[ 2 ] * descentScale );
				buffer.putAsFloats( lrtb[ 1 ] , lrtb[ 3 ] );
				
				buffer.putAsFloats( nextDot[ 0 ] + ascent[ 0 ] * ascentScale );
				buffer.putAsFloats( nextDot[ 1 ] + ascent[ 1 ] * ascentScale );
				buffer.putAsFloats( nextDot[ 2 ] + ascent[ 2 ] * ascentScale );
				buffer.putAsFloats( lrtb[ 1 ] , lrtb[ 2 ] );
				
				float[ ] temp = dot;
				dot = nextDot;
				nextDot = temp;
			}
			
			return this;
		}
		
		public JoglText create( JoglResourceManager manager )
		{
			JoglText text = new JoglText( manager );
			for( Map.Entry<SegmentKey, BufferHelper> entry : buffers.entrySet( ) )
			{
				FloatBuffer data = entry.getValue( ).toByteBuffer( ).asFloatBuffer( );
				Dumps.dumpBuffer( data , "%9.2f, " , 5 );
				text.segments.add( new Segment( data , entry.getKey( ).page , data.capacity( ) / 5 , entry.getKey( ).color ) );
			}
			
			return text;
		}
	};
	
	private static class SegmentKey
	{
		private final GlyphPage	page;
		private final float[ ]	color;
		
		public SegmentKey( GlyphPage page , float[ ] color )
		{
			this.page = page;
			this.color = Arrays.copyOf( color , 4 );
		}
		
		public boolean equals( Object o )
		{
			if( o instanceof SegmentKey )
			{
				SegmentKey ps = ( SegmentKey ) o;
				return ps.page == page && Arrays.equals( ps.color , color );
			}
			return false;
		}
		
		public int hashCode( )
		{
			return ( 31 * page.hashCode( ) ) ^ Arrays.hashCode( color );
		}
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		if( BILLBOARD_PROGRAM <= 0 )
		{
			String vertexShaderCode =
					"uniform mat4 p;" +
							"uniform mat4 v;" +
							"uniform mat4 m;" +
							"uniform vec2 px;" +
							
							"uniform vec3 u_origin;" +
							"attribute vec3 a_pos;" +
							"attribute vec2 a_texcoord;" +
							
							"varying vec2 v_texcoord;" +
							
							"void main() {" +
							"  v_texcoord = a_texcoord;" +
							"  gl_Position = p * v * m * vec4(u_origin, 1.0);" +
							"  gl_Position.xy += vec2(a_pos.xy * px * gl_Position.w);" +
							"}";
			
			String fragmentShaderCode =
					"uniform sampler2D u_texture;" +
							"uniform vec4 u_color;" +
							"varying vec2 v_texcoord;" +
							
							"void main() {" +
							// "  gl_FragColor = vec4(u_color.xyz , u_color.w * texture2D(u_texture, v_texcoord).r);" +
							"  gl_FragColor = vec4(u_color * texture2D(u_texture, v_texcoord));" +
							"  if (gl_FragColor.a == 0.0) {" +
							"    discard;" +
							"  }" +
							"}";
			
			BILLBOARD_PROGRAM = JOGLUtils.loadProgram( gl , vertexShaderCode , fragmentShaderCode );
		}
		
		program = BILLBOARD_PROGRAM;
		
		int k = 0;
		for( Segment segment : segments )
		{
			segment.page.use( );
			if( segment.buffer <= 0 )
			{
				k++ ;
			}
		}
		
		if( k > 0 )
		{
			int[ ] temp = new int[ k ];
			gl.glGenBuffers( k , temp , 0 );
			
			k = 0;
			for( Segment segment : segments )
			{
				if( segment.buffer <= 0 )
				{
					segment.buffer = temp[ k++ ];
					segment.data.position( 0 );
					gl.glBindBuffer( GL_ARRAY_BUFFER , segment.buffer );
					gl.glBufferData( GL_ARRAY_BUFFER , segment.data.capacity( ) * 4 , segment.data , GL_STATIC_DRAW );
				}
			}
		}
		
		gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
	}
	
	@Override
	public void dispose( GL2ES2 gl )
	{
		int[ ] temp = new int[ segments.size( ) ];
		int k = 0;
		for( Segment segment : segments )
		{
			segment.page.release( );
			if( segment.buffer > 0 )
			{
				temp[ k++ ] = segment.buffer;
				segment.buffer = 0;
			}
		}
		
		if( k > 0 )
		{
			gl.glDeleteBuffers( k , temp , 0 );
		}
	}
	
	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		gl.glUseProgram( program );
		
		gl.glEnable( GL_DEPTH_TEST );
		
		gl.glEnable( GL_BLEND );
		gl.glBlendFunc( GL_SRC_ALPHA , GL_ONE_MINUS_SRC_ALPHA );
		
		int loc;
		
		loc = gl.glGetUniformLocation( program , "m" );
		gl.glUniformMatrix4fv( loc , 1 , false , m , 0 );
		
		loc = gl.glGetUniformLocation( program , "v" );
		gl.glUniformMatrix4fv( loc , 1 , false , context.viewXform( ) , 0 );
		
		loc = gl.glGetUniformLocation( program , "p" );
		gl.glUniformMatrix4fv( loc , 1 , false , context.projXform( ) , 0 );
		
		loc = gl.glGetUniformLocation( program , "px" );
		gl.glUniform2fv( loc , 1 , context.pixelScale( ) , 0 );
		
		loc = gl.glGetUniformLocation( program , "u_origin" );
		gl.glUniform3fv( loc , 1 , origin , 0 );
		
		int colorLoc = gl.glGetUniformLocation( program , "u_color" );
		int posLoc = gl.glGetAttribLocation( program , "a_pos" );
		int texcoordLoc = gl.glGetAttribLocation( program , "a_texcoord" );
		int textureLoc = gl.glGetUniformLocation( program , "u_texture" );
		gl.glUniform1i( textureLoc , 0 );
		
		gl.glEnableVertexAttribArray( posLoc );
		gl.glEnableVertexAttribArray( texcoordLoc );
		
		gl.glActiveTexture( GL_TEXTURE0 );
		
		for( Segment segment : segments )
		{
			gl.glBindBuffer( GL_ARRAY_BUFFER , segment.buffer );
			gl.glBindTexture( GL_TEXTURE_2D , segment.page.getTexture( ) );
			
			gl.glUniform4fv( colorLoc , 1 , segment.color , 0 );
			
			gl.glVertexAttribPointer( posLoc , 3 , GL_FLOAT , false , 20 , 0 );
			gl.glVertexAttribPointer( texcoordLoc , 2 , GL_FLOAT , false , 20 , 12 );
			
			gl.glDrawArrays( GL_TRIANGLES , 0 , segment.count );
		}
		
		gl.glBindBuffer( GL_ARRAY_BUFFER , 0 );
		gl.glBindTexture( GL_TEXTURE_2D , 0 );
		
		gl.glDisable( GL_BLEND );
		gl.glDisable( GL_DEPTH_TEST );
		
		gl.glUseProgram( 0 );
	}
	
	private static class Segment
	{
		private FloatBuffer	data;
		private int			buffer;
		private GlyphPage	page;
		private int			count;
		private float[ ]	color;
		
		public Segment( FloatBuffer data , GlyphPage page , int count , float[ ] color )
		{
			this.data = data;
			this.page = page;
			this.count = count;
			this.color = color;
		}
	}
}
