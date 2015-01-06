package org.andork.breakout.table2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.andork.swing.table.TableModelList;

public class ShotList extends TableModelList<Shot>
{
	private final List<ShotColumnDef>	columnDefs				= new ArrayList<ShotColumnDef>( );
	private final List<ShotColumnDef>	columnDefsUnmodifiable	= Collections.unmodifiableList( columnDefs );

	/**
	 * The indices of all custom columns in {@link #columnDefs}. This list should correspond exactly to all
	 * {@link Shot#custom} in this {@code ShotList}.
	 */
	private final List<Integer>			customColumnDefIndices	= new ArrayList<Integer>( );

	public List<ShotColumnDef> getColumnDefs( )
	{
		return columnDefsUnmodifiable;
	}

	public void setColumnDefs( List<ShotColumnDef> newColumnDefs )
	{
		List<ShotColumnDef> oldColumnDefs = columnDefs;
		List<Integer> oldCustomColumnDefIndices = customColumnDefIndices;

		List<Integer> newCustomColumnDefIndices = new ArrayList<Integer>( );

		ListIterator<ShotColumnDef> defIter = newColumnDefs.listIterator( );
		while( defIter.hasNext( ) )
		{
			int index = defIter.nextIndex( );
			ShotColumnDef def = defIter.next( );
			if( def.type != ShotColumnType.BUILTIN )
			{
				newCustomColumnDefIndices.add( index );
			}
		}

		Map<String, Integer> oldValueIndices = new HashMap<>( );

		int k = 0;
		for( int oldDefIndex : oldCustomColumnDefIndices )
		{
			oldValueIndices.put( oldColumnDefs.get( oldDefIndex ).name , k++ );
		}

		int[ ] copyMap = new int[ newCustomColumnDefIndices.size( ) ];
		Arrays.fill( copyMap , -1 );

		boolean customValuesChanged = oldCustomColumnDefIndices.size( ) != newCustomColumnDefIndices.size( );

		k = 0;
		for( int newDefIndex : newCustomColumnDefIndices )
		{
			ShotColumnDef newDef = newColumnDefs.get( newDefIndex );

			Integer oldValueIndex = oldValueIndices.get( newDef.name );
			Integer oldDefIndex = oldValueIndex == null ? null : oldCustomColumnDefIndices.get( oldValueIndex );
			ShotColumnDef oldDef = oldDefIndex == null ? null : oldColumnDefs.get( oldDefIndex );

			if( oldDef != null && newDef.type == oldDef.type )
			{
				copyMap[ k ] = oldValueIndex;
			}
			customValuesChanged |= copyMap[ k ] != k;

			k++;
		}

		if( customValuesChanged )
		{
			for( Shot shot : this )
			{
				Object[ ] newCustom = new Object[ copyMap.length ];
				for( k = 0 ; k < copyMap.length ; k++ )
				{
					if( copyMap[ k ] >= 0 )
					{
						newCustom[ k ] = shot.custom[ copyMap[ k ] ];
					}
				}
				shot.custom = newCustom;
			}
		}

		columnDefs.clear( );
		columnDefs.addAll( newColumnDefs );

		customColumnDefIndices.clear( );
		customColumnDefIndices.addAll( newCustomColumnDefIndices );

		fireStructureChanged( );
	}
}