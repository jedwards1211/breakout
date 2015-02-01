package org.breakout.wallsimport;

@SuppressWarnings( "serial" )
public class WallsImportMessageException extends RuntimeException
{
	private final WallsImportMessage	importMessage;

	public WallsImportMessageException( WallsImportMessage importMessage )
	{
		super( );
		this.importMessage = importMessage;
	}

	public WallsImportMessage getImportMessage( )
	{
		return importMessage;
	}

	public String getMessage( )
	{
		return importMessage.toString( );
	}
}
