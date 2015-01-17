package org.andork.breakout.table;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.andork.swing.table.NiceTableModel.Column;
import org.andork.swing.table.TableModelList;
import org.andork.util.Java7.Objects;
import org.andork.util.PowerCloneable.Cloners;

/**
 * The model for {@link SurveyDataRowTable}. It is backed by a {@link SurveyDataList}, and keeps up-to-date with its
 * columns and rows. {@link SurveyDataRowTableColumnModel} and {@link SurveyDataTableModel} form
 * the
 * presenter layer of an MVP pattern, where {@link SurveyDataList} is the model and {@link SurveyDataRowTable} is the
 * view.<br>
 * <br>
 * It displays the {@link SurveyDataList#getPrototypeDataRow() prototypeDataRow} of the {@code SurveyDataList} as
 * the last row, so it is
 * always one row longer than {@link SurveyDataList}; when the user enters a value in the last row,
 * {@link SurveyDataTableModel} will add a new {@link SurveyDataRow} to the {@link SurveyDataList} and populate it
 * with the new value.<br>
 * <br>
 * {@link SurveyDataTableModel} will also shrink the {@code SurveyDataList} as necessary if the user deletes the
 * contents of rows at
 * the
 * end so that the last element of the {@code SurveyDataList} is always non-empty, followed in this
 * {@code SurveyDataRowTableModel} by
 * the
 * empty prototype row.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public abstract class SurveyDataTableModel<R extends SurveyDataRow> extends AbstractTableModel
{
	private SurveyDataList<R>					dataList;
	private SurveyDataListListener				dataListListener	= new SurveyDataListListener( );

	private final List<SurveyDataModelColumn>	columns				= new ArrayList<>( );
	private final Map<SurveyDataColumnDef, Integer>	defIndices			= new HashMap<>( );

	public SurveyDataTableModel( )
	{
	}

	/**
	 * @return the {@link SurveyDataList} containing the backing data for this model.
	 */
	public SurveyDataList<R> getSurveyDataList( )
	{
		return dataList;
	}

	/**
	 * Sets the {@link SurveyDataList} containing the backing data for this model. The appropriate
	 * {@link TableModelEvent}s
	 * will be fired, if the new list is different from the current one.
	 * 
	 * @param newList
	 */
	public void setSurveyDataList( SurveyDataList<R> newList )
	{
		if( dataList != newList )
		{
			if( dataList != null )
			{
				dataList.removeListener( dataListListener );
			}
			dataList = newList;
			if( newList != null )
			{
				newList.addListener( dataListListener );
			}
			setColumns( createColumns( ) );
		}
	}

	/**
	 * @param def
	 *            the {@link SurveyDataColumnDef} to look for.
	 * @return the column index of {@code def}, or -1 if it is not one of the columns of this model.
	 */
	public int indexOfColumn( SurveyDataColumnDef def )
	{
		Integer result = defIndices.get( def );
		return result == null ? -1 : result;
	}

	private void setColumns( List<SurveyDataModelColumn> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		defIndices.clear( );
		int i = 0;
		for( SurveyDataModelColumn column : newColumns )
		{
			defIndices.put( column.def , i++ );
		}
		fireTableStructureChanged( );
	}

	private List<SurveyDataModelColumn> createColumns( )
	{
		if( dataList == null )
		{
			return Collections.emptyList( );
		}

		List<SurveyDataModelColumn> result = new ArrayList<>( );

		addBuiltinColumnsTo( result );

		int custCols = 0;
		for( SurveyDataColumnDef def : dataList.getCustomColumnDefs( ) )
		{
			SurveyDataModelColumn col = createCustomColumn( custCols++ , def );
			if( col != null )
			{
				result.add( col );
			}
		}

		return result;
	}

	protected abstract void addBuiltinColumnsTo( List<SurveyDataModelColumn> result );

	@SuppressWarnings( "unchecked" )
	private SurveyDataModelColumn createCustomColumn( int index , SurveyDataColumnDef def )
	{
		switch( def.type )
		{
		case STRING:
			return new DefaultColumn( def , s -> ( String ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( String ) v );
		case INTEGER:
			return new DefaultColumn( def , s -> ( ParsedText<Integer> ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( ParsedText<Integer> ) v );
		case DOUBLE:
			return new DefaultColumn( def , s -> ( ParsedText<Double> ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( ParsedText<Double> ) v );
		case TAGS:
			return new DefaultColumn( def , s -> ( LinkedHashSet<String> ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( LinkedHashSet<String> ) v );
		case SECTION:
			return new DefaultColumn( def , s -> ( List<String> ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( List<String> ) v );
		case LINK:
			return new DefaultColumn( def , s -> ( ParsedText<URL> ) s.getCustom( )[ index ] ,
				( s , v ) -> s.getCustom( )[ index ] = ( ParsedText<URL> ) v );
		default:
			return null;
		}
	}

	@Override
	public int getRowCount( )
	{
		return dataList != null ? dataList.size( ) + 1 : 0;
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
		return columns.get( columnIndex ).isCellEditable( rowIndex );
	}

	@Override
	public void setValueAt( Object aValue , int rowIndex , int columnIndex )
	{
		if( columns.get( columnIndex ).setValueAt( aValue , rowIndex ) )
		{
			fireTableRowsUpdated( rowIndex , rowIndex );
		}
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex )
	{
		return columns.get( columnIndex ).getValueAt( rowIndex );
	}

	/**
	 * @param rowIndex
	 * @return a {@link List} of all {@link ParsedText#getNote() note}s in any cells in the row at {@code rowIndex}, or
	 *         {@code null} if there are no notes.
	 */
	public List<Object> getNotesInRow( int rowIndex )
	{
		List<Object> notes = null;

		for( int colIndex = 0 ; colIndex < getColumnCount( ) ; colIndex++ )
		{
			Object o = getValueAt( rowIndex , colIndex );
			if( o instanceof ParsedText )
			{
				ParsedText<?> p = ( ParsedText<?> ) o;
				if( p.getNote( ) != null )
				{
					if( notes == null )
					{
						notes = new LinkedList<Object>( );
					}
					notes.add( p.getNote( ) );
				}
			}
		}

		return notes;
	}

	private class SurveyDataListListener implements TableModelList.Listener<R>
	{
		@Override
		public void elementsInserted( TableModelList<R> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( SurveyDataTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.INSERT ) );
		}

		@Override
		public void elementsDeleted( TableModelList<R> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( SurveyDataTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.DELETE ) );
		}

		@Override
		public void elementsUpdated( TableModelList<R> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( SurveyDataTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}

		@Override
		public void dataChanged( TableModelList<R> list )
		{
			fireTableDataChanged( );
		}

		@Override
		public void structureChanged( TableModelList<R> list )
		{
			setColumns( createColumns( ) );
		}
	}

	public abstract class SurveyDataModelColumn implements Column<Integer>
	{
		public final SurveyDataColumnDef	def;

		public SurveyDataModelColumn( SurveyDataColumnDef def )
		{
			super( );
			this.def = def;
		}

		@Override
		public String getColumnName( )
		{
			return def.name;
		}
	}

	protected class DefaultColumn extends SurveyDataModelColumn
	{
		/**
		 * @param def
		 *            the definition for this column.
		 * @param valueGetter
		 *            given a {@link SurveyDataRow}, gets the value for its property represented by this column.
		 * @param valueSetter
		 *            given a {@link SurveyDataRow} and a new value, sets the dataRow's property represented by this
		 *            column to the new value.
		 */
		public DefaultColumn( SurveyDataColumnDef def ,
			Function<SurveyDataRow, ?> valueGetter ,
			BiConsumer<SurveyDataRow, Object> valueSetter )
		{
			super( def );
			this.valueGetter = valueGetter;
			this.valueSetter = valueSetter;
		}

		Function<SurveyDataRow, ?>			valueGetter;
		BiConsumer<SurveyDataRow, Object>	valueSetter;

		@Override
		public Class<?> getColumnClass( )
		{
			return String.class;
		}

		@Override
		public boolean isCellEditable( Integer row )
		{
			return true;
		}

		@Override
		public Object getValueAt( Integer row )
		{
			return valueGetter.apply( row == dataList.size( ) ? dataList.getPrototypeDataRow( )
				: dataList.get( row ) );
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			if( row == dataList.size( ) )
			{
				// the last row is always an empty placeholder view of the prototype dataRow, which is not actually part of 
				// the list.

				if( updatePrototypeDataRow( aValue ) )
				{
					// If the user just changed the vector type, but didn't enter any text, we can just update the
					// prototype dataRow, because we want the new type to remain showing in the last row.

					Object oldValue = valueGetter.apply( dataList.getPrototypeDataRow( ) );
					valueSetter.accept( dataList.getPrototypeDataRow( ) , aValue );
					return !Objects.equals( oldValue , aValue );
				}

				// In this case, the user changed the text, so we add a clone of the prototype dataRow to the end of the list
				// and this clone will get updated below.
				dataList.add( ( R ) dataList.getPrototypeDataRow( ).clone( Cloners::defaultClone ) );
			}

			Object oldValue = valueGetter.apply( dataList.get( row ) );
			valueSetter.accept( dataList.get( row ) , aValue );

			{
				dataList.trimEmptyRowsAtEnd( );
			}

			return !Objects.equals( oldValue , aValue );
		}

		protected boolean updatePrototypeDataRow( Object aValue )
		{
			return ( aValue instanceof ParsedText<?> && ( ( ParsedText<?> ) aValue ).isEmpty( ) );
		}
	}

	protected class UnitColumn extends DefaultColumn
	{

		public UnitColumn( SurveyDataColumnDef def , Function<SurveyDataRow, ?> valueGetter ,
			BiConsumer<SurveyDataRow, Object> valueSetter )
		{
			super( def , valueGetter , valueSetter );
		}

		@Override
		protected boolean updatePrototypeDataRow( Object aValue )
		{
			return true;
		}
	}
}
