package org.andork.breakout;

public enum FilterType
{
	ALPHA_DESIGNATION( "Alphabetic Designation" ) ,
	REGEXP( "Regular Expression" ) ,
	SURVEYORS( "Surveyors" ) ,
	DESCRIPTION( "Description" );
	
	private String	displayText;
	
	private FilterType( String displayText )
	{
		this.displayText = displayText;
	}
	
	public String toString( )
	{
		return displayText;
	}
}