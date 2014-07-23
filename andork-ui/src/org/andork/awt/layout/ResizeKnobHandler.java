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
package org.andork.awt.layout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class ResizeKnobHandler extends MouseAdapter
{
	Component	target;
	
	Side		side;
	MouseEvent	pressEvent;
	Rectangle	pressBounds;
	
	public ResizeKnobHandler( Component target , Side side )
	{
		super( );
		this.target = target;
		this.side = side;
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( e.getButton( ) == MouseEvent.BUTTON1 )
		{
			pressEvent = e;
			pressBounds = target.getBounds( );
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( e.getButton( ) == MouseEvent.BUTTON1 )
		{
			pressEvent = null;
		}
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( pressEvent == null )
		{
			return;
		}
		
		Dimension newSize = pressBounds.getSize( );
		
		Axis axis = side.axis( );
		axis.setSize( newSize , axis.size( newSize ) +
				( axis.get( e.getLocationOnScreen( ) ) - axis.get( pressEvent.getLocationOnScreen( ) ) ) /
				side.direction( ) );
		
		Dimension minSize = target.getMinimumSize( );
		Dimension maxSize = target.getMaximumSize( );
		
		newSize.width = Math.max( minSize.width , Math.min( maxSize.width , newSize.width ) );
		newSize.height = Math.max( minSize.height , Math.min( maxSize.height , newSize.height ) );
		
		target.setPreferredSize( newSize );
		onResize( target );
	}
	
	protected void onResize( Component target )
	{
		if( target.getParent( ) != null )
		{
			if( target.getParent( ).getLayout( ) instanceof DelegatingLayoutManager )
			{
				( ( DelegatingLayoutManager ) target.getParent( ).getLayout( ) ).onLayoutChanged( target.getParent( ) );
			}
			else
			{
				target.getParent( ).invalidate( );
				target.getParent( ).validate( );
			}
		}
	}
}
