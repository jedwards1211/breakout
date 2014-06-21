package org.andork.breakout.model;

public enum ColorParam
{
	DEPTH( "Depth" ) , PASSAGE_WIDTH( "Passage Width" ) , PASSAGE_HEIGHT( "Passage Height" ) , PASSAGE_AREA( "Passage Area" );
	
	private final String	displayName;
	
	private ColorParam( String displayName )
	{
		this.displayName = displayName;
	}
	
	public String getDisplayName( )
	{
		return displayName;
	}
	
	public String toString( )
	{
		return displayName;
	}
}
