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
package com.andork.plot;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

@SuppressWarnings( "serial" )
public class AxisLinkButton extends JToggleButton
{
	PlotAxisController[ ]	axisControllers;
	PlotAxis[ ]				axes;
	
	public AxisLinkButton( PlotAxisController ... axisControllers )
	{
		this.axisControllers = axisControllers;
		axes = new PlotAxis[ axisControllers.length ];
		for( int i = 0 ; i < axisControllers.length ; i++ )
		{
			axes[ i ] = axisControllers[ i ].getView( );
		}
		
		setIcon( new ImageIcon( getClass( ).getResource( "unlink.png" ) ) );
		setSelectedIcon( new ImageIcon( getClass( ).getResource( "link.png" ) ) );
		
		addItemListener( new ItemListener( )
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if( isSelected( ) )
				{
					PlotAxis.equalizeScale( axes );
				}
				for( PlotAxisController axisController : AxisLinkButton.this.axisControllers )
				{
					axisController.setEnableZoom( !isSelected( ) );
				}
			}
		} );
	}
}
