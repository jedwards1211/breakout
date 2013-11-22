package org.andork.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class LayeredBorder implements Border
{
	public final List<Border>	borders	= new ArrayList<Border>( );
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		for( Border border : borders )
		{
			border.paintBorder( c , g , x , y , width , height );
		}
	}
	
	@Override
	public Insets getBorderInsets( Component c )
	{
		Insets i = new Insets( 0 , 0 , 0 , 0 );
		for( Border border : borders )
		{
			Insets bi = border.getBorderInsets( c );
			i.left = Math.max( i.left , bi.left );
			i.right = Math.max( i.right , bi.right );
			i.top = Math.max( i.top , bi.top );
			i.bottom = Math.max( i.bottom , bi.bottom );
		}
		return i;
	}
	
	@Override
	public boolean isBorderOpaque( )
	{
		return false;
	}
	
	public static void addBorder( Border b , JComponent c )
	{
		Border current = c.getBorder( );
		if( current == null )
		{
			c.setBorder( b );
		}
		else if( current instanceof LayeredBorder )
		{
			LayeredBorder lb = ( LayeredBorder ) current;
			lb.borders.add( b );
		}
		else
		{
			LayeredBorder lb = new LayeredBorder( );
			lb.borders.add( current );
			lb.borders.add( b );
			c.setBorder( lb );
		}
	}
	
	public static void removeBorder( Border b , JComponent c )
	{
		Border current = c.getBorder( );
		if( current == b )
		{
			c.setBorder( null );
		}
		else if( current instanceof LayeredBorder )
		{
			LayeredBorder lb = ( LayeredBorder ) current;
			lb.borders.remove( b );
			if( lb.borders.isEmpty( ) )
			{
				c.setBorder( null );
			}
		}
	}
}
