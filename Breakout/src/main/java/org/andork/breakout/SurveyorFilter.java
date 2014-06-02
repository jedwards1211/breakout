package org.andork.breakout;

import javax.swing.RowFilter;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.snakeyaml.YamlObject;

public class SurveyorFilter extends RowFilter<SurveyTableModel, Integer>
{
	String[ ]	surveyors;
	
	public SurveyorFilter( String surveyors )
	{
		this.surveyors = surveyors.split( "\\s*,\\s*" );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends SurveyTableModel, ? extends Integer> entry )
	{
		if( surveyors.length == 0 )
		{
			return true;
		}
		
		YamlObject<Row> row = entry.getModel( ).getRow( entry.getIdentifier( ) );
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
