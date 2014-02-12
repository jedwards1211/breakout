package org.andork.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class LayeredBorder implements Border
{
	protected Border	top , bottom;
	
	protected LayeredBorder( Border top , Border bottom )
	{
		super( );
		this.top = top;
		this.bottom = bottom;
	}
	
	protected Border remove( Border oldb )
	{
		if( oldb == top )
			return bottom;
		if( oldb == bottom )
			return top;
		Border top2 = remove( top , oldb );
		Border bottom2 = remove( bottom , oldb );
		if( top2 == top && bottom2 == bottom )
		{
			return this;	// it's not here
		}
		return add( top2 , bottom2 );
	}
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		bottom.paintBorder( c , g , x , y , width , height );
		top.paintBorder( c , g , x , y , width , height );
	}
	
	@Override
	public Insets getBorderInsets( Component c )
	{
		Insets ti = top.getBorderInsets( c );
		Insets bi = bottom.getBorderInsets( c );
		
		return new Insets(
				Math.max( ti.top , bi.top ) ,
				Math.max( ti.left , bi.left ) ,
				Math.max( ti.bottom , bi.bottom ) ,
				Math.max( ti.right , bi.right ) );
	}
	
	@Override
	public boolean isBorderOpaque( )
	{
		return false;
	}
	
	public static Border add( Border top , Border bottom )
	{
		if( top == null )
		{
			return bottom;
		}
		if( bottom == null )
		{
			return top;
		}
		return new LayeredBorder( top , bottom );
	}
	
	public static void addOnTop( Border b , JComponent c )
	{
		c.setBorder( add( b , c.getBorder( ) ) );
	}
	
	public static void addOnBottom( Border b , JComponent c )
	{
		c.setBorder( add( c.getBorder( ) , b ) );
	}
	
	public static void remove( Border b , JComponent c )
	{
		c.setBorder( remove( c.getBorder( ) , b ) );
	}
	
	public static Border remove( Border b , Border oldb )
	{
		if( b == oldb || b == null )
		{
			return null;
		}
		else if( b instanceof LayeredBorder )
		{
			return ( ( LayeredBorder ) b ).remove( oldb );
		}
		else
		{
			return b;		// it's not here
		}
	}
}
