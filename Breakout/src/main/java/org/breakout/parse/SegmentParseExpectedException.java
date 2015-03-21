package org.breakout.parse;

@SuppressWarnings( "serial" )
public class SegmentParseExpectedException extends SegmentParseException
{
	public final Object[ ]	expectedItems;

	public SegmentParseExpectedException( Segment segment , Object ... expectedItems )
	{
		super( segment );
		this.expectedItems = expectedItems;
	}
}
