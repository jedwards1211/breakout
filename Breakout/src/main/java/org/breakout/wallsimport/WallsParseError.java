package org.breakout.wallsimport;

import org.andork.i18n.I18n;
import org.breakout.parse.ParseErrorMessage;

public enum WallsParseError implements ParseErrorMessage
{
	TOO_MANY_COLONS,
	AZM_OUT_OF_RANGE,
	SIGNED_ZERO_INC,
	UNSIGNED_NONZERO_INC,
	INC_OUT_OF_RANGE,
	TOO_MANY_ARGS,
	ARGS_NOT_ALLOWED,
	ARGS_REQUIRED,
	STACK_FULL,
	STACK_EMPTY,
	INVALID_ORDER_ELEMENTS,
	INVALID_LRUD_ELEMENTS;

	@Override
	public String getLocalizedMessage( )
	{
		return I18n.forClass( WallsParseError.class ).getString( name( ) );
	}
}
