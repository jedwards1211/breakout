package org.andork.breakout.wallsimport;

import org.andork.util.StringUtils;

public class WallsToken
{
	private final int		startColumn;
	private final int		endColumn;
	private final String	token;

	public WallsToken( int startColumn , int endColumn , String token )
	{
		super( );
		if( startColumn < 0 )
		{
			throw new IllegalArgumentException( "startColumn must be >= 0" );
		}
		if( endColumn < startColumn )
		{
			throw new IllegalArgumentException( "endColumn must be >= startColumn" );
		}
		this.startColumn = startColumn;
		this.endColumn = endColumn;
		this.token = StringUtils.requireNonNullOrEmpty( token );
	}

	public int startColumn( )
	{
		return startColumn;
	}

	public int endColumn( )
	{
		return endColumn;
	}

	public String token( )
	{
		return token;
	}
}
