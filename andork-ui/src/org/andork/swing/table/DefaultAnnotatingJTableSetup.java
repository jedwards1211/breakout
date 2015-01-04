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
package org.andork.swing.table;

import java.awt.Color;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorterCursorController;
import org.andork.swing.jump.JScrollAndJumpPane;
import org.andork.swing.jump.JTableJumpSupport;

public class DefaultAnnotatingJTableSetup
{
	public final JScrollAndJumpPane						scrollPane;
	public final AnnotatingJTable						table;
	public final AnnotatingRowSorterCursorController	cursorController;

	public DefaultAnnotatingJTableSetup( AnnotatingJTable table , Consumer<Runnable> sortRunner )
	{
		this.table = table;
		AnnotatingTableRowSorter sorter;
		if( table.getRowSorter( ) instanceof AnnotatingTableRowSorter )
		{
			sorter = ( AnnotatingTableRowSorter ) table.getRowSorter( );
		}
		else
		{
			sorter = new AnnotatingTableRowSorter( table , sortRunner );
			table.setRowSorter( sorter );
		}

		scrollPane = new JScrollAndJumpPane( table );
		scrollPane.getJumpBar( ).setModel( new AnnotatingJTableJumpBarModel( table ) );
		scrollPane.getJumpBar( ).setJumpSupport( new JTableJumpSupport( table ) );

		cursorController = new AnnotatingRowSorterCursorController( scrollPane );
		sorter.addRowSorterListener( cursorController );
	}

	protected AnnotatingJTable createTable( TableModel model )
	{
		return new AnnotatingJTable( model );
	}

	public void setAnnotationColors( Map<?, Color> colors )
	{
		table.setAnnotationColors( colors );
		scrollPane.getJumpBar( ).setColorMap( colors );
	}

	public void setColorer( Function<Object, Color> colorer )
	{
		table.setColorer( colorer );
		scrollPane.getJumpBar( ).setColorer( colorer );
	}
}
