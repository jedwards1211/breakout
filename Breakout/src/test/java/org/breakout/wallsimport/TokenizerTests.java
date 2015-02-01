package org.breakout.wallsimport;

import static org.breakout.wallsimport.WallsParser.SIGNED_FLOATING_POINT;
import static org.breakout.wallsimport.WallsParser.SIGNED_INTEGER;
import static org.breakout.wallsimport.WallsParser.UNSIGNED_FLOATING_POINT;
import static org.breakout.wallsimport.WallsParser.UNSIGNED_INTEGER;

import java.util.regex.Pattern;

import org.breakout.parse.LineTokenizer;
import org.breakout.parse.Token;
import org.junit.Assert;
import org.junit.Test;

public class TokenizerTests
{
	@Test
	public void pullOnlyWhitespaceTest( )
	{
		String line = "  \t\n  \r";
		LineTokenizer tokenizer = new LineTokenizer( line , 0 );
		Token token = tokenizer.pull( Character::isWhitespace );
		Assert.assertEquals( line , token.image );
		Assert.assertEquals( 0 , token.beginColumn );
		Assert.assertEquals( line.length( ) - 1 , token.endColumn );

		token = tokenizer.pull( Character::isWhitespace );
		Assert.assertNull( token );
	}

	@Test
	public void pullPartWhitespaceTest( )
	{
		String line = "  \tHello";
		LineTokenizer tokenizer = new LineTokenizer( line , 0 );
		Token token = tokenizer.pull( Character::isWhitespace );
		Assert.assertEquals( "  \t" , token.image );
		Assert.assertEquals( 0 , token.beginColumn );
		Assert.assertEquals( 2 , token.endColumn );

		token = tokenizer.pull( Character::isWhitespace );
		Assert.assertNull( token );
	}

	@Test
	public void pullOnlyNonWhitespaceTest( )
	{
		String line = "blahblahblah";
		LineTokenizer tokenizer = new LineTokenizer( line , 0 );
		Token token = tokenizer.pull( LineTokenizer::isNotWhitespace );
		Assert.assertEquals( line , token.image );
		Assert.assertEquals( 0 , token.beginColumn );
		Assert.assertEquals( line.length( ) - 1 , token.endColumn );

		token = tokenizer.pull( LineTokenizer::isNotWhitespace );
		Assert.assertNull( token );
	}

	@Test
	public void pullPartNonWhitespaceTest( )
	{
		String line = "Hello World";
		LineTokenizer tokenizer = new LineTokenizer( line , 0 );
		Token token = tokenizer.pull( LineTokenizer::isNotWhitespace );
		Assert.assertEquals( "Hello" , token.image );
		Assert.assertEquals( 0 , token.beginColumn );
		Assert.assertEquals( 4 , token.endColumn );

		token = tokenizer.pull( LineTokenizer::isNotWhitespace );
		Assert.assertNull( token );
	}

	@Test
	public void pullCharacterTest( )
	{
		String line = "=test";
		LineTokenizer tokenizer = new LineTokenizer( line , 0 );
		Token token = tokenizer.pull( '$' );
		Assert.assertNull( token );

		token = tokenizer.pull( '=' );
		Assert.assertEquals( "=" , token.image );
		Assert.assertEquals( 0 , token.beginColumn );
		Assert.assertEquals( 0 , token.endColumn );

		token = tokenizer.pull( 't' );
		Assert.assertEquals( "t" , token.image );
		Assert.assertEquals( 1 , token.beginColumn );
		Assert.assertEquals( 1 , token.endColumn );
	}

	public void testPull( String literal , Pattern pattern , String expected )
	{
		LineTokenizer tokenizer = new LineTokenizer( literal , 0 );
		Token token = tokenizer.pull( pattern );
		if( expected == null )
		{
			Assert.assertNull( token );
		}
		else
		{
			Assert.assertEquals( expected , token.image );
			Assert.assertEquals( 0 , token.beginColumn );
			Assert.assertEquals( expected.length( ) - 1 , token.endColumn );
		}
	}

	@Test
	public void pullUnsignedIntegerTests( )
	{
		testPull( "302" , UNSIGNED_INTEGER , "302" );
		testPull( "302." , UNSIGNED_INTEGER , "302" );
		testPull( "302.85" , UNSIGNED_INTEGER , "302" );
		testPull( ".85" , UNSIGNED_INTEGER , null );
		testPull( "302.85.85" , UNSIGNED_INTEGER , "302" );
		testPull( "302.85A" , UNSIGNED_INTEGER , "302" );
		testPull( "A302.85" , UNSIGNED_INTEGER , null );
		testPull( "..85" , UNSIGNED_INTEGER , null );
		testPull( "302.A" , UNSIGNED_INTEGER , "302" );
	}

	public void pullSignedIntegerTests( )
	{
		testPull( "302" , SIGNED_INTEGER , "302" );
		testPull( "-302" , SIGNED_INTEGER , "-302" );
		testPull( "+302" , SIGNED_INTEGER , "+302" );
		testPull( "--302" , SIGNED_INTEGER , null );
		testPull( "-" , SIGNED_INTEGER , null );
		testPull( "+" , SIGNED_INTEGER , null );
		testPull( "-+302" , SIGNED_INTEGER , null );
		testPull( "-302.85" , SIGNED_INTEGER , "-302" );
	}

	@Test
	public void pullUnsignedFloatingPointTests( )
	{
		testPull( "302" , UNSIGNED_FLOATING_POINT , "302" );
		testPull( "302." , UNSIGNED_FLOATING_POINT , "302." );
		testPull( "302.85" , UNSIGNED_FLOATING_POINT , "302.85" );
		testPull( ".85" , UNSIGNED_FLOATING_POINT , ".85" );
		testPull( "302.85.85" , UNSIGNED_FLOATING_POINT , "302.85" );
		testPull( "302.85A" , UNSIGNED_FLOATING_POINT , "302.85" );
		testPull( "A302.85" , UNSIGNED_FLOATING_POINT , null );
		testPull( "..85" , UNSIGNED_FLOATING_POINT , null );
		testPull( "302.A" , UNSIGNED_FLOATING_POINT , "302." );
	}

	@Test
	public void pullSignedFloatingPointTests( )
	{
		testPull( "302.85" , SIGNED_FLOATING_POINT , "302.85" );
		testPull( "-302.85" , SIGNED_FLOATING_POINT , "-302.85" );
		testPull( "+302.85" , SIGNED_FLOATING_POINT , "+302.85" );
		testPull( "-A" , SIGNED_FLOATING_POINT , null );
	}
}
