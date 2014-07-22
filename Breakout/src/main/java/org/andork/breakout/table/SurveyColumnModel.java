package org.andork.breakout.table;

import org.andork.breakout.table.NewSurveyTableModel.SurveyColumnType;
import org.andork.q.QSpec;
import org.andork.swing.table.FormatAndDisplayInfo;

public class SurveyColumnModel extends QSpec<SurveyColumnModel>
{
	public static final Attribute<Boolean>					fixed			= newAttribute( Boolean.class , "fixed" );
	public static final Attribute<Boolean>					visible			= newAttribute( Boolean.class , "show" );
	public static final Attribute<String>					name			= newAttribute( String.class , "name" );
	public static final Attribute<SurveyColumnType>			type			= newAttribute( SurveyColumnType.class , "type" );
	public static final Attribute<FormatAndDisplayInfo<?>>	defaultFormat	= newAttribute( FormatAndDisplayInfo.class , "defaultFormat" );
	
	public static final SurveyColumnModel					instance		= new SurveyColumnModel( );
	
	private SurveyColumnModel( )
	{
		
	}
}