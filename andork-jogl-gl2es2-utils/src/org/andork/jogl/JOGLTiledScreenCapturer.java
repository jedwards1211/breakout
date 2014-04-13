package org.andork.jogl;

import static javax.media.opengl.GL.GL_BGRA;
import static javax.media.opengl.GL.GL_COLOR_ATTACHMENT0;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_ATTACHMENT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_COMPONENT32;
import static javax.media.opengl.GL.GL_FRAMEBUFFER;
import static javax.media.opengl.GL.GL_RENDERBUFFER;
import static javax.media.opengl.GL.GL_RGB;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

public class JOGLTiledScreenCapturer implements JOGLRunnable
{
	public JOGLTiledScreenCapturer( BasicJOGLScene scene , int[ ] tileWidths , int[ ] tileHeights , Fit fit )
	{
		super( );
		this.scene = scene;
		this.fit = fit;
		this.tileWidths = tileWidths;
		this.tileHeights = tileHeights;
		capturedImage = new BufferedImage( total( tileWidths ) , total( tileHeights ) , BufferedImage.TYPE_INT_ARGB );
	}
	
	public static enum Fit
	{
		VERTICAL , HORIZONTAL , BOTH;
	}
	
	BasicJOGLScene	scene;
	
	Fit				fit;
	
	int[ ]			tileWidths;
	int[ ]			tileHeights;
	
	BufferedImage	capturedImage;
	
	public static int pow2ceil( int a )
	{
		int p = 2;
		while( p < a )
		{
			p <<= 1;
		}
		return p;
	}
	
	private static int max( int ... values )
	{
		int max = Integer.MIN_VALUE;
		for( int i : values )
		{
			max = Math.max( max , i );
		}
		return max;
	}
	
	private static int total( int ... values )
	{
		int total = 0;
		for( int i : values )
		{
			total += i;
		}
		return total;
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
		
		int bufferWidth = max( tileWidths ), bufferHeight = max( tileHeights );
		int totalWidth = total( tileWidths ), totalHeight = total( tileHeights );
		
		int viewportWidth = 0, viewportHeight = 0;
		
		Fit effectiveFit = fit;
		if( fit == Fit.BOTH )
		{
			effectiveFit = totalWidth * scene.getWidth( ) > totalHeight * scene.getHeight( ) ? Fit.HORIZONTAL : Fit.VERTICAL;
		}
		
		switch( effectiveFit )
		{
			case HORIZONTAL:
				viewportWidth = totalWidth;
				viewportHeight = viewportWidth * scene.getHeight( ) / scene.getWidth( );
				break;
			case VERTICAL:
				viewportHeight = totalHeight;
				viewportWidth = viewportHeight * scene.getWidth( ) / scene.getHeight( );
				break;
			default:
				break;
		}
		
		try
		{
			gl.glBindFramebuffer( GL_FRAMEBUFFER , fbo );
			gl.glBindRenderbuffer( GL_RENDERBUFFER , colorBuf );
			gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_RGB , bufferWidth , bufferHeight );
			gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , colorBuf );
			gl.glBindRenderbuffer( GL_RENDERBUFFER , depthBuf );
			gl.glRenderbufferStorage( GL_RENDERBUFFER , GL_DEPTH_COMPONENT32 , bufferWidth , bufferHeight );
			gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_DEPTH_ATTACHMENT , GL_RENDERBUFFER , depthBuf );
			
			ByteBuffer buffer = ByteBuffer.allocateDirect( bufferWidth * bufferHeight * 4 );
			buffer.order( ByteOrder.nativeOrder( ) );
			
			int viewportX = 0;
			for( int tileX = 0 ; tileX < tileWidths.length ; tileX++ )
			{
				int viewportY = 0;
				for( int tileY = 0 ; tileY < tileHeights.length ; tileY++ )
				{
					gl.glViewport( viewportX , viewportY , viewportWidth , viewportHeight );
					
					gl.glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT );
					
					scene.drawObjects( gl );
					
					buffer.position( 0 );
					// gl.glFramebufferRenderbuffer( GL_FRAMEBUFFER , GL_COLOR_ATTACHMENT0 , GL_RENDERBUFFER , colorBuf );
					gl.glReadPixels( 0 , 0 , tileWidths[ tileX ] , tileHeights[ tileY ] , GL_BGRA , GL_UNSIGNED_BYTE , buffer );
					buffer.position( 0 );
					
					for( int y = 0 ; y < tileHeights[ tileY ] ; y++ )
					{
						for( int x = 0 ; x < tileWidths[ tileX ] ; x++ )
						{
							int rgb = buffer.getInt( );
							capturedImage.setRGB( x - viewportX , totalHeight - y + viewportY - 1 , rgb );
						}
					}
					
					viewportY -= tileHeights[ tileY ];
				}
				viewportX -= tileWidths[ tileX ];
			}
		}
		finally
		{
			gl.glBindFramebuffer( GL_FRAMEBUFFER , 0 );
			
			gl.glDeleteFramebuffers( 1 , i , 0 );
			gl.glDeleteRenderbuffers( 1 , i , 1 );
			gl.glDeleteRenderbuffers( 1 , i , 2 );
			
			gl.glViewport( 0 , 0 , scene.getWidth( ) , scene.getHeight( ) );
		}
	}
	
	public BufferedImage getCapturedImage( )
	{
		return capturedImage;
	}
}
