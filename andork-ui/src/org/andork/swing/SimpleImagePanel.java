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
package org.andork.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class SimpleImagePanel extends JPanel
{
	private Image	image;
	
	public SimpleImagePanel( )
	{
		
	}
	
	public SimpleImagePanel( Image image )
	{
		super( );
		this.image = image;
	}
	
	public Image getImage( )
	{
		return image;
	}
	
	public void setImage( Image image )
	{
		if( this.image != image )
		{
			this.image = image;
			repaint( );
		}
	}
	
	public Dimension getMinimumSize( )
	{
		if( !isMinimumSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getMinimumSize( );
	}
	
	public Dimension getPreferredSize( )
	{
		if( !isPreferredSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getPreferredSize( );
	}
	
	public Dimension getMaximumSize( )
	{
		if( !isMaximumSizeSet( ) && image != null )
		{
			return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
		}
		return super.getMaximumSize( );
	}
	
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		if( image != null )
		{
			g.drawImage( image , 0 , 0 , null );
		}
	}
}