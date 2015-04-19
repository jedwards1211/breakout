package org.breakout.wallsimport;

import org.andork.i18n.I18n;
import org.andork.parse.ParseErrorMessage;

public enum WallsParseError implements ParseErrorMessage
{
	AZM_OUT_OF_RANGE,
	SIGNED_ZERO_INC,
	UNSIGNED_NONZERO_INC,
	INC_OUT_OF_RANGE,
	STACK_FULL,
	STACK_EMPTY,
	INVALID_DATE,
	LATITUDE_OUT_OF_RANGE,
	LONGITUDE_OUT_OF_RANGE,
	MACRO_NOT_DEFINED;

	@Override
	public String getLocalizedMessage( )
	{
		return I18n.forClass( WallsParseError.class ).getString( name( ) );
	}
}
