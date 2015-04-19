package org.breakout.wallsimport;

import org.andork.i18n.I18n;

public enum WallsExpectedTypes
{
	SEGMENT,
	PREFIX,
	MACRO_NAME,
	STATION_NAME,
	FLAG,
	DATE,
	NOTE,
	QUOTED_TEXT;

	@Override
	public String toString( )
	{
		return I18n.forClass( WallsExpectedTypes.class ).getString( name( ) );
	}
}
