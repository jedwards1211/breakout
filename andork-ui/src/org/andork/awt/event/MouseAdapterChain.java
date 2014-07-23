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
package org.andork.awt.event;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAdapterChain extends MouseAdapter
{
	MouseAdapter	first;
	MouseAdapter	second;
	
	public MouseAdapterChain( )
	{
		
	}
	
	public MouseAdapterChain( MouseAdapter first , MouseAdapter second )
	{
		super( );
		this.first = first;
		this.second = second;
	}
	
	public void install( Component c )
	{
		c.addMouseListener( this );
		c.addMouseMotionListener( this );
		c.addMouseWheelListener( this );
	}
	
	public void uninstall( Component c )
	{
		c.removeMouseListener( this );
		c.removeMouseMotionListener( this );
		c.removeMouseWheelListener( this );
	}
	
	public void addMouseAdapter( MouseAdapter adapter )
	{
		if( first == null )
		{
			first = adapter;
		}
		else
		{
			second = new MouseAdapterChain( second , adapter );
		}
	}
	
	@Override
	public void mouseClicked( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseClicked( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseClicked( e );
		}
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( first != null )
		{
			first.mousePressed( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mousePressed( e );
		}
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseReleased( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseReleased( e );
		}
	}
	
	@Override
	public void mouseEntered( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseEntered( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseEntered( e );
		}
	}
	
	@Override
	public void mouseExited( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseExited( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseExited( e );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( first != null )
		{
			first.mouseWheelMoved( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseWheelMoved( e );
		}
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseDragged( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseDragged( e );
		}
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		if( first != null )
		{
			first.mouseMoved( e );
		}
		if( !e.isConsumed( ) && second != null )
		{
			second.mouseMoved( e );
		}
	}
	
}
