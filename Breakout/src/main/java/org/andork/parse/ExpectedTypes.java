package org.andork.parse;

import org.andork.i18n.I18n;

public enum ExpectedTypes
{
	INTEGER,
	UNSIGNED_INTEGER,
	FLOAT,
	UNSIGNED_FLOAT,
	DOUBLE,
	UNSIGNED_DOUBLE,
	WHITESPACE,
	END_OF_LINE,
	NON_WHITESPACE;

	@Override
	public String toString( )
	{
		return I18n.forClass( ExpectedTypes.class ).getString( name( ) );
	}
}
