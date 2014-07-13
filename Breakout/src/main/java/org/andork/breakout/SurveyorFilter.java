package org.andork.breakout;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.q.QObject;

public class SurveyorFilter extends RowFilter<TableModel, Integer>
{
	String[ ]	surveyors;
	
	public SurveyorFilter( String surveyors )
	{
		this.surveyors = surveyors.split( "\\s*,\\s*" );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry )
	{
		if( surveyors.length == 0 )
		{
			return true;
		}
		
		QObject<Row> row = ( ( SurveyTableModel ) entry.getModel( ) ).getRow( entry.getIdentifier( ) );
		if( row == null || row.get( Row.surveyors ) == null )
		{
			return false;
		}
		String surveyorString = row.get( Row.surveyors ).toLowerCase( );
		for( String surveyor : surveyors )
		{
			if( surveyorString.contains( surveyor.toLowerCase( ) ) )
			{
				return true;
			}
		}
		return false;
	}
}
