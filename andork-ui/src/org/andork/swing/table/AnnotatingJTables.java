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
import java.util.Collections;
import java.util.Map;

import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.andork.awt.AWTUtil;
import org.andork.swing.RowAnnotator;
import org.andork.swing.event.EasyDocumentListener;
import org.andork.swing.jump.JScrollAndJumpPane;

public class AnnotatingJTables
{
	public static DocumentListener createHighlightFieldListener(
			final AnnotatingJTable table ,
			final JTextComponent highlightField , final RowFilterFactory<String, TableModel, Integer> filterFactory , final Color highlightColor )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( table.getAnnotatingRowSorter( ) == null )
				{
					{
						return;
					}
				}
				
				RowFilter<TableModel, Integer> filter = null;
				
				if( highlightField.getText( ) != null && highlightField.getText( ).length( ) > 0 )
				{
					try
					{
						filter = filterFactory.createFilter( highlightField.getText( ) );
						highlightField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						highlightField.setForeground( Color.RED );
					}
				}
				if( filter != null )
				{
					table.getAnnotatingRowSorter( ).setRowAnnotator( RowAnnotator.filterAnnotator( filter ) );
					setAnnotationColors( table , Collections.singletonMap( filter , highlightColor ) );
				}
				else
				{
					highlightField.setForeground( Color.BLACK );
					setAnnotationColors( table , Collections.<RowFilter<TableModel, Integer>,Color>emptyMap( ) );
				}
				
			}
		};
	}
	
	private static void setAnnotationColors( AnnotatingJTable table , Map<?, Color> colors )
	{
		table.setAnnotationColors( colors );
		JScrollAndJumpPane scrollPane = AWTUtil.getAncestorOfClass( JScrollAndJumpPane.class , table );
		if( scrollPane != null )
		{
			scrollPane.getJumpBar( ).setColorMap( colors );
		}
	}
	
	public static DocumentListener createFilterFieldListener(
			final AnnotatingJTable table , final JTextComponent filterField ,
			final RowFilterFactory<String, TableModel, Integer> filterFactory )
	{
		return new EasyDocumentListener( )
		{
			@Override
			public void documentChanged( DocumentEvent e )
			{
				if( table.getAnnotatingRowSorter( ) == null )
				{
					return;
				}
				if( filterField.getText( ) != null && filterField.getText( ).length( ) > 0 )
				{
					RowFilter<TableModel, Integer> filter = null;
					try
					{
						filter = filterFactory.createFilter( filterField.getText( ) );
						filterField.setForeground( Color.BLACK );
					}
					catch( Exception ex )
					{
						filterField.setForeground( Color.RED );
					}
					table.getAnnotatingRowSorter( ).setRowFilter( filter );
				}
				else
				{
					filterField.setForeground( Color.BLACK );
					
					table.getAnnotatingRowSorter( ).setRowFilter( null );
				}
			}
		};
	}
	
}
