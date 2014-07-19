package org.andork.breakout;

import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.q.QObject;

public class SurveyRegexFilter extends RowFilter<TableModel, Integer>
{
	Pattern	pattern;
	
	public SurveyRegexFilter( String designation )
	{
		pattern = Pattern.compile( designation );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entry )
	{
		SurveyTableModel model = ( SurveyTableModel ) entry.getModel( );
		QObject<Row> row = model.getRow( entry.getIdentifier( ) );
		
		return pattern.matcher( row.get( Row.from ) ).find( ) && pattern.matcher( row.get( Row.to ) ).find( );
	}
}
