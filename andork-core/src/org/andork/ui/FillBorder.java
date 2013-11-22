package org.andork.ui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.Border;

public abstract class FillBorder implements Border
{
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
