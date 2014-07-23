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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class SpinnerButtonUI extends BasicButtonUI
{
	AbstractButton		button;
	static Icon[ ]		icons;
	static Icon			offIcon;
	javax.swing.Timer	timer;
	
	boolean				spinning	= false;
	
	static
	{
		icons = new Icon[ 30 ];
		for( int i = 0 ; i < 30 ; i++ )
		{
			icons[ i ] = new ImageIcon( SpinnerButtonUI.class.getResource( "spinner-" + i + ".png" ) );
		}
		
		offIcon = new ImageIcon( SpinnerButtonUI.class.getResource( "spinner-off.png" ) );
	}
	
	public SpinnerButtonUI( )
	{
		timer = new Timer( 30 , new ActionListener( )
		{
			int	frame	= 0;
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( button != null && spinning )
				{
					if( !button.isShowing( ) )
					{
						timer.stop( );
					}
					frame++ ;
					if( frame >= icons.length )
					{
						frame %= icons.length;
					}
					button.setIcon( icons[ frame ] );
					button.setSelectedIcon( icons[ frame ] );
					button.repaint( );
				}
			}
		} );
	}
	
	public boolean isSpinning( )
	{
		return spinning;
	}
	
	public void setSpinning( boolean spinning )
	{
		this.spinning = spinning;
		if( !spinning )
		{
			timer.stop( );
			button.setIcon( offIcon );
			button.setSelectedIcon( offIcon );
			button.repaint( );
		}
		else
		{
			timer.start( );
		}
	}
	
	@Override
	public void installUI( JComponent c )
	{
		super.installUI( c );
		button = ( AbstractButton ) c;
		button.setMargin( new Insets( 0 , 0 , 0 , 0 ) );
		button.setFocusPainted( false );
		button.setMinimumSize( new Dimension( icons[ 0 ].getIconWidth( ) , icons[ 0 ].getIconHeight( ) ) );
		button.setPreferredSize( new Dimension( icons[ 0 ].getIconWidth( ) , icons[ 0 ].getIconHeight( ) ) );
		button.setIcon( offIcon );
		button.setSelectedIcon( offIcon );
	}
	
	@Override
	public void update( Graphics g , JComponent c )
	{
		super.update( g , c );
		timer.start( );
	}
}
