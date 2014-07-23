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
package org.andork.jogl;

import static javax.media.opengl.GL.GL_BGRA;
import static javax.media.opengl.GL.GL_BGRA8;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

public class JOGLScreenCapturer implements JOGLRunnable
{
	public JOGLScreenCapturer( BasicJOGLScene scene )
	{
		super( );
		this.scene = scene;
	}
	
	BasicJOGLScene	scene;
	
	int				captureWidth;
	int				captureHeight;
	ByteBuffer		capture;
	
	public static int pow2ceil( int a )
	{
		int p = 2;
		while( p < a )
		{
			p <<= 1;
		}
		return p;
	}
	
	@Override
	public void run( GL2ES2 gl )
	{
		gl = new DebugGL2( ( GL2 ) gl );
		int[ ] i = new int[ 3 ];
		gl.glGenFramebuffers( 1 , i , 0 );
		gl.glGenRenderbuffers( 2 , i , 1 );
		int fbo = i[ 0 ];
		int colorBuf = i[ 1 ];
		int depthBuf = i[ 2 ];
		
		int width = scene.getWidth( );
		int height = scene.getHeight( );
		
		try
		{
			gl.glBindFramebuffer( GL_FRAMEBUFFER , fbo );
			gl.glBindRenderbuffer( GL_RENDERBUFFER , colorBuf );
			gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_RGB , width , height );
			gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , colorBuf );
			gl.glBindRenderbuffer( GL_RENDERBUFFER , depthBuf );
			gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_DEPTH_COMPONENT32 , width , height );
			gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , depthBuf );
			
			gl.glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT );
			
			gl.glViewport( -width * 3 / 2 , -height * 3 / 2 , width * 3 , height * 3 );
			
			scene.drawObjects( gl );
			
			captureWidth = scene.getWidth( );
			captureHeight = scene.getHeight( );
			// gl.glReadBuffer( GL_COLOR_ATTACHMENT0 );
			ByteBuffer buffer = ByteBuffer.allocateDirect( captureWidth * captureHeight * 4 );
			buffer.order( ByteOrder.nativeOrder( ) );
			capture = buffer;
			
			// gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , colorBuf );
			gl.glReadPixels( 0 , 0 , captureWidth , captureHeight , GL_BGRA , GL_UNSIGNED_BYTE , buffer );
			buffer.position( 0 );
		}
		finally
		{
			gl.glBindFramebuffer( GL_FRAMEBUFFER , 0 );
			
			gl.glDeleteFramebuffers( 1 , i , 0 );
			gl.glDeleteRenderbuffers( 1 , i , 1 );
			gl.glDeleteRenderbuffers( 1 , i , 2 );
			
			gl.glViewport( 0 , 0 , width , height );
		}
	}
	
	public BufferedImage getCaptureAsBufferedImage( )
	{
		capture.position( 0 );
		
		BufferedImage result = new BufferedImage( captureWidth , captureHeight , BufferedImage.TYPE_INT_ARGB );
		
		for( int y = captureHeight - 1 ; y >= 0 ; y-- )
		{
			for( int x = 0 ; x < captureWidth ; x++ )
			{
				result.setRGB( x , y , capture.getInt( ) );
			}
		}
		
		return result;
	}
}
