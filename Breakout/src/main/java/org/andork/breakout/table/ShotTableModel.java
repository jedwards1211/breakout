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
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.Java7.Objects;
import org.andork.util.PowerCloneable.Cloners;

/**
 * The model for {@link ShotTable}. It is backed by a {@link ShotList}, and keeps up-to-date with its columns and rows.
 * {@link ShotTableColumnModel} and {@link ShotTableModel} form
 * the
 * presenter layer of an MVP pattern, where {@link ShotList} is the model and {@link ShotTable} is the view.<br>
 * <br>
 * It displays the {@link ShotList#getPrototypeShot() prototypeShot} of the {@code ShotList} as the last row, so it is
 * always one row longer than {@link ShotList}; when the user enters a value in the last row, {@link ShotTableModel}
 * will add a new {@link Shot} to the {@link ShotList} and populate it with the new value.<br>
 * <br>
 * {@link ShotTableModel} will also shrink the {@code ShotList} as necessary if the user deletes the contents of rows at
 * the
 * end so that the last element of the {@code ShotList} is always non-empty, followed in this {@code ShotTableModel} by
 * the
 * empty prototype row.
 * 
 * @author James
 */
@SuppressWarnings( "serial" )
public class ShotTableModel extends AbstractTableModel
{
	private ShotList							shotList;
	private ShotListListener					shotListListener	= new ShotListListener( );

	private final List<ShotModelColumn>			columns				= new ArrayList<>( );
	private final Map<SurveyDataColumnDef, Integer>	defIndices			= new HashMap<>( );

	public final ShotModelColumn				fromStationNameColumn;
	public final ShotModelColumn				toStationNameColumn;
	public final ShotModelColumn				vectorColumn;
	public final ShotModelColumn				xSectionAtFromColumn;
	public final ShotModelColumn				xSectionAtToColumn;
	public final ShotModelColumn				lengthUnitColumn;
	public final ShotModelColumn				angleUnitColumn;

	public ShotTableModel( )
	{
		fromStationNameColumn = new DefaultColumn(
			SurveyDataColumnDef.fromStationName , s -> s.getFromStationName( ) ,
			( s , v ) -> s.setFromStationName( ( String ) v ) );

		toStationNameColumn = new DefaultColumn(
			SurveyDataColumnDef.toStationName , s -> s.getToStationName( ) ,
			( s , v ) -> s.setToStationName( ( String ) v ) );

		vectorColumn = new DefaultColumn(
			SurveyDataColumnDef.vector , s -> s.getVector( ) ,
			( s , v ) -> s.setVector( ( ParsedTextWithType<ShotVector> ) v ) );

		xSectionAtFromColumn = new DefaultColumn(
			SurveyDataColumnDef.xSectionAtFrom , s -> s.getXSectionAtFrom( ) ,
			( s , v ) -> s.setXSectionAtFrom( ( ParsedTextWithType<XSection> ) v ) );

		xSectionAtToColumn = new DefaultColumn(
			SurveyDataColumnDef.xSectionAtTo , s -> s.getXSectionAtTo( ) ,
			( s , v ) -> s.setXSectionAtTo( ( ParsedTextWithType<XSection> ) v ) );

		lengthUnitColumn = new UnitColumn(
			SurveyDataColumnDef.lengthUnit , s -> s.getLengthUnit( ) ,
			( s , u ) -> s.setLengthUnit( ( Unit<Length> ) u ) );

		angleUnitColumn = new UnitColumn(
			SurveyDataColumnDef.angleUnit , s -> s.getAngleUnit( ) ,
			( s , u ) -> s.setAngleUnit( ( Unit<Angle> ) u ) );
	}

	/**
	 * @return the {@link ShotList} containing the backing data for this model.
	 */
	public ShotList getShotList( )
	{
		return shotList;
	}

	/**
	 * Sets the {@link ShotList} containing the backing data for this model. The appropriate {@link TableModelEvent}s
	 * will be fired, if the new list is different from the current one.
	 * 
	 * @param newList
	 */
	public void setShotList( ShotList newList )
	{
		if( shotList != newList )
		{
			if( shotList != null )
			{
				shotList.removeListener( shotListListener );
			}
			shotList = newList;
			if( newList != null )
			{
				newList.addListener( shotListListener );
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

	private void setColumns( List<ShotModelColumn> newColumns )
	{
		columns.clear( );
		columns.addAll( newColumns );
		defIndices.clear( );
		int i = 0;
		for( ShotModelColumn column : newColumns )
		{
			defIndices.put( column.def , i++ );
		}
		fireTableStructureChanged( );
	}

	private List<ShotModelColumn> createColumns( )
	{
		if( shotList == null )
		{
			return Collections.emptyList( );
		}

		List<ShotModelColumn> result = new ArrayList<>( );

		result.add( fromStationNameColumn );
		result.add( toStationNameColumn );
		result.add( vectorColumn );
		result.add( xSectionAtFromColumn );
		result.add( xSectionAtToColumn );
		result.add( lengthUnitColumn );
		result.add( angleUnitColumn );

		int custCols = 0;
		for( SurveyDataColumnDef def : shotList.getCustomColumnDefs( ) )
		{
			ShotModelColumn col = createCustomColumn( custCols++ , def );
			if( col != null )
			{
				result.add( col );
			}
		}

		return result;
	}

	private ShotModelColumn createCustomColumn( int index , SurveyDataColumnDef def )
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
		}

		return null;
	}

	@Override
	public int getRowCount( )
	{
		return shotList != null ? shotList.size( ) + 1 : 0;
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

	private class ShotListListener implements TableModelList.Listener<Shot>
	{
		@Override
		public void elementsInserted( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.INSERT ) );
		}

		@Override
		public void elementsDeleted( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.DELETE ) );
		}

		@Override
		public void elementsUpdated( TableModelList<Shot> list , int fromIndex , int toIndex )
		{
			fireTableChanged( new TableModelEvent( ShotTableModel.this , fromIndex , toIndex ,
				TableModelEvent.ALL_COLUMNS , TableModelEvent.UPDATE ) );
		}

		@Override
		public void dataChanged( TableModelList<Shot> list )
		{
			fireTableDataChanged( );
		}

		@Override
		public void structureChanged( TableModelList<Shot> list )
		{
			setColumns( createColumns( ) );
		}
	}

	public abstract class ShotModelColumn implements Column<Integer>
	{
		public final SurveyDataColumnDef	def;

		public ShotModelColumn( SurveyDataColumnDef def )
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

	private class DefaultColumn extends ShotModelColumn
	{
		/**
		 * @param def
		 *            the definition for this column.
		 * @param valueGetter
		 *            given a {@link Shot}, gets the value for its property represented by this column.
		 * @param valueSetter
		 *            given a {@link Shot} and a new value, sets the shot's property represented by this
		 *            column to the new value.
		 */
		public DefaultColumn( SurveyDataColumnDef def ,
			Function<Shot, ?> valueGetter ,
			BiConsumer<Shot, Object> valueSetter )
		{
			super( def );
			this.valueGetter = valueGetter;
			this.valueSetter = valueSetter;
		}

		Function<Shot, ?>			valueGetter;
		BiConsumer<Shot, Object>	valueSetter;

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
			return valueGetter.apply( row == shotList.size( ) ? shotList.getPrototypeShot( ) : shotList.get( row ) );
		}

		@Override
		public boolean setValueAt( Object aValue , Integer row )
		{
			if( row == shotList.size( ) )
			{
				// the last row is always an empty placeholder view of the prototype shot, which is not actually part of 
				// the list.

				if( updatePrototypeShot( aValue ) )
				{
					// If the user just changed the vector type, but didn't enter any text, we can just update the
					// prototype shot, because we want the new type to remain showing in the last row.

					Object oldValue = valueGetter.apply( shotList.getPrototypeShot( ) );
					valueSetter.accept( shotList.getPrototypeShot( ) , aValue );
					return !Objects.equals( oldValue , aValue );
				}

				// In this case, the user changed the text, so we add a clone of the prototype shot to the end of the list
				// and this clone will get updated below.
				shotList.add( shotList.getPrototypeShot( ).clone( Cloners::defaultClone ) );
			}

			Object oldValue = valueGetter.apply( shotList.get( row ) );
			valueSetter.accept( shotList.get( row ) , aValue );

			if( row == shotList.size( ) - 1 )
			{
				shotList.trimEmptyRowsAtEnd( );
			}

			return !Objects.equals( oldValue , aValue );
		}

		protected boolean updatePrototypeShot( Object aValue )
		{
			return ( aValue instanceof ParsedText<?> && ( ( ParsedText<?> ) aValue ).isEmpty( ) );
		}
	}

	private class UnitColumn extends DefaultColumn
	{

		public UnitColumn( SurveyDataColumnDef def , Function<Shot, ?> valueGetter , BiConsumer<Shot, Object> valueSetter )
		{
			super( def , valueGetter , valueSetter );
		}

		@Override
		protected boolean updatePrototypeShot( Object aValue )
		{
			return true;
		}
	}
}
