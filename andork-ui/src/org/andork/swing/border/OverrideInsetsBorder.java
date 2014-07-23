/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
