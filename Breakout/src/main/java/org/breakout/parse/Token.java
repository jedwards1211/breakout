package org.breakout.parse;

import org.andork.util.StringUtils;

public class Token
{
	public final String	image;
	public final int	beginLine;
	public final int	endLine;
	public final int	beginColumn;
	public final int	endColumn;

	public Token( int beginLine , int beginColumn , int endLine , int endColumn , String image )
	{
		super( );
		if( beginLine < 0 )
		{
			throw new IllegalArgumentException( "beginLine must be >= 0" );
		}
		if( beginColumn < 0 )
		{
			throw new IllegalArgumentException( "beginColumn must be >= 0" );
		}
		if( endLine < beginLine )
		{
			throw new IllegalArgumentException( "endLine must be >= beginLine" );
		}
		if( endColumn < beginColumn )
		{
			throw new IllegalArgumentException( "endColumn must be >= beginColumn" );
		}
		this.beginLine = beginLine;
		this.endLine = endLine;
		this.beginColumn = beginColumn;
		this.endColumn = endColumn;
		this.image = StringUtils.requireNonNullOrEmpty( image );
	}
}
