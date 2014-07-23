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
	
	public abstract Paint getPaint( Component c , Graphics g , int x , int y , int width , int height );
	
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
