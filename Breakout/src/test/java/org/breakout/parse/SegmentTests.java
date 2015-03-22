package org.breakout.parse;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

public class SegmentTests
{
	@Test
	public void constructionEndLocTests( )
	{
		Segment segment;

		segment = new Segment( "test\r\none" , null , 5 , 3 );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\r\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n \r\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n \rone" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\none" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "test\r\n\none\n" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 3 , segment.endCol );

		segment = new Segment( "test\r\n\none\r\n" , null , 5 , 3 );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 4 , segment.endCol );

		segment = new Segment( "test\r\n\none\r\n\n\r" , null , 5 , 3 );
		Assert.assertEquals( 9 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );
	}

	@Test
	public void substringLocTests( )
	{
		Segment segment;

		segment = new Segment( "hello world" , null , 5 , 3 ).substring( 0 , 0 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 3 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 2 , segment.endCol );

		segment = new Segment( "hello world" , null , 5 , 3 ).substring( 11 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 14 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 13 , segment.endCol );

		segment = new Segment( "hello world" , null , 5 , 3 ).substring( 2 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 10 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 2 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 2 , 6 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 5 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 8 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 5 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 7 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 6 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 8 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 7 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 9 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 6 , 6 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 9 , segment.startCol );
		Assert.assertEquals( 5 , segment.endLine );
		Assert.assertEquals( 8 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 7 , 7 );
		Assert.assertEquals( 6 , segment.startLine );
		Assert.assertEquals( 0 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( -1 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 6 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 9 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\nworld" , null , 5 , 3 ).substring( 7 , 9 );
		Assert.assertEquals( 6 , segment.startLine );
		Assert.assertEquals( 0 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 1 , segment.endCol );

		segment = new Segment( "hello\r\n\r\nworld" , null , 5 , 3 ).substring( 5 , 10 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 8 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 6 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 9 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 7 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );

		segment = new Segment( "hello\r\n\n\rworld" , null , 5 , 3 ).substring( 5 , 10 );
		Assert.assertEquals( 5 , segment.startLine );
		Assert.assertEquals( 8 , segment.startCol );
		Assert.assertEquals( 8 , segment.endLine );
		Assert.assertEquals( 0 , segment.endCol );
	}

	@Test
	public void parseAsNonNegativeIntegerTest( )
	{
		Segment segment = new Segment( "1234" , "file" , 5 , 3 );
		Assert.assertEquals( 1234 , segment.parseAsUnsignedInteger( ) );

		segment = new Segment( "-1234" , "file" , 5 , 3 );
		try
		{
			segment.parseAsUnsignedInteger( );
			Assert.fail( "expected to throw" );
		}
		catch( SegmentParseExpectedException ex )
		{
			Assert.assertArrayEquals( new Object[ ] { ExpectedTypes.UNSIGNED_INTEGER } , ex.expectedItems );
			Assert.assertEquals( ex.segment.source , "file" );
			Assert.assertEquals( ex.segment.startLine , 5 );
			Assert.assertEquals( ex.segment.startCol , 3 );
			Assert.assertEquals( ex.segment.endLine , 5 );
			Assert.assertEquals( ex.segment.endCol , 7 );
		}
	}

	@Test
	public void parseAsAnyOfTest( )
	{
		Segment integer = new Segment( "1234" , "file" , 5 , 3 );
		Segment omit = new Segment( "--" , "file" , 5 , 3 );
		Segment invalid = new Segment( "abc" , "file" , 5 , 3 );

		Function<Segment, Integer> parseAsOmit = s ->
		{
			if( s.matches( "-+" ) )
			{
				return null;
			}
			throw new SegmentParseExpectedException( s , "--" );
		};
		Assert.assertEquals( ( Integer ) 1234 ,
			( Integer ) integer.parseAsAnyOf( Segment::parseAsInteger , parseAsOmit ) );
		Assert.assertEquals( ( Integer ) null ,
			( Integer ) omit.parseAsAnyOf( Segment::parseAsInteger , parseAsOmit ) );

		try
		{
			invalid.parseAsAnyOf( Segment::parseAsInteger , parseAsOmit );
		}
		catch( SegmentParseExpectedException ex )
		{
			Assert.assertArrayEquals( new Object[ ] { ExpectedTypes.INTEGER , "--" } , ex.expectedItems );
			Assert.assertEquals( ex.segment.source , "file" );
			Assert.assertEquals( ex.segment.startLine , 5 );
			Assert.assertEquals( ex.segment.startCol , 3 );
			Assert.assertEquals( ex.segment.endLine , 5 );
			Assert.assertEquals( ex.segment.endCol , 5 );
		}
	}
}
