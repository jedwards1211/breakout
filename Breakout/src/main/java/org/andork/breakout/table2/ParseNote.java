package org.andork.breakout.table2;

public class ParseNote
{
	public final String			messageKey;
	public final ParseStatus	status;

	public ParseNote( String messageKey , ParseStatus status )
	{
		super( );
		this.messageKey = messageKey;
		this.status = status;
	}
}