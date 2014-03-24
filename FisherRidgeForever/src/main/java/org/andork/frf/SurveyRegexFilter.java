package org.andork.frf;

import java.util.regex.Pattern;

import javax.swing.RowFilter;

import org.andork.frf.model.SurveyShot;

public class SurveyRegexFilter extends RowFilter<SurveyTableModel, Integer>
{
	Pattern	pattern;
	
	public SurveyRegexFilter( String designation )
	{
		pattern = Pattern.compile( designation );
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends SurveyTableModel, ? extends Integer> entry )
	{
		SurveyShot shot = entry.getModel( ).getShotAtRow( entry.getIdentifier( ) );
		if( shot == null )
		{
			return false;
		}
		
		return pattern.matcher( shot.from.name ).find( ) && pattern.matcher( shot.to.name ).find( );
	}
}
