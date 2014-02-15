package org.andork.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class OverrideInsetsBorder implements Border
{
	Border	wrapped;
	Insets	insets;
	
	public OverrideInsetsBorder( Border wrapped , Insets insets )
	{
		this.wrapped = wrapped;
		this.insets = insets;
	}
	
	@Override
	public void paintBorder( Component c , Graphics g , int x , int y , int width , int height )
	{
		wrapped.paintBorder( c , g , x , y , width , height );
	}
	
	@Override
	public Insets getBorderInsets( Component c )
	{
		return ( Insets ) insets.clone( );
	}
	
	@Override
	public boolean isBorderOpaque( )
	{
		return wrapped.isBorderOpaque( );
	}
	
	public static void override( JComponent comp , Insets insets )
	{
		Border b = comp.getBorder( );
		while( b instanceof OverrideInsetsBorder )
		{
			b = ( ( OverrideInsetsBorder ) b ).wrapped;
		}
		comp.setBorder( new OverrideInsetsBorder( b , insets ) );
	}
}
