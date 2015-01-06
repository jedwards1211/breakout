package org.andork.breakout.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.andork.collect.CollectionUtils;
import org.andork.swing.table.TableModelList;

public class ShotList extends TableModelList<Shot>
{
	private List<ShotColumnDef>	columnDefs			= Collections.emptyList( );
	private List<ShotColumnDef>	builtinColumnDefs	= Collections.emptyList( );
	private List<ShotColumnDef>	customColumnDefs	= Collections.emptyList( );

	public List<ShotColumnDef> getColumnDefs( )
	{
		return columnDefs;
	}

	public List<ShotColumnDef> getBuiltinColumnDefs( )
	{
		return builtinColumnDefs;
	}

	public List<ShotColumnDef> getCustomColumnDefs( )
	{
		return customColumnDefs;
	}

	public void setColumnDefs( List<ShotColumnDef> newColumnDefs )
	{
		newColumnDefs = Collections.unmodifiableList( new ArrayList<>( newColumnDefs ) );
		int newFirstCustomIndex = CollectionUtils.indexOf( newColumnDefs , d -> d.type != ShotColumnType.BUILTIN );
		if( newFirstCustomIndex < 0 )
		{
			newFirstCustomIndex = newColumnDefs.size( );
		}
		List<ShotColumnDef> newBuiltinColumnDefs = newColumnDefs.subList( 0 , newFirstCustomIndex );
		List<ShotColumnDef> newCustomColumnDefs = newColumnDefs.subList( newFirstCustomIndex , newColumnDefs.size( ) );

		if( newCustomColumnDefs.stream( ).anyMatch( d -> d.type == ShotColumnType.BUILTIN ) )
		{
			throw new IllegalArgumentException( "all custom columns must be at the end of newColumnDefs" );
		}

		List<ShotColumnDef> oldCustomColumnDefs = customColumnDefs;

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

		if( customValuesChanged )
		{
			for( Shot shot : this )
			{
				Object[ ] newCustom = new Object[ copyMap.length ];
				for( int k = 0 ; k < copyMap.length ; k++ )
				{
					if( copyMap[ k ] >= 0 )
					{
						newCustom[ k ] = shot.custom[ copyMap[ k ] ];
					}
				}
				shot.custom = newCustom;
			}
		}

		columnDefs = newColumnDefs;
		builtinColumnDefs = newBuiltinColumnDefs;
		customColumnDefs = newCustomColumnDefs;

		fireStructureChanged( );
	}
}