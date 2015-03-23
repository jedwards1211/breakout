package org.andork.parse;

@SuppressWarnings( "serial" )
public class SegmentParseExpectedException extends SegmentParseException
{
	public final Object[ ] expectedItems;

	public SegmentParseExpectedException( Segment segment , Object ... expectedItems )
	{
		super( segment , new ParseExpectedMessage( expectedItems ) );
		this.expectedItems = expectedItems;
	}
}
