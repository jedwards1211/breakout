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
package org.andork.jogl.old;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2ES2;

public class BasicJOGLTexture implements JOGLResource
{
	boolean		initialized;
	
	int			texture;
	
	int			target;
	
	int			minFilter	= GL2ES2.GL_LINEAR;
	int			magFilter	= GL2ES2.GL_LINEAR;
	
	Image[ ]	images		= new Image[ 1 ];
	
	public int texture( )
	{
		return texture;
	}
	
	public int minFilter( )
	{
		return minFilter;
	}
	
	public BasicJOGLTexture minFilter( int minFilter )
	{
		this.minFilter = minFilter;
		return this;
	}
	
	public int magFilter( )
	{
		return magFilter;
	}
	
	public BasicJOGLTexture magFilter( int magFilter )
	{
		this.magFilter = magFilter;
		return this;
	}
	
	public int target( )
	{
		return target;
	}
	
	public BasicJOGLTexture target( int target )
	{
		this.target = target;
		return this;
	}
	
	public Image image( int level )
	{
		if( images.length <= level )
		{
			images = Arrays.copyOf( images , level + 1 );
		}
		if( images[ level ] == null )
		{
			images[ level ] = new Image( );
		}
		return images[ level ];
	}
	
	public BasicJOGLTexture image( int level , Image image )
	{
		if( images.length <= level )
		{
			images = Arrays.copyOf( images , level + 1 );
		}
		images[ level ] = image;
		return this;
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		if( !initialized )
		{
			int[ ] textures = new int[ 1 ];
			gl.glGenTextures( 1 , textures , 0 );
			texture = textures[ 0 ];
			
			gl.glBindTexture( target , texture );
			
			gl.glTexParameteri( target , GL2ES2.GL_TEXTURE_MIN_FILTER , minFilter );
			gl.glTexParameteri( target , GL2ES2.GL_TEXTURE_MAG_FILTER , magFilter );
			
			initialized = true;
			
			rebuffer( gl );
		}
	}
	
	public void rebuffer( GL2ES2 gl )
	{
		if( !initialized )
		{
			init( gl );
		}
		if( images != null )
		{
			int level = 0;
			for( Image image : images )
			{
				if( image != null )
				{
					image.buffer.position( 0 );
					gl.glTexImage2D( target , level , image.internalFormat , image.width , image.height ,
							image.border , image.format , image.type , image.buffer );
				}
				level++ ;
			}
		}
	}
	
	@Override
	public void destroy( GL2ES2 gl )
	{
		if( initialized )
		{
			int[ ] textures = new int[ ] { texture };
			gl.glDeleteTextures( 1 , textures , 0 );
			initialized = false;
		}
	}
	
	public static class Image
	{
		int			internalFormat;
		int			width;
		int			height;
		int			border;
		int			format;
		int			type;
		
		ByteBuffer	buffer;
		
		public int internalFormat( )
		{
			return internalFormat;
		}
		
		public Image internalFormat( int internalFormat )
		{
			this.internalFormat = internalFormat;
			return this;
		}
		
		public int width( )
		{
			return width;
		}
		
		public Image width( int width )
		{
			this.width = width;
			return this;
		}
		
		public int height( )
		{
			return height;
		}
		
		public Image height( int height )
		{
			this.height = height;
			return this;
		}
		
		public int border( )
		{
			return border;
		}
		
		public Image border( int border )
		{
			this.border = border;
			return this;
		}
		
		public int format( )
		{
			return format;
		}
		
		public Image format( int format )
		{
			this.format = format;
			return this;
		}
		
		public int type( )
		{
			return type;
		}
		
		public Image type( int type )
		{
			this.type = type;
			return this;
		}
		
		public ByteBuffer buffer( )
		{
			return buffer;
		}
		
		public Image buffer( ByteBuffer buffer )
		{
			this.buffer = buffer;
			return this;
		}
		
	}
}
