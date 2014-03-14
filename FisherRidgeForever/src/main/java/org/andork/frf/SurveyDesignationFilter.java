package org.andork.frf;

import javax.swing.RowFilter;

public class SurveyDesignationFilter extends RowFilter<SurveyTableModel, Integer>
{
	String	designation;
	
	public SurveyDesignationFilter( String designation )
	{
		this.designation = designation;
	}
	
	@Override
	public boolean include( javax.swing.RowFilter.Entry<? extends SurveyTableModel, ? extends Integer> entry )
	{
		String from = entry.getStringValue( SurveyTable.FROM_COLUMN );
		String to = entry.getStringValue( SurveyTable.TO_COLUMN );
		
		return from.startsWith( designation ) && from.length( ) > designation.length( ) && Character.isDigit( from.charAt( designation.length( ) ) )
				&& to.startsWith( designation ) && to.length( ) > designation.length( ) && Character.isDigit( to.charAt( designation.length( ) ) );
	}
}
