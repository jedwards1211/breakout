package org.breakout.table;

import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;

public class StationTableModelCopier extends AbstractTableModelCopier<StationTableModel>
{
	@Override
	public void copyRow( StationTableModel src , int row , StationTableModel dest )
	{
		// ignore the last row, as it is always blank and not backed by the StationList
		if( row == src.getRowCount( ) - 1 )
		{
			return;
		}
		super.copyRow( src , row , dest );
	}

	@Override
	public StationTableModel createEmptyCopy( StationTableModel model )
	{
		StationTableModel result = new StationTableModel( );
		if( model.getSurveyDataList( ) != null )
		{
			SurveyDataList<Station> list = new SurveyDataList<>( new Station( ) );
			int numCustomColumns = model.getSurveyDataList( ).getCustomColumnDefs( ).size( );
			list.setCustomColumnDefs( model.getSurveyDataList( ).getCustomColumnDefs( ) );
			for( int i = 0 ; i < model.getSurveyDataList( ).size( ) ; i++ )
			{
				Station Station = new Station( );
				Station.setCustom( new Object[ numCustomColumns ] );
				list.add( Station );
			}
			result.setSurveyDataList( list );
		}
		return result;
	}
}
