package org.breakout.wallsimport;

import java.nio.file.Paths;
import java.util.function.Supplier;

import javax.management.RuntimeErrorException;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.ValueToken;
import org.breakout.wallsimport.WallsImporter;
import org.breakout.wallsimport.WallsParser;
import org.breakout.wallsimport.WallsImportMessage.Severity;
import org.junit.Assert;
import org.junit.Test;

public class DistanceParsingTests
{
	public void testParseUnsignedDistance( String text , UnitizedDouble<Length> expected )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		ValueToken<UnitizedDouble<Length>> actual = parser.pullUnsignedDistance( tokenizer , Length.meters ,
			( s , m , l , c ) ->
			{
			} );
		Assert.assertEquals( expected , actual == null ? null : actual.value );
	}

	public void
		testParseUnsignedDistance( String text , Severity expectedSeverity , String expectedMessage ,
			int expectedColumn )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		try
		{
			parser.pullUnsignedDistance( tokenizer , Length.meters ,
				( Severity actualSeverity , String actualMessage , int actualLine , int actualColumn ) ->
				{
					Assert.assertEquals( expectedSeverity , actualSeverity );
					Assert.assertEquals( expectedMessage , actualMessage );
					Assert.assertEquals( 0 , actualLine );
					Assert.assertEquals( expectedColumn , actualColumn );
					throw new RuntimeException( );
				} );
		}
		catch( Exception ex )
		{
			return;
		}
		Assert.fail( "expected an exception" );
	}

	public void testParseSignedDistance( String text , UnitizedDouble<Length> expected )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		ValueToken<UnitizedDouble<Length>> actual = parser.pullSignedDistance( tokenizer , Length.meters ,
			( s , m , l , c ) ->
			{
			} );
		Assert.assertEquals( expected , actual == null ? null : actual.value );
	}

	public void
		testParseSignedDistance( String text , Severity expectedSeverity , String expectedMessage ,
			int expectedColumn )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		try
		{
			parser.pullSignedDistance( tokenizer , Length.meters ,
				( Severity actualSeverity , String actualMessage , int actualLine , int actualColumn ) ->
				{
					Assert.assertEquals( expectedSeverity , actualSeverity );
					Assert.assertEquals( expectedMessage , actualMessage );
					Assert.assertEquals( 0 , actualLine );
					Assert.assertEquals( expectedColumn , actualColumn );
					throw new RuntimeException( );
				} );
		}
		catch( Exception ex )
		{
			return;
		}
		Assert.fail( "expected an exception" );
	}

	@Test
	public void test001( )
	{
		testParseUnsignedDistance( "240" , new UnitizedDouble<>( 240.0 , Length.meters ) );
		testParseUnsignedDistance( "+240" , null );
		testParseUnsignedDistance( "-240" , null );
		testParseUnsignedDistance( "a240" , null );
		testParseUnsignedDistance( "240m" , new UnitizedDouble<>( 240.0 , Length.meters ) );
		testParseUnsignedDistance( "240f" , new UnitizedDouble<>( 240.0 , Length.feet ) );
		testParseUnsignedDistance( "240.05ff" , new UnitizedDouble<>( 240.05 , Length.feet ) );
		testParseUnsignedDistance( "240i6" , new UnitizedDouble<>( 240.5 , Length.feet ) );
		testParseUnsignedDistance( "i6" , new UnitizedDouble<>( 6 , Length.inches ) );
		testParseUnsignedDistance( "240.05k" , new UnitizedDouble<>( 240.05 , Length.meters ) );

		testParseSignedDistance( "+240" , new UnitizedDouble<>( 240.0 , Length.meters ) );
		testParseSignedDistance( "+240m" , new UnitizedDouble<>( 240.0 , Length.meters ) );
		testParseSignedDistance( "+240f" , new UnitizedDouble<>( 240.0 , Length.feet ) );
		testParseSignedDistance( "+240.05ff" , new UnitizedDouble<>( 240.05 , Length.feet ) );
		testParseSignedDistance( "+240i6" , new UnitizedDouble<>( 240.5 , Length.feet ) );
		testParseSignedDistance( "+i6" , new UnitizedDouble<>( 6 , Length.inches ) );
		testParseSignedDistance( "+240.05k" , new UnitizedDouble<>( 240.05 , Length.meters ) );

		testParseSignedDistance( "-240" , new UnitizedDouble<>( -240.0 , Length.meters ) );
		testParseSignedDistance( "-240m" , new UnitizedDouble<>( -240.0 , Length.meters ) );
		testParseSignedDistance( "-240f" , new UnitizedDouble<>( -240.0 , Length.feet ) );
		testParseSignedDistance( "-240.05ff" , new UnitizedDouble<>( -240.05 , Length.feet ) );
		testParseSignedDistance( "-240i6" , new UnitizedDouble<>( -240.5 , Length.feet ) );
		testParseSignedDistance( "-i6" , new UnitizedDouble<>( -6 , Length.inches ) );
		testParseSignedDistance( "-240.05k" , new UnitizedDouble<>( -240.05 , Length.meters ) );

		I18n i18n = new I18n( );
		Localizer localizer = i18n.forClass( WallsImporter.class );

		testParseUnsignedDistance( "240i" ,
			Severity.ERROR , localizer.getString( "expectedNumberAfterInches" ) , 4 );
		testParseUnsignedDistance( "i" ,
			Severity.ERROR , localizer.getString( "expectedNumberAfterInches" ) , 1 );

		testParseSignedDistance( "-240i" ,
			Severity.ERROR , localizer.getString( "expectedNumberAfterInches" ) , 5 );
		testParseSignedDistance( "--240i" ,
			Severity.ERROR , localizer.getString( "expectedNumberOrInches" ) , 1 );
	}
}
