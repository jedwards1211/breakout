package org.breakout.parse;

import org.andork.i18n.I18n;

@SuppressWarnings( "serial" )
public class SurveyParseException extends RuntimeException
{
	public final Integer	beginColumn;
	public final Integer	endColumn;
	private String			messageKey;
	private Object[ ]		messageArgs;

	public SurveyParseException( Integer beginColumn , Integer endColumn , String messageKey , Object ... messageArgs )
	{
		super( );
		this.beginColumn = beginColumn;
		this.endColumn = endColumn;
		this.messageKey = messageKey;
		this.messageArgs = messageArgs;
	}

	public SurveyParseException( String messageKey , Object ... messageArgs )
	{
		this( null , null , messageKey , messageArgs );
	}

	public String getLocalizedMessage( I18n i18n )
	{
		return i18n.forClass( SurveyParseException.class ).getFormattedString( messageKey , messageArgs );
	}

	public String getMessage( )
	{
		return messageKey;
	}
}
