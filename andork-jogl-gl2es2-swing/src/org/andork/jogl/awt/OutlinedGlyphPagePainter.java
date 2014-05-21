package org.andork.jogl.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class OutlinedGlyphPagePainter implements GlyphPagePainter
{
	Stroke	outlineStroke;
	Color	outlineColor;
	Color	fillColor;
	
	public OutlinedGlyphPagePainter( Stroke outlineStroke , Color outlineColor , Color fillColor )
	{
		super( );
		this.outlineStroke = outlineStroke;
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;
	}
	
	public void drawGlyphs( Graphics2D g , char startChar , int rows , int cols , int cellHeight , int cellWidth , int cellBaseline )
	{
		AffineTransform prevXform = g.getTransform( );
		Stroke prevStroke = g.getStroke( );
		Paint prevPaint = g.getPaint( );
		
		char[ ] chars = { startChar };
		g.translate( 0 , cellBaseline );
		for( int row = 0 ; row < rows ; row++ , g.translate( -cellWidth * cols , cellHeight ) )
		{
			for( int col = 0 ; col < cols ; col++ , chars[ 0 ]++ , g.translate( cellWidth , 0 ) )
			{
				g.setColor( outlineColor );
				g.setStroke( outlineStroke );
				Shape outline = g.getFont( ).createGlyphVector( g.getFontRenderContext( ) , chars ).getOutline( );
				g.draw( outline );
				g.setColor( fillColor );
				g.fill( outline );
			}
		}
		g.setTransform( prevXform );
		g.setStroke( prevStroke );
		g.setPaint( prevPaint );
	}
}
