package org.andork.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SegmentMatcher
{
	private final Segment	segment;
	private final Matcher	matcher;

	public SegmentMatcher( Segment segment , Pattern pattern )
	{
		this.segment = segment;
		this.matcher = pattern.matcher( segment.toString( ) );
	}

	public Pattern pattern( )
	{
		return matcher.pattern( );
	}

	public SegmentMatcher usePattern( Pattern newPattern )
	{
		matcher.usePattern( newPattern );
		return this;
	}

	public SegmentMatcher reset( )
	{
		matcher.reset( );
		return this;
	}

	public int start( )
	{
		return matcher.start( );
	}

	public int start( int group )
	{
		return matcher.start( group );
	}

	public int end( )
	{
		return matcher.end( );
	}

	public int end( int group )
	{
		return matcher.end( group );
	}

	public int end( String name )
	{
		return matcher.end( name );
	}

	public Segment group( )
	{
		return segment.substring( matcher.start( ) , matcher.end( ) );
	}

	public Segment group( int group )
	{
		int start = matcher.start( group );
		int end = matcher.end( group );

		if( start < 0 || end < 0 )
		{
			return null;
		}

		return segment.substring( start , end );
	}

	public int groupCount( )
	{
		return matcher.groupCount( );
	}

	public boolean matches( )
	{
		return matcher.matches( );
	}

	public boolean find( )
	{
		return matcher.find( );
	}

	public boolean find( int start )
	{
		return matcher.find( start );
	}

	public boolean lookingAt( )
	{
		return matcher.lookingAt( );
	}

	public SegmentMatcher region( int start , int end )
	{
		matcher.region( start , end );
		return this;
	}

	public int regionStart( )
	{
		return matcher.regionStart( );
	}

	public int regionEnd( )
	{
		return matcher.regionEnd( );
	}

	public boolean hasTransparentBounds( )
	{
		return matcher.hasTransparentBounds( );
	}

	public SegmentMatcher useTransparentBounds( boolean b )
	{
		matcher.useTransparentBounds( b );
		return this;
	}

	public boolean hasAnchoringBounds( )
	{
		return matcher.hasAnchoringBounds( );
	}

	public SegmentMatcher useAnchoringBounds( boolean b )
	{
		matcher.useAnchoringBounds( b );
		return this;
	}

	public boolean hitEnd( )
	{
		return matcher.hitEnd( );
	}

	public boolean requireEnd( )
	{
		return matcher.requireEnd( );
	}

}
