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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings( "serial" )
public class NiceTableModel<R> extends AbstractTableModel
{
	public static interface Column<R>
	{
		public abstract String getColumnName( );

		public abstract Class<?> getColumnClass( );

		public abstract boolean isCellEditable( R row );

		public abstract Object getValueAt( R row );

		/**
		 * @param aValue
		 * @param row
		 * @return if {@code true}, this {@code NiceTableModel} should fire a table cell updated event.
		 */
		public abstract boolean setValueAt( Object aValue , R row );
	}

	private final List<Column<R>>	columns				= new ArrayList<>( );
	private final List<Column<R>>	unmodifiableColumns	= Collections.unmodifiableList( columns );
	private final List<R>			rows				= new ArrayList<>( );
	private final List<R>			unmodifiableRows	= Collections.unmodifiableList( rows );

	protected void addRow( R row )
	{
		addRow( rows.size( ) , row );
	}

	protected void addRow( int index , R row )
	{
		rows.add( index , row );
		fireTableRowsInserted( index , index );
	}

	protected void addRows( Collection<? extends R> rows )
	{
		addRows( this.rows.size( ) , rows );
	}

	protected void addRows( int index , Collection<? extends R> rows )
	{
		if( !rows.isEmpty( ) )
		{
			this.rows.addAll( index , rows );
			fireTableRowsInserted( index , index + rows.size( ) - 1 );
		}
	}

	protected void removeRow( int index )
	{
		rows.remove( index );
		fireTableRowsDeleted( index , index );
	}

	protected void removeRows( int ... rows )
	{
		for( int i = rows.length - 1 ; i >= 0 ; i-- )
		{
			this.rows.remove( rows[ i ] );
		}
		fireTableDataChanged( );
	}

	protected void removeRows( int firstIndex , int lastIndex )
	{
		rows.subList( firstIndex , lastIndex + 1 ).clear( );
		fireTableRowsDeleted( firstIndex , lastIndex );
	}

	protected void clearRows( )
	{
		rows.clear( );
		fireTableDataChanged( );
	}

	protected void setRows( Collection<? extends R> rows )
	{
		this.rows.clear( );
		this.rows.addAll( rows );
		fireTableDataChanged( );
	}

	protected void setRow( int index , R row )
	{
		rows.set( index , row );
		fireTableRowsUpdated( index , index );
	}

	protected void setRows( int index , Collection<? extends R> rows )
	{
		if( !rows.isEmpty( ) )
		{
			List<R> sublist = this.rows.subList( index , index + rows.size( ) );
			sublist.clear( );
			sublist.addAll( index , rows );
			fireTableRowsUpdated( index , index + rows.size( ) - 1 );
		}
	}

	protected void addColumn( Column<R> column )
	{
		columns.add( column );
		fireTableStructureChanged( );
	}

	protected void removeColumn( Column<R> column )
	{
		columns.remove( column );
		fireTableStructureChanged( );
	}

	protected void setColumns( Collection<? extends Column<R>> columns )
	{
		this.columns.clear( );
		this.columns.addAll( columns );
		fireTableStructureChanged( );
	}

	protected R getRow( int rowIndex )
	{
		return rows.get( rowIndex );
	}

	protected List<R> getRows( )
	{
		return unmodifiableRows;
	}

	protected Column<R> getColumn( int columnIndex )
	{
		return columns.get( columnIndex );
	}

	protected List<Column<R>> getColumns( )
	{
		return unmodifiableColumns;
	}

	protected int indexOfColumn( Column<R> column )
	{
		return columns.indexOf( column );
	}

	@Override
	public String getColumnName( int column )
	{
		return columns.get( column ).getColumnName( );
	}

	@Override
	public Class<?> getColumnClass( int columnIndex )
	{
		return columns.get( columnIndex ).getColumnClass( );
	}

	@Override
	public int getRowCount( )
	{
		return rows.size( );
	}

	@Override
	public int getColumnCount( )
	{
		return columns.size( );
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).getValueAt( rows.get( rowIndex ) );
	}

	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		if( columns.get( columnIndex ).setValueAt( aValue , rows.get( rowIndex ) ) )
		{
			fireTableCellUpdated( rowIndex , columnIndex );
		}
	}

	@Override
	public boolean isCellEditable( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).isCellEditable( rows.get( rowIndex ) );
	}

	public static <R> TableColumn getTableColumn( JTable table , Column<R> column )
	{
		NiceTableModel<R> model = ( NiceTableModel<R> ) table.getModel( );
		int index = model.indexOfColumn( column );
		if( index >= 0 )
		{
			index = table.convertColumnIndexToView( index );
		}
		if( index < 0 )
		{
			return null;
		}
		return table.getColumnModel( ).getColumn( index );
	}
}
