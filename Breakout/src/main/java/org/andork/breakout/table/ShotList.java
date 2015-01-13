package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.andork.collect.CollectionUtils;
import org.andork.swing.table.TableModelList;

public class ShotList extends TableModelList<Shot>
{
	private List<ShotColumnDef>	customColumnDefs	= Collections.emptyList( );
	private final Shot			prototypeShot		= new Shot( );

	private void requireProperNumCustomFields( Shot shot )
	{
		if( shot.getCustom( ).length != customColumnDefs.size( ) )
		{
			throw new IllegalArgumentException( "shot does not have the proper number of custom fields: " + shot );
		}
	}

	@Override
	public void set( int index , Shot element )
	{
		requireProperNumCustomFields( element );
		super.set( index , element );
	}

	@Override
	public void add( Shot element )
	{
		requireProperNumCustomFields( element );
		super.add( element );
	}

	@Override
	public void add( int index , Shot element )
	{
		requireProperNumCustomFields( element );
		super.add( index , element );
	}

	@Override
	public void addAll( Collection<? extends Shot> elements )
	{
		for( Shot element : elements )
		{
			requireProperNumCustomFields( element );
		}
		super.addAll( elements );
	}

	@Override
	public void addAll( int index , Collection<? extends Shot> elements )
	{
		for( Shot element : elements )
		{
			requireProperNumCustomFields( element );
		}
		super.addAll( index , elements );
	}

	public Shot getPrototypeShot( )
	{
		return prototypeShot;
	}

	public List<ShotColumnDef> getCustomColumnDefs( )
	{
		return customColumnDefs;
	}

	public void setCustomColumnDefs( List<ShotColumnDef> newCustomColumnDefs )
	{
		newCustomColumnDefs = Collections.unmodifiableList( new ArrayList<>( newCustomColumnDefs ) );

		List<ShotColumnDef> oldCustomColumnDefs = customColumnDefs;

		// We may need to resize each Shot.custom array to correspond to the new columns.
		// For each new column that matches the name and type of an old column, we need to copy the value from
		// the old array (from the old column's position) to the new array (in the new column's position).

		// the index into copyMap will represent the index of the new column, and the value at that index 
		// will represent the index of the corresponding old column, or -1 if there is no corresponding old column.

		int[ ] copyMap = new int[ newCustomColumnDefs.size( ) ];
		Arrays.fill( copyMap , -1 );

		boolean customValuesChanged = newCustomColumnDefs.size( ) != oldCustomColumnDefs.size( );

		ListIterator<ShotColumnDef> i = newCustomColumnDefs.listIterator( );
		while( i.hasNext( ) )
		{
			int index = i.nextIndex( );
			ShotColumnDef newCustomColumnDef = i.next( );
			copyMap[ index ] = CollectionUtils.indexOf( oldCustomColumnDefs , d -> newCustomColumnDef.equals( d ) );
			customValuesChanged |= copyMap[ index ] != index;
		}

		// Now if necessary, resize each Shot.custom array and copy the values from the old array to the new array,
		// according to the mapping in copyMap.

		if( customValuesChanged )
		{
			for( Shot shot : this )
			{
				remapCustom( shot , copyMap );
			}
		}

		remapCustom( prototypeShot , copyMap );

		customColumnDefs = newCustomColumnDefs;

		fireStructureChanged( );
	}

	private void remapCustom( Shot shot , int[ ] copyMap )
	{
		Object[ ] newCustom = new Object[ copyMap.length ];
		if( shot.getCustom( ) != null )
		{
			for( int k = 0 ; k < copyMap.length ; k++ )
			{
				if( copyMap[ k ] >= 0 )
				{
					newCustom[ k ] = shot.getCustom( )[ copyMap[ k ] ];
				}
			}
		}
		shot.setCustom( newCustom );
	}

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

	public int getNumCustomColumns( )
	{
		return customColumnDefs.size( );
	}
}