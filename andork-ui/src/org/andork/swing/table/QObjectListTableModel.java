package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.andork.q2.QObject;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;
import org.andork.swing.table.NiceTableModel.Column;

/**
 * A {@link TableModel} backed by a {@link QObjectList} (every row is a {@link QObject}). You specify the columns which
 * determine how the properties of the {@link QObject}s get mapped to table cells.
 * 
 * @author James
 *
 * @param <S>
 *            the {@link QSpec} of the {@link QObject}s.
 */
@SuppressWarnings( "serial" )
public class QObjectListTableModel<S extends QSpec> extends AbstractTableModel implements QObjectList.Listener<S>
{
	private QObjectList<S>								model;

	private final List<Column<QObject<? extends S>>>	columns				= new ArrayList<>( );
	private final List<Column<QObject<? extends S>>>	unmodifiableColumns	= Collections
																				.unmodifiableList( columns );

	public QObjectListTableModel( )
	{

	}

	public QObjectListTableModel( QObjectList<S> model )
	{
		setModel( model );
	}

	public void setModel( QObjectList<S> newModel )
	{
		if( model != newModel )
		{
			if( model != null )
			{
				model.removeListener( this );
			}
			model = newModel;
			if( newModel != null )
			{
				newModel.addListener( this );
			}
			fireTableDataChanged( );
		}
	}

	public QObjectList<S> getModel( )
	{
		return model;
	}

	public List<Column<QObject<? extends S>>> getColumns( )
	{
		return unmodifiableColumns;
	}

	public void setColumns( List<Column<QObject<? extends S>>> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		fireTableStructureChanged( );
	}

	@Override
	public int getRowCount( )
	{
		return model == null ? 0 : model.size( );
	}

	@Override
	public int getColumnCount( )
	{
		return columns.size( );
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
	public boolean isCellEditable( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).isCellEditable( model.get( rowIndex ) );
	}

	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		columns.get( columnIndex ).setValueAt( aValue , model.get( rowIndex ) );
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).getValueAt( model.get( rowIndex ) );
	}

	@Override
	public void elementsInserted( QObjectList<? extends S> list , int fromIndex , int toIndex )
	{
		fireTableChanged( new TableModelEvent( this , fromIndex , toIndex , TableModelEvent.ALL_COLUMNS ,
			TableModelEvent.INSERT ) );
	}

	@Override
	public void elementsDeleted( QObjectList<? extends S> list , int fromIndex , int toIndex )
	{
		fireTableChanged( new TableModelEvent( this , fromIndex , toIndex , TableModelEvent.ALL_COLUMNS ,
			TableModelEvent.DELETE ) );
	}

	@Override
	public void elementsUpdated( QObjectList<? extends S> list , int fromIndex , int toIndex )
	{
		fireTableChanged( new TableModelEvent( this , fromIndex , toIndex , TableModelEvent.ALL_COLUMNS ,
			TableModelEvent.UPDATE ) );
	}

	@Override
	public void elementsUpdated( QObjectList<? extends S> list , int fromIndex , int toIndex , Property<?> property )
	{
		fireTableChanged( new TableModelEvent( this , fromIndex , toIndex , TableModelEvent.ALL_COLUMNS ,
			TableModelEvent.UPDATE ) );
	}

	@SuppressWarnings( "rawtypes" )
	public static class PropertyColumn<S extends QSpec> implements Column<QObject<? extends S>>
	{
		protected final Property	property;
		protected boolean			editable;

		public PropertyColumn( Property property , boolean editable )
		{
			super( );
			this.property = property;
			this.editable = editable;
		}

		@Override
		public String getColumnName( )
		{
			return property.name( );
		}

		@Override
		public Class<?> getColumnClass( )
		{
			return property.type( );
		}

		@Override
		public boolean isCellEditable( QObject<? extends S> row )
		{
			return editable;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public Object getValueAt( QObject<? extends S> row )
		{
			return row.get( property );
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public boolean setValueAt( Object aValue , QObject<? extends S> row )
		{
			Object oldValue = row.set( property , aValue );
			return !property.equals( oldValue , aValue );
		}
	}
}
