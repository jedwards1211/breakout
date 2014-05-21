package org.andork.jogl.awt;

import static javax.media.opengl.GL.GL_BGRA;
import static javax.media.opengl.GL.GL_CLAMP_TO_EDGE;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_LINEAR_MIPMAP_LINEAR;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL2ES2.GL_RED;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.neu.JoglManagedResource;
import org.andork.jogl.neu.JoglResourceManager;

import com.jogamp.nativewindow.awt.DirectDataBufferInt;

public class GlyphPage extends JoglManagedResource
{
	public final FontMetrics	metrics;
	
	BufferedImage				image;
	
	char						startChar;
	
	public final int			cellHeight;
	public final int			cellWidth;
	
	float						texcoordCellHeight;
	float						texcoordCellWidth;
	float						texcoordMaxDescent;
	float						texcoordMaxAscent;
	float						texcoordWidthFactor;
	
	int							rows;
	int							cols;
	
	int							texture;
	
	public GlyphPage( JoglResourceManager manager , FontMetrics fm , BufferedImage image , GlyphPagePainter painter , char startChar )
	{
		super( manager );
		this.metrics = fm;
		this.image = image;
		this.startChar = startChar;
		
		Graphics2D g2 = ( Graphics2D ) image.createGraphics( );
		
		g2.setFont( fm.getFont( ) );
		
		cellHeight = fm.getMaxAscent( ) + fm.getMaxDescent( ) + 1;
		int cellBaseline = cellHeight - fm.getMaxDescent( );
		cellWidth = fm.getMaxAdvance( ) + 1;
		
		texcoordCellHeight = ( float ) cellHeight / image.getHeight( );
		texcoordWidthFactor = 1f / image.getWidth( );
		texcoordCellWidth = ( float ) cellWidth * texcoordWidthFactor;
		texcoordMaxAscent = ( float ) fm.getMaxAscent( ) / image.getHeight( );
		texcoordMaxDescent = ( float ) fm.getMaxDescent( ) / image.getHeight( );
		
		cols = image.getWidth( ) / cellWidth;
		rows = image.getHeight( ) / cellHeight;
		
		g2.setBackground( new Color( 0 , 0 , 0 , 0 ) );
		g2.clearRect( 0 , 0 , image.getWidth( ) , image.getHeight( ) );
		g2.setColor( Color.white );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		painter.drawGlyphs( g2 , ( char ) startChar , rows , cols , cellHeight , cellWidth , cellBaseline );
		
		g2.dispose( );
	}
	
	public void getTexcoordBounds( char c , float[ ] lrtb )
	{
		int offs = c - startChar;
		int row = offs / cols;
		int col = offs % cols;
		
		lrtb[ 0 ] = col * texcoordCellWidth;
		lrtb[ 1 ] = lrtb[ 0 ] + metrics.charWidth( c ) * texcoordWidthFactor;
		lrtb[ 3 ] = ( row + 1 ) * texcoordCellHeight;
		lrtb[ 2 ] = lrtb[ 3 ] - texcoordMaxDescent - texcoordMaxAscent;
	}
	
	public static void drawGlyphs( Graphics2D g , char startChar , int rows , int cols , int cellHeight , int cellWidth , int cellBaseline )
	{
		char[ ] chars = { startChar };
		int y = cellBaseline;
		for( int row = 0 ; row < rows ; row++ , y += cellHeight )
		{
			int x = 0;
			for( int col = 0 ; col < cols ; col++ , x += cellWidth , chars[ 0 ]++ )
			{
				g.drawChars( chars , 0 , 1 , x , y );
			}
		}
	}
	
	public int getTexture( )
	{
		return texture;
	}
	
	public BufferedImage getImage( )
	{
		return image;
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		if( texture > 0 )
		{
			return;
		}
		
		int[ ] temp = new int[ 1 ];
		gl.glGenTextures( 1 , temp , 0 );
		
		texture = temp[ 0 ];
		
		gl.glActiveTexture( GL_TEXTURE0 );
		gl.glBindTexture( GL_TEXTURE_2D , texture );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_WRAP_S , GL_CLAMP_TO_EDGE );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_WRAP_T , GL_CLAMP_TO_EDGE );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER , GL_LINEAR );
		gl.glTexParameteri( GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER , GL_LINEAR_MIPMAP_LINEAR );
		
		if( image.getRaster( ).getDataBuffer( ) instanceof DirectDataBufferByte )
		{
			ByteBuffer data = ( ( DirectDataBufferByte ) image.getRaster( ).getDataBuffer( ) ).getData( );
			data.position( 0 );
			gl.glTexImage2D( GL_TEXTURE_2D , 0 , GL_RED , image.getWidth( ) , image.getHeight( ) , 0 , GL_RED , GL_UNSIGNED_BYTE , data );
		}
		else if( image.getRaster( ).getDataBuffer( ) instanceof DirectDataBufferInt )
		{
			IntBuffer data = ( ( DirectDataBufferInt ) image.getRaster( ).getDataBuffer( ) ).getData( );
			data.position( 0 );
			gl.glTexImage2D( GL_TEXTURE_2D , 0 , GL_RGBA , image.getWidth( ) , image.getHeight( ) , 0 , GL_BGRA , GL_UNSIGNED_BYTE , data );
		}
		
		gl.glGenerateMipmap( GL_TEXTURE_2D );
		
		gl.glBindTexture( GL_TEXTURE_2D , 0 );
	}
	
	@Override
	public void dispose( GL2ES2 gl )
	{
		if( texture <= 0 )
		{
			return;
		}
		
		int[ ] temp = { texture };
		gl.glDeleteTextures( 1 , temp , 0 );
		
		texture = -1;
	}
}
