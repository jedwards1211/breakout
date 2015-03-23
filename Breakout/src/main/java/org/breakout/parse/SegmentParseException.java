package org.breakout.parse;

import org.andork.i18n.I18n;

@SuppressWarnings( "serial" )
public class SegmentParseException extends RuntimeException
{
	public final Segment segment;
	public final ParseErrorMessage message;

	public SegmentParseException( Segment segment , ParseErrorMessage message , Throwable cause )
	{
		super( cause );
		this.segment = segment;
		this.message = message;
	}

	public SegmentParseException( Segment segment , ParseErrorMessage message )
	{
		super( );
		this.segment = segment;
		this.message = message;
	}

	public String getLocalizedMessage( )
	{
		return I18n.forClass( SegmentParseException.class ).getFormattedString( "message" ,
			message.getLocalizedMessage( ) , segment.source , segment.startLine + 1 , segment.startCol + 1 ,
			System.lineSeparator( ) , segment.underlineInContext( ) );
	}
}
