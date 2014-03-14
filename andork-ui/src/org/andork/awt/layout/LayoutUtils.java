package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;

public class LayoutUtils
{
	public static Rectangle calculateInnerArea( Component comp , LayoutSize size )
	{
		Rectangle bounds = new Rectangle( size.get( comp ) );
		if( comp instanceof Container )
		{
			Insets insets = ( ( Container ) comp ).getInsets( );
			if( insets != null )
			{
				RectangleUtils.inset( bounds , insets , bounds );
			}
		}
		return bounds;
	}
}
