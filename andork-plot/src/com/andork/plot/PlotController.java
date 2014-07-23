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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import org.andork.swing.event.MouseEventFaker;

import com.andork.plot.PlotAxis.Orientation;

public class PlotController extends MouseAdapter
{
	private Component			view;
	private PlotAxisController	haxis;
	private PlotAxisController	vaxis;
	
	public PlotController( Component view , PlotAxisController haxis , PlotAxisController vaxis )
	{
		super( );
		this.view = view;
		this.haxis = haxis;
		this.vaxis = vaxis;
	}
	
	private MouseEvent convertForAxis( MouseEvent e , PlotAxis axis )
	{
		Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , axis );
		if( axis.getOrientation( ) == Orientation.HORIZONTAL )
		{
			p.y = axis.getHeight( ) / 2;
		}
		else
		{
			p.x = axis.getWidth( ) / 2;
		}
		
		if( e instanceof MouseWheelEvent )
		{
			MouseWheelEvent we = ( MouseWheelEvent ) e;
			return new MouseWheelEvent( axis , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , p.x , p.y , e.getClickCount( ) ,
					e.isPopupTrigger( ) , we.getScrollType( ) , we.getScrollAmount( ) , we.getWheelRotation( ) );
		}
		else
		{
			int xAbs = e.getXOnScreen( ) + p.x - e.getX( );
			int yAbs = e.getYOnScreen( ) + p.y - e.getY( );
			return new MouseEvent( axis , e.getID( ) , e.getWhen( ) , e.getModifiers( ) , p.x , p.y , xAbs , yAbs ,
					e.getClickCount( ) , e.isPopupTrigger( ) , e.getButton( ) );
		}
	}
	
	private void retarget( MouseEvent e , PlotAxisController axis )
	{
		boolean enableZoom = axis.isEnableZoom( );
		if( e instanceof MouseWheelEvent )
		{
			axis.setEnableZoom( true );
		}
		MouseEventFaker.dispatch( convertForAxis( e , axis.getView( ) ) , axis.getMouseHandler( ) );
		axis.setEnableZoom( enableZoom );
	}
	
	private void retarget( MouseEvent e )
	{
		if( haxis != null )
		{
			retarget( e , haxis );
		}
		if( vaxis != null )
		{
			retarget( e , vaxis );
		}
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		retarget( e );
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		retarget( e );
	}
}
