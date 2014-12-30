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
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.andork.awt.layout.DelegatingLayoutManager.LayoutDelegate;

public class DrawerAutoshowController extends MouseAdapter
{
	int	autoshowDistance	= 30;

	private static final class AutoshowDrawerHolder
	{

	}

	public static final AutoshowDrawerHolder	autoshowDrawerHolder	= new AutoshowDrawerHolder( );

	public int getAutoshowDistance( )
	{
		return autoshowDistance;
	}

	public void setAutoshowDistance( int autoshowDistance )
	{
		this.autoshowDistance = autoshowDistance;
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		Component c = e.getComponent( );

		while( c != null && ( ! ( c instanceof Container ) ||
			! ( ( ( Container ) c ).getLayout( ) instanceof DelegatingLayoutManager ) ) )
		{
			c = c.getParent( );
		}

		if( c == null )
		{
			return;
		}

		Container parent = ( Container ) c;

		DelegatingLayoutManager layout = ( DelegatingLayoutManager ) ( ( Container ) parent ).getLayout( );

		for( Component comp : parent.getComponents( ) )
		{
			if( comp instanceof Drawer )
			{
				Drawer drawer = ( Drawer ) comp;
				Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , comp );
				if( RectangleUtils.rectilinearDistance( SwingUtilities.getLocalBounds( comp ) , p ) < autoshowDistance )
				{
					drawer.holder( ).hold( autoshowDrawerHolder );
				}
				else
				{
					drawer.holder( ).release( autoshowDrawerHolder );
				}
			}
			else
			{
				LayoutDelegate delegate = layout.getDelegate( comp );
				if( delegate instanceof DrawerLayoutDelegate )
				{
					DrawerLayoutDelegate drawerDelegate = ( DrawerLayoutDelegate ) delegate;

					Point p = SwingUtilities.convertPoint( e.getComponent( ) , e.getPoint( ) , comp );
					drawerDelegate.setOpen( RectangleUtils.rectilinearDistance(
						SwingUtilities.getLocalBounds( comp ) , p ) < autoshowDistance );
				}
			}
		}
	}
}
