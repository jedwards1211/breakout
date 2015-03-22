package org.breakout.wallsimport;

import java.util.List;
import java.util.Map;

import org.andork.util.Pair;
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

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
		Assert.assertEquals( new Segment( "quoted text" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
	}

	@Test
	public void testQuotedQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
		Assert.assertEquals( new Segment( "\\\"quoted\\\" text" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );

		Assert.assertEquals( "\"quoted\" text" , NewWallsParser.unescape( options.get( 1 ).getValue( ) ) );
	}

	@Test
	public void testSemicolon( )
	{
		Segment segment = new Segment( "hello=world ;flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( 1 , options.size( ) );
	}

	@Test
	public void testSemicolonInQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text\" then=" , null , 7 , 3 );

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( new Segment( "\\\"quoted\\\" ;text" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
		Assert.assertEquals( new Segment( "" , null , 0 , 0 ) , options.get( 2 ).getValue( ) );
		Assert.assertEquals( new Segment( "then" , null , 0 , 0 ) , options.get( 2 ).getKey( ) );
	}

	@Test
	public void testUnclosedQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text then=\\\"" , null , 7 , 3 );

		try
		{
			List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );
		}
		catch( SegmentParseExpectedException ex )
		{
			Assert.assertEquals( ex.segment.startCol , segment.substring( segment.length( ) ).startCol );
			return;
		}

		Assert.fail( "expected SegmentParseExpectedException" );
	}
}
