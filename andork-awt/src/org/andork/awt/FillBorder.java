package org.andork.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.border.Border;

public abstract class FillBorder implements Border
{
	protected Shape getFillShape( Component c , Graphics g , int x , int y , int width , int height )
	{
		return new Rectangle( x , y , width , height );
	}
	
	protected abstract Paint getPaint( Component c , Graphics g , int x , int y , int width , int height );
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		Graphics2D g2 = ( Graphics2D ) g;
		Paint prevPaint = g2.getPaint( );
		
		g2.setPaint( getPaint( c , g , x , y , width , height ) );
		g2.fill( getFillShape( c , g , x , y , width , height ) );
		
		g2.setPaint( prevPaint );
	}
	
	@Override
	public Insets getBorderInsets( Component c )
	{
		return new Insets( 0 , 0 , 0 , 0 );
	}
	
	@Override
	public boolean isBorderOpaque( )
	{
		return false;
	}
}
