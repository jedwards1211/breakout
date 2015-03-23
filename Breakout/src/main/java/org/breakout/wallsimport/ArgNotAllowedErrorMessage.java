package org.breakout.wallsimport;

import org.andork.i18n.I18n;
import org.andork.parse.ParseErrorMessage;
import org.andork.parse.Segment;

public class ArgNotAllowedErrorMessage implements ParseErrorMessage
{
	private final Segment optionName;

	public ArgNotAllowedErrorMessage( Segment optionName )
	{
		super( );
		this.optionName = optionName;
	}

	@Override
	public String getLocalizedMessage( )
	{
		return I18n.forClass( ArgNotAllowedErrorMessage.class ).getFormattedString( "message" , optionName );
	}
}
