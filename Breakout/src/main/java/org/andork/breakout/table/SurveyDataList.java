package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.andork.collect.CollectionUtils;
import org.andork.swing.table.TableModelList;

/**
 * A list of {@link SurveyDataRow}s that backs a {@link SurveyDataTableModel}. It also contains the
 * {@linkplain #setCustomColumnDefs(List)
 * definitions} of custom columns for which data is present in the dataRows, and a {@linkplain #getPrototypeDataRow()
 * "prototype" dataRow} that backs the empty last row of the table, so that it is always ready for more input.<br>
 * <br>
 * Whenever you
 * add {@link SurveyDataRow}s to the list, an {@link IllegalArgumentException} will be thrown if its
 * {@linkplain SurveyDataRow#getCustom()
 * custom field array}'s length does not equal the number of custom columns in this {@code SurveyDataList}.
 * 
 * 
 * @author James
 */
public class SurveyDataList<R extends SurveyDataRow> extends TableModelList<R>
{
	private List<SurveyDataColumnDef>	customColumnDefs	= Collections.emptyList( );
	private R					prototypeDataRow;

	private void requireProperNumCustomFields( R dataRow )
	{
		if( dataRow.getCustom( ).length != customColumnDefs.size( ) )
		{
			throw new IllegalArgumentException( "dataRow does not have the proper number of custom fields: " + dataRow );
		}
	}

	@Override
	public void set( int index , R element )
	{
		requireProperNumCustomFields( element );
		super.set( index , element );
	}

	/**
	 * Adds a {@link SurveyDataRow} to the list.
	 * 
	 * @param element
	 *            the dataRow to add.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code element}'s {@linkplain SurveyDataRow#getCustom() custom field array}'s length does not
	 *             equal the
	 *             number of custom columns in this {@code SurveyDataList}.
	 */
	@Override
	public void add( R element )
	{
		requireProperNumCustomFields( element );
		super.add( element );
	}

	/**
	 * Inserts a {@link SurveyDataRow} into the list before the element at the given index.
	 * 
	 * @param index
	 *            the index to insert at.
	 * @param element
	 *            the dataRow to add.
	 * 
	 * @throws IllegalArgumentException
	 *             if {@code element}'s {@linkplain SurveyDataRow#getCustom() custom field array}'s length does not
	 *             equal the
	 *             number of custom columns in this {@code SurveyDataList}.
	 */
	@Override
	public void add( int index , R element )
	{
		requireProperNumCustomFields( element );
		super.add( index , element );
	}

	/**
	 * Adds zero or more {@link SurveyDataRow}s to the list.
	 * 
	 * @param elements
	 *            the elements to add.
	 * 
	 * @throws IllegalArgumentException
	 *             if any added element's {@linkplain SurveyDataRow#getCustom() custom field array}'s length does not
	 *             equal
	 *             the
	 *             number of custom columns in this {@code SurveyDataList}.
	 */
	@Override
	public void addAll( Collection<? extends R> elements )
	{
		for( R element : elements )
		{
			requireProperNumCustomFields( element );
		}
		super.addAll( elements );
	}

	/**
	 * Inserts zero or more {@link SurveyDataRow}s into the list before the element at the given index.
	 * 
	 * @param index
	 *            the index to insert at.
	 * @param elements
	 *            the elements to add.
	 * 
	 * @throws IllegalArgumentException
	 *             if any added element's {@linkplain SurveyDataRow#getCustom() custom field array}'s length does not
	 *             equal
	 *             the
	 *             number of custom columns in this {@code SurveyDataList}.
	 */
	@Override
	public void addAll( int index , Collection<? extends R> elements )
	{
		for( R element : elements )
		{
			requireProperNumCustomFields( element );
		}
		super.addAll( index , elements );
	}

	/**
	 * Sets the "prototype" {@link SurveyDataRow} that backs the empty last row of {@link SurveyDataTableModel}.
	 * If the user changes the type of the vector, cross section, or other fields in the last row, they will be stored
	 * in this dataRow.
	 */
	public void setPrototypeDataRow( R newPrototypeDataRow )
	{
		this.prototypeDataRow = newPrototypeDataRow;
	}

	/**
	 * @return the "prototype" {@link SurveyDataRow} that backs the empty last row of {@link SurveyDataTableModel}.
	 *         If the user
	 *         changes
	 *         the
	 *         type of the vector, cross section, or other fields in the last row, they will be stored in this dataRow.
	 */
	public R getPrototypeDataRow( )
	{
		return prototypeDataRow;
	}

	/**
	 * @return the custom column definitions, in an unmodifiable list. The {@linkplain SurveyDataRow#getCustom() custom
	 *         value
	 *         array} of each dataRow should correspond to these column definitions.
	 */
	public List<SurveyDataColumnDef> getCustomColumnDefs( )
	{
		return customColumnDefs;
	}

	/**
	 * @return the number of custom columns, i.e. {@link #getCustomColumnDefs()}{@code .size()}.
	 */
	public int getNumCustomColumns( )
	{
		return customColumnDefs.size( );
	}

	/**
	 * Sets what custom columns are available, resizes the {@linkplain SurveyDataRow#getCustom() custom value array} of
	 * each {@link SurveyDataRow} in this {@code SurveyDataList} (and rearranges the elements) to correspond to the new
	 * custom
	 * columns.<br>
	 * If one of the new column defs has the same name as an old one but a different type, the old values for the column
	 * in each {@link SurveyDataRow} will be deleted.
	 * 
	 * @param newCustomColumnDefs
	 */
	public void setCustomColumnDefs( List<SurveyDataColumnDef> newCustomColumnDefs )
	{
		newCustomColumnDefs = Collections.unmodifiableList( new ArrayList<>( newCustomColumnDefs ) );

		List<SurveyDataColumnDef> oldCustomColumnDefs = customColumnDefs;

		// We may need to resize each D.custom array to correspond to the new columns.
		// For each new column that matches the name and type of an old column, we need to copy the value from
		// the old array (from the old column's position) to the new array (in the new column's position).

		// the index into copyMap will represent the index of the new column, and the value at that index 
		// will represent the index of the corresponding old column, or -1 if there is no corresponding old column.

		int[ ] copyMap = new int[ newCustomColumnDefs.size( ) ];
		Arrays.fill( copyMap , -1 );

		boolean customValuesChanged = newCustomColumnDefs.size( ) != oldCustomColumnDefs.size( );

		ListIterator<SurveyDataColumnDef> i = newCustomColumnDefs.listIterator( );
		while( i.hasNext( ) )
		{
			int index = i.nextIndex( );
			SurveyDataColumnDef newCustomColumnDef = i.next( );
			copyMap[ index ] = CollectionUtils.indexOf( oldCustomColumnDefs , d -> newCustomColumnDef.equals( d ) );
			customValuesChanged |= copyMap[ index ] != index;
		}

		// Now if necessary, resize each D.custom array and copy the values from the old array to the new array,
		// according to the mapping in copyMap.

		if( customValuesChanged )
		{
			for( R dataRow : this )
			{
				remapCustom( dataRow , copyMap );
			}
		}

		remapCustom( prototypeDataRow , copyMap );

		customColumnDefs = newCustomColumnDefs;

		fireStructureChanged( );
	}

	private void remapCustom( R dataRow , int[ ] copyMap )
	{
		Object[ ] newCustom = new Object[ copyMap.length ];
		if( dataRow.getCustom( ) != null )
		{
			for( int k = 0 ; k < copyMap.length ; k++ )
			{
				if( copyMap[ k ] >= 0 )
				{
					newCustom[ k ] = dataRow.getCustom( )[ copyMap[ k ] ];
				}
			}
		}
		dataRow.setCustom( newCustom );
	}

	/**
	 * Removes all "empty" rows (that have no text or values for any fields) after the last non-empty row.
	 */
	public void trimEmptyRowsAtEnd( )
	{
		int lastNonEmptyRow;

		for( lastNonEmptyRow = size( ) - 1 ; lastNonEmptyRow >= 0 ; lastNonEmptyRow-- )
		{
			if( !get( lastNonEmptyRow ).isEmpty( ) )
			{
				break;
			}
		}

		if( lastNonEmptyRow < size( ) )
		{
			removeSublist( lastNonEmptyRow + 1 , size( ) );
		}
	}
}