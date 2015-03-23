package org.breakout.wallsimport;

import org.andork.i18n.I18n;
import org.andork.parse.ParseErrorMessage;
import org.andork.parse.Segment;

public class ArgRequiredErrorMessage implements ParseErrorMessage
{
	private final Segment optionName;

	public ArgRequiredErrorMessage( Segment optionName )
	{
		super( );
		this.optionName = optionName;
	}

	@Override
	public String getLocalizedMessage( )
	{
		return I18n.forClass( ArgRequiredErrorMessage.class ).getFormattedString( "message" , optionName );
	}
}
