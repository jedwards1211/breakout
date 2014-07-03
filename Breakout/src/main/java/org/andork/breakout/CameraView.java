package org.andork.breakout;

public enum CameraView
{
	PERSPECTIVE( "Perspective" ) ,
	PLAN( "Plan" ) ,
	NORTH_FACING_PROFILE( "North-Facing Profile" ) ,
	SOUTH_FACING_PROFILE( "South-Facing Profile" ) ,
	EAST_FACING_PROFILE( "East-Facing Profile" ) ,
	WEST_FACING_PROFILE( "West-Facing Profile" );
	
	private String	displayText;
	
	CameraView( String displayText )
	{
		this.displayText = displayText;
	}
	
	public String toString( )
	{
		return displayText;
	}
}