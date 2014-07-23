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
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.andork.q.QObject;
import org.andork.q.QSpec;
import org.andork.q.QSpec.Attribute;
import org.andork.util.FormattedText;
import org.andork.util.StringUtils;

@SuppressWarnings( "serial" )
public class NiceTableModel<R> extends AbstractTableModel
{
	public static interface Column<R>
	{
		String getColumnName( );
		
		Class<?> getColumnClass( );
		
		boolean isCellEditable( R row );
		
		public Object getValueAt( R row );
		
		public void setValueAt( Object aValue , R row );
		
		public boolean isSortable( );
	}
	
	public static class QObjectColumn<S extends QSpec<S>, T> implements Column<QObject<S>>
	{
		public final Attribute<T>	attribute;
		
		public QObjectColumn( Attribute<T> attribute )
		{
			this.attribute = attribute;
		}
		
		public static <S extends QSpec<S>, T> QObjectColumn<S, T> newInstance( S spec , Attribute<T> attribute )
		{
			return new QObjectColumn<>( attribute );
		}
		
		@Override
		public String getColumnName( )
		{
			return attribute.getName( );
		}
		
		@Override
		public Class<?> getColumnClass( )
		{
			return attribute.getValueClass( );
		}
		
		@Override
		public boolean isCellEditable( QObject<S> row )
		{
			return true;
		}
		
		@Override
		public T getValueAt( QObject<S> row )
		{
			return row.get( attribute );
		}
		
		@Override
		public void setValueAt( Object aValue , QObject<S> row )
		{
			row.set( attribute , aValue );
		}
		
		@Override
		public boolean isSortable( )
		{
			return true;
		}
	}
	
	public static class MapColumn<R, K, V> implements Column<R>
	{
		public final Column<R>				wrapped;
		public final K						key;
		public final Class<? extends V>		valueClass;
		public final Supplier<Map<K, V>>	mapSupplier;
		
		public MapColumn( Column<R> wrapped , K key , Class<? extends V> valueClass , Supplier<Map<K, V>> mapSupplier )
		{
			this.wrapped = wrapped;
			this.key = key;
			this.valueClass = valueClass;
			this.mapSupplier = mapSupplier;
		}
		
		public static <R, K, V> MapColumn<R, K, V> newInstance( Column<R> wrapped , K key , Class<? extends V> valueClass , Supplier<Map<K, V>> mapSupplier )
		{
			return new MapColumn<>( wrapped , key , valueClass , mapSupplier );
		}
		
		@Override
		public String getColumnName( )
		{
			return key.toString( );
		}
		
		@Override
		public Class<?> getColumnClass( )
		{
			return valueClass;
		}
		
		@Override
		public boolean isCellEditable( R row )
		{
			return wrapped.isCellEditable( row );
		}
		
		@Override
		public V getValueAt( R row )
		{
			Map<K, V> map = ( Map<K, V> ) wrapped.getValueAt( row );
			return map == null ? null : map.get( key );
		}
		
		@Override
		public void setValueAt( Object aValue , R row )
		{
			Map<K, V> map = ( Map<K, V> ) wrapped.getValueAt( row );
			if( aValue == null )
			{
				if( map != null )
				{
					map.remove( key );
					if( map.isEmpty( ) )
					{
						wrapped.setValueAt( null , row );
					}
				}
			}
			else
			{
				if( map == null )
				{
					map = mapSupplier.get( );
					wrapped.setValueAt( map , row );
				}
				map.put( key , ( V ) aValue );
			}
		}
		
		@Override
		public boolean isSortable( )
		{
			return true;
		}
	}
	
	public static class FormattedTextColumn<R> implements Column<R>
	{
		public final Column<R>			wrapped;
		public final Class<?>			valueClass;
		private Supplier<FormattedText>	formattedTextSupplier;
		private boolean					sortable	= true;
		
		public FormattedTextColumn( Column<R> wrapped , Class<?> valueClass , Supplier<FormattedText> formattedTextSupplier )
		{
			this.wrapped = wrapped;
			this.valueClass = valueClass;
			this.formattedTextSupplier = formattedTextSupplier;
		}
		
		public static <R> FormattedTextColumn<R> newInstance( Column<R> wrapped , Class<?> valueClass , Supplier<FormattedText> formattedTextSupplier )
		{
			return new FormattedTextColumn<>( wrapped , valueClass , formattedTextSupplier );
		}
		
		public static <R> FormattedTextColumn<R> newInstance( Column<R> wrapped , Class<?> valueClass )
		{
			return new FormattedTextColumn<>( wrapped , valueClass , ( ) -> new FormattedText( ) );
		}
		
		@Override
		public String getColumnName( )
		{
			return wrapped.getColumnName( );
		}
		
		@Override
		public Class<?> getColumnClass( )
		{
			return FormattedText.class;
		}
		
		@Override
		public boolean isCellEditable( R row )
		{
			return wrapped.isCellEditable( row );
		}
		
		@Override
		public FormattedText getValueAt( R row )
		{
			return ( FormattedText ) wrapped.getValueAt( row );
		}
		
		@Override
		public void setValueAt( Object aValue , R row )
		{
			if( aValue instanceof FormattedText )
			{
				wrapped.setValueAt( aValue , row );
			}
			else
			{
				if( StringUtils.isNullOrEmpty( aValue ) )
				{
					wrapped.setValueAt( null , row );
				}
				else
				{
					FormattedText text = getValueAt( row );
					if( text == null )
					{
						text = formattedTextSupplier.get( );
						wrapped.setValueAt( text , row );
					}
					text.setText( StringUtils.toStringOrNull( aValue ) );
				}
			}
		}
		
		public void setFormattedTextSupplier( Supplier<FormattedText> supplier )
		{
			this.formattedTextSupplier = supplier;
		}
		
		@Override
		public boolean isSortable( )
		{
			return sortable;
		}
		
		public FormattedTextColumn<R> sortable( boolean sortable )
		{
			this.sortable = sortable;
			return this;
		}
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
		columns.get( columnIndex ).setValueAt( aValue , rows.get( rowIndex ) );
		fireTableCellUpdated( rowIndex , columnIndex );
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
