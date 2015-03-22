package org.breakout.wallsimport;

import java.util.Map;

import org.breakout.parse.Segment;
import org.breakout.parse.SegmentParseExpectedException;
import org.junit.Assert;
import org.junit.Test;

public class UnitsOptionsParsingTests
{
	@Test
	public void test001( )
	{
		Segment segment = new Segment( "hello=world flag=\"quoted text\"" , null , 7 , 3 );

		Map<Segment, Segment> map = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , map.get( new Segment( "hello" , null , 0 , 0 ) ) );
		Assert.assertEquals( new Segment( "quoted text" , null , 0 , 0 ) ,
			map.get( new Segment( "flag" , null , 0 , 0 ) ) );
	}

	@Test
	public void testQuotedQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );

		Map<Segment, Segment> map = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , map.get( new Segment( "hello" , null , 0 , 0 ) ) );
		Assert.assertEquals( new Segment( "\\\"quoted\\\" text" , null , 0 , 0 ) ,
			map.get( new Segment( "flag" , null , 0 , 0 ) ) );

		Assert.assertEquals( "\"quoted\" text" ,
			NewWallsParser.unescape( map.get( new Segment( "flag" , null , 0 , 0 ) ) ) );
	}

	@Test
	public void testSemicolon( )
	{
		Segment segment = new Segment( "hello=world ;flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );

		Map<Segment, Segment> map = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , map.get( new Segment( "hello" , null , 0 , 0 ) ) );
		Assert.assertEquals( null , map.get( new Segment( "flag" , null , 0 , 0 ) ) );
	}

	@Test
	public void testSemicolonInQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text\" then=" , null , 7 , 3 );

		Map<Segment, Segment> map = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , map.get( new Segment( "hello" , null , 0 , 0 ) ) );
		Assert.assertEquals( new Segment( "\\\"quoted\\\" ;text" , null , 0 , 0 ) ,
			map.get( new Segment( "flag" , null , 0 , 0 ) ) );
		Assert.assertEquals( null , map.get( new Segment( "then" , null , 0 , 0 ) ) );
		Assert.assertTrue( map.containsKey( new Segment( "then" , null , 0 , 0 ) ) );
	}

	@Test
	public void testUnclosedQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text then=\\\"" , null , 7 , 3 );

		try
		{
			Map<Segment, Segment> map = NewWallsParser.parseUnitsOptions( segment );
		}
		catch( SegmentParseExpectedException ex )
		{
			Assert.assertEquals( ex.segment.startCol , segment.substring( segment.length( ) ).startCol );
			return;
		}

		Assert.fail( "expected SegmentParseExpectedException" );
	}
}
