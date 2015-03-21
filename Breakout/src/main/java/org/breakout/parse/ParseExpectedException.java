package org.breakout.parse;

@SuppressWarnings( "serial" )
public class ParseExpectedException extends RuntimeException
{
	public final Object[ ]	expectedItems;
	public final Object		source;
	public final int		startLine;
	public final int		startCol;
	public final int		endLine;
	public final int		endCol;

	public ParseExpectedException( Object source , int startLine , int startCol , int endLine ,
		int endCol , Object ... expectedItems )
	{
		super( );
		this.source = source;
		this.startLine = startLine;
		this.startCol = startCol;
		this.endLine = endLine;
		this.endCol = endCol;
		this.expectedItems = expectedItems;
	}

	public ParseExpectedException( Segment segment , Object ... expectedItems )
	{
		this( segment.source , segment.startLine , segment.startCol , segment.endLine , segment.endCol , expectedItems );
	}
}
