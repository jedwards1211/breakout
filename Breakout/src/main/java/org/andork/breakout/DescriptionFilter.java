package org.andork.breakout;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.q.QObject;

public class DescriptionFilter extends RowFilter<TableModel, Integer>
{
	String[ ]	descriptions;
	
	public DescriptionFilter( String descriptions )
	{
		this.descriptions = descriptions.toLowerCase( ).split( "\\s+" );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry )
	{
		if( descriptions.length == 0 )
		{
			return true;
		}
		
		QObject<Row> row = ( ( SurveyTableModel ) entry.getModel( ) ).getRow( entry.getIdentifier( ) );
		if( row == null || row.get( Row.desc ) == null )
		{
			return false;
		}
		String desc = row.get( Row.desc ).toLowerCase( );
		for( String description : descriptions )
		{
			if( !desc.contains( description ) )
			{
				return false;
			}
		}
		return true;
	}
}
