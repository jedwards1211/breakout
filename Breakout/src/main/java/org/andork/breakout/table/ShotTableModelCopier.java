package org.andork.breakout.table;

import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;

public class ShotTableModelCopier extends AbstractTableModelCopier<ShotTableModel>
{
	@Override
	public void copyRow( ShotTableModel src , int row , ShotTableModel dest )
	{
		// ignore the last row, as it is always blank and not backed by the ShotList
		if( row == src.getRowCount( ) - 1 )
		{
			return;
		}
		super.copyRow( src , row , dest );
	}

	@Override
	public ShotTableModel createEmptyCopy( ShotTableModel model )
	{
		ShotTableModel result = new ShotTableModel( );
		if( model.getShotList( ) != null )
		{
			ShotList list = new ShotList( );
			int numCustomColumns = model.getShotList( ).getCustomColumnDefs( ).size( );
			list.setCustomColumnDefs( model.getShotList( ).getCustomColumnDefs( ) );
			for( int i = 0 ; i < model.getShotList( ).size( ) ; i++ )
			{
				Shot shot = new Shot( );
				shot.setCustom( new Object[ numCustomColumns ] );
				list.add( shot );
			}
			result.setShotList( list );
		}
		return result;
	}
}
