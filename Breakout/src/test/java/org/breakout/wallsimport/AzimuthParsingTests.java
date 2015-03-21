package org.breakout.wallsimport;

import java.nio.file.Paths;
import java.util.function.Supplier;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.ValueToken;
import org.breakout.wallsimport.CardinalDirection;
import org.breakout.wallsimport.WallsImporter;
import org.breakout.wallsimport.WallsParser;
import org.breakout.wallsimport.WallsImportMessage.Severity;
import org.junit.Assert;
import org.junit.Test;

public class AzimuthParsingTests
{
	public void testParseAzimuth( String text , UnitizedDouble<Angle> expected )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		ValueToken<UnitizedDouble<Angle>> actual = parser.pullAzimuth( tokenizer , Angle.degrees ,
			( s , m , l , c ) ->
			{

			} );
		Assert.assertEquals( expected , actual == null ? null : actual.value );
	}

	public void
		testParseAzimuth( String text , Severity expectedSeverity , String expectedMessage ,
			int expectedColumn )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		try
		{
			parser.pullAzimuth( tokenizer , Angle.degrees ,
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
		testParseAzimuth( "240" , new UnitizedDouble<>( 240.0 , Angle.degrees ) );
		testParseAzimuth( "240q" , new UnitizedDouble<>( 240.0 , Angle.degrees ) );
		testParseAzimuth( "240.25" , new UnitizedDouble<>( 240.25 , Angle.degrees ) );
		testParseAzimuth( "240.25m" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseAzimuth( "240.25M/" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseAzimuth( "240.25g  " , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseAzimuth( "240.25GN" , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseAzimuth( "nn" , CardinalDirection.NORTH.angle );
		testParseAzimuth( "N" , CardinalDirection.NORTH.angle );
		testParseAzimuth( "s" , CardinalDirection.SOUTH.angle );
		testParseAzimuth( "S" , CardinalDirection.SOUTH.angle );
		testParseAzimuth( "e" , CardinalDirection.EAST.angle );
		testParseAzimuth( "E" , CardinalDirection.EAST.angle );
		testParseAzimuth( "w" , CardinalDirection.WEST.angle );
		testParseAzimuth( "W" , CardinalDirection.WEST.angle );
		testParseAzimuth( "N50W" , new UnitizedDouble<>( 310.0 , Angle.degrees ) );
		testParseAzimuth( "N50E" , new UnitizedDouble<>( 50.0 , Angle.degrees ) );
		testParseAzimuth( "S50W" , new UnitizedDouble<>( 230.0 , Angle.degrees ) );
		testParseAzimuth( "s50E" , new UnitizedDouble<>( 130.0 , Angle.degrees ) );
		testParseAzimuth( "e50n" , new UnitizedDouble<>( 40.0 , Angle.degrees ) );
		testParseAzimuth( "e50s" , new UnitizedDouble<>( 140.0 , Angle.degrees ) );
		testParseAzimuth( "w50n" , new UnitizedDouble<>( 320.0 , Angle.degrees ) );
		testParseAzimuth( "w50s" , new UnitizedDouble<>( 220.0 , Angle.degrees ) );
		testParseAzimuth( "w50gs" , new UnitizedDouble<>( 225.0 , Angle.degrees ) );

		testParseAzimuth( "10:20x" , new UnitizedDouble<>( 10 + 20.0 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "10:20.5x" , new UnitizedDouble<>( 10 + 20.5 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "10:20:30G" , new UnitizedDouble<>( 10 + 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20:30" , new UnitizedDouble<>( 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20.5:30.6" , new UnitizedDouble<>( 20.5 / 60.0 + 30.6 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20:" , new UnitizedDouble<>( 20.0 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "::20" , new UnitizedDouble<>( ( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) );
		testParseAzimuth( "S::20E" , CardinalDirection.SOUTH.angle.subtract( new UnitizedDouble<>(
			( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) ) );
		testParseAzimuth( "S:20E" , CardinalDirection.SOUTH.angle.subtract( new UnitizedDouble<>(
			20.0 / 60.0 , Angle.degrees ) ) );

		I18n i18n = new I18n( );
		Localizer localizer = i18n.forClass( WallsImporter.class );

		testParseAzimuth( "::" , Severity.ERROR , localizer.getString( "colonsWithoutDMS" ) , 2 );
		testParseAzimuth( "N50" , Severity.ERROR , localizer.getString( "expectedAzimuthToDirection" ) , 3 );
		testParseAzimuth( "N50N" , Severity.ERROR , localizer.getString( "invalidDirectionCombination" ) , 3 );
		testParseAzimuth( "N50S" , Severity.ERROR , localizer.getString( "invalidDirectionCombination" ) , 3 );
		testParseAzimuth( "W50W" , Severity.ERROR , localizer.getString( "invalidDirectionCombination" ) , 3 );
		testParseAzimuth( "W50E" , Severity.ERROR , localizer.getString( "invalidDirectionCombination" ) , 3 );
	}
}
