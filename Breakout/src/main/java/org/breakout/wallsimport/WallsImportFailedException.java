package org.breakout.wallsimport;

@SuppressWarnings( "serial" )
public class WallsImportFailedException extends RuntimeException
{
	public WallsImportFailedException( )
	{
	}

	public WallsImportFailedException( String message )
	{
		super( message );
	}

	public WallsImportFailedException( Throwable cause )
	{
		super( cause );
	}

	public WallsImportFailedException( String message , Throwable cause )
	{
		super( message , cause );
	}

	public WallsImportFailedException( String message , Throwable cause , boolean enableSuppression ,
		boolean writableStackTrace )
	{
		super( message , cause , enableSuppression , writableStackTrace );
	}
}
