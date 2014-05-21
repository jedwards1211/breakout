package org.andork.jogl.awt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

import org.andork.jogl.neu.JoglResourceManager;

public class GlyphCache
{
	JoglResourceManager						manager;
	Font									font;
	public final FontMetrics				fontMetrics;
	
	BufferedImageFactory					imageFactory;
	GlyphPagePainter						painter;
	
	int										cellWidth;
	int										cellHeight;
	int										cellBaseline;
	int										rowsPerPage;
	int										colsPerPage;
	
	int										pageWidth;
	int										pageHeight;
	int										charsPerPage;
	
	final WeakHashMap<Integer, GlyphPage>	pageCache	= new WeakHashMap<Integer, GlyphPage>( );
	
	public GlyphCache( JoglResourceManager manager , Font f , int pageWidth , int pageHeight , BufferedImageFactory imageFactory , GlyphPagePainter painter )
	{
		this.manager = manager;
		this.font = f;
		this.imageFactory = imageFactory;
		this.painter = painter;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		
		BufferedImage image = imageFactory.newImage( 1 , 1 );
		Graphics2D g2 = ( Graphics2D ) image.createGraphics( );
		
		fontMetrics = g2.getFontMetrics( f );
		cellWidth = fontMetrics.getMaxAdvance( );
		cellHeight = fontMetrics.getMaxDescent( ) + fontMetrics.getMaxAscent( );
		cellBaseline = fontMetrics.getMaxAscent( );
		
		g2.dispose( );
		
		colsPerPage = pageWidth / cellWidth;
		rowsPerPage = pageHeight / cellHeight;
		charsPerPage = colsPerPage * rowsPerPage;
	}
	
	public int pageIndex( char c )
	{
		return c / charsPerPage;
	}
	
	public GlyphPage getPage( int pageIndex )
	{
		GlyphPage page = pageCache.get( pageIndex );
		if( page == null )
		{
			page = createPage( pageIndex );
			pageCache.put( pageIndex , page );
		}
		return page;
	}
	
	public GlyphPage getPage( char c )
	{
		return getPage( pageIndex( c ) );
	}
	
	public GlyphPage createPage( int pageIndex )
	{
		int startChar = charsPerPage * pageIndex;
		if( startChar > Character.MAX_VALUE )
		{
			throw new IllegalArgumentException( "pageIndex out of range: " + pageIndex );
		}
		
		BufferedImage image = imageFactory.newImage( pageWidth , pageHeight );
		return new GlyphPage( manager , fontMetrics , image , painter , ( char ) startChar );
	}
}
