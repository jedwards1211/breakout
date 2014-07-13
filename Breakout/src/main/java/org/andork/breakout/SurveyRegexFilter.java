package org.andork.breakout;

import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.andork.breakout.model.Shot;
import org.andork.breakout.model.SurveyTableModel;

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
		Shot shot = ( ( SurveyTableModel ) entry.getModel( ) ).getShotAtRow( entry.getIdentifier( ) );
		if( shot == null )
		{
			return false;
		}
		
		return pattern.matcher( shot.from.name ).find( ) && pattern.matcher( shot.to.name ).find( );
	}
}
