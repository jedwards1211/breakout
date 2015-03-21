package org.breakout.parse;

@SuppressWarnings( "serial" )
public class SegmentParseException extends RuntimeException
{
	public final Segment	segment;

	public SegmentParseException( Segment segment )
	{
		super( );
		this.segment = segment;
	}

	public SegmentParseException( Segment segment , String message , Throwable cause , boolean enableSuppression ,
		boolean writableStackTrace )
	{
		super( message , cause , enableSuppression , writableStackTrace );
		this.segment = segment;
	}

	public SegmentParseException( Segment segment , String message , Throwable cause )
	{
		super( message , cause );
		this.segment = segment;
	}

	public SegmentParseException( Segment segment , String message )
	{
		super( message );
		this.segment = segment;
	}

	public SegmentParseException( Segment segment , Throwable cause )
	{
		super( cause );
		this.segment = segment;
	}
}
