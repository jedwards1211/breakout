package org.andork.frf;

import javax.swing.RowFilter;

import org.andork.swing.table.AnnotatingJTable;

@SuppressWarnings( "serial" )
public class SurveyTable extends AnnotatingJTable<SurveyTableModel, RowFilter<SurveyTableModel, Integer>>
{
	public void createDefaultColumnsFromModel( )
	{
		super.createDefaultColumnsFromModel( );
	}
	
	public SurveyTable( )
	{
		super( new SurveyTableModel( ) );
	}
	
	public SurveyTableModel getModel( )
	{
		return ( SurveyTableModel ) super.getModel( );
	}
	

}
