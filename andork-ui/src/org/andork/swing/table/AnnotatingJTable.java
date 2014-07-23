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
import java.awt.Component;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.AnnotatingRowSorter;

/**
 * A {@link BetterJTable} with the following added features:
 * <ul>
 * <li>It will invoke {@link AnnotatingTableCellRenderer}s with annotations from an {@link AnnotatingRowSorter} if one is installed.
 * </ul>
 * 
 * @author Andy
 */
@SuppressWarnings( "serial" )
public class AnnotatingJTable extends BetterJTable
{
	protected Function<Object, Color>	colorer;
	
	public AnnotatingJTable( )
	{
		super( );
		init( );
	}
	
	public AnnotatingJTable( int numRows , int numColumns )
	{
		super( numRows , numColumns );
		init( );
	}
	
	public AnnotatingJTable( Object[ ][ ] rowData , Object[ ] columnNames )
	{
		super( rowData , columnNames );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm , ListSelectionModel sm )
	{
		super( dm , cm , sm );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm , TableColumnModel cm )
	{
		super( dm , cm );
		init( );
	}
	
	public AnnotatingJTable( TableModel dm )
	{
		super( dm );
		init( );
	}
	
	public AnnotatingJTable( Vector rowData , Vector columnNames )
	{
		super( rowData , columnNames );
		init( );
	}
	
	@Override
	public Component prepareRenderer( TableCellRenderer renderer , int row , int column )
	{
		Component comp = null;
		
		Object value = getValueAt( row , column );
		
		boolean isSelected = false;
		boolean hasFocus = false;
		
		// Only indicate the selection and focused cell if not printing
		if( !isPaintingForPrint( ) )
		{
			isSelected = isCellSelected( row , column );
			
			boolean rowIsLead =
					( selectionModel.getLeadSelectionIndex( ) == row );
			boolean colIsLead =
					( columnModel.getSelectionModel( ).getLeadSelectionIndex( ) == column );
			
			hasFocus = ( rowIsLead && colIsLead ) && isFocusOwner( );
		}
		
		if( renderer instanceof AnnotatingTableCellRenderer )
		{
			comp = ( ( AnnotatingTableCellRenderer ) renderer ).getTableCellRendererComponent(
					this , value , getAnnotation( row ) , isSelected , hasFocus , row , column );
		}
		// this isn't nice but it's the only way I can figure out to reset the component background
		// and still have the renderer update it afterward
		comp = super.prepareRenderer( renderer , row , column );
		comp.setBackground( getBackground( ) );
		comp = super.prepareRenderer( renderer , row , column );
		
		if( colorer != null && !isSelected )
		{
			Object annotation = getAnnotation( row );
			if( annotation != null )
			{
				Color color = colorer.apply( annotation );
				if( color != null )
				{
					comp.setBackground( color );
				}
			}
		}
		
		return comp;
	}
	
	public void setAnnotationColors( Map<?, Color> annotationColors )
	{
		colorer = annotationColors == null ? null : a -> annotationColors.get( a );
	}
	
	public void setColorer( Function<Object, Color> colorer )
	{
		this.colorer = colorer;
	}
	
	public Object getAnnotation( int row )
	{
		if( getRowSorter( ) instanceof AnnotatingRowSorter )
		{
			return ( ( AnnotatingRowSorter<? extends TableModel, Integer> ) getRowSorter( ) ).getAnnotation( row );
		}
		return null;
	}
	
	public AnnotatingRowSorter<? extends TableModel, Integer> getAnnotatingRowSorter( )
	{
		if( getRowSorter( ) instanceof AnnotatingRowSorter )
		{
			return ( AnnotatingRowSorter<? extends TableModel, Integer> ) getRowSorter( );
		}
		return null;
	}
}
