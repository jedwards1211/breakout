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

import java.awt.Component;
import java.awt.Cursor;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

public class AnnotatingRowSorterCursorController implements RowSorterListener
{
	Component						target;
	
	Set<AnnotatingRowSorter<?, ?>>	busySorters	= new HashSet<AnnotatingRowSorter<?, ?>>( );
	
	public AnnotatingRowSorterCursorController( Component target )
	{
		super( );
		this.target = target;
	}
	
	@Override
	public void sorterChanged( RowSorterEvent e )
	{
		if( e.getSource( ) instanceof AnnotatingRowSorter )
		{
			AnnotatingRowSorter<?, ?> sorter = ( org.andork.swing.AnnotatingRowSorter<?, ?> ) e.getSource( );
			if( sorter.isSortingInBackground( ) )
			{
				busySorters.add( sorter );
			}
			else
			{
				busySorters.remove( sorter );
			}
			
			target.setCursor( busySorters.isEmpty( ) ? null : Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
		}
	}
}
