package org.breakout.wallsimport;

import java.nio.file.Paths;
import java.util.function.Supplier;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.ValueToken;
import org.breakout.wallsimport.WallsImporter;
import org.breakout.wallsimport.WallsParser;
import org.breakout.wallsimport.WallsImportMessage.Severity;
import org.junit.Assert;
import org.junit.Test;

public class InclinationParsingTests
{
	public void testParseInclination( String text , UnitizedDouble<Angle> expected )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		ValueToken<UnitizedDouble<Angle>> actual = parser.pullInclination( tokenizer , Angle.degrees ,
			( s , m , l , c ) ->
			{

			} );
		Assert.assertEquals( expected , actual == null ? null : actual.value );
	}

	public void
		testParseInclination( String text , Severity expectedSeverity , String expectedMessage ,
			int expectedColumn )
	{
		I18n i18n = new I18n( );
		WallsParser parser = new WallsParser( i18n );
		LineTokenizer tokenizer = new LineTokenizer( text , 0 );

		try
		{
			parser.pullInclination( tokenizer , Angle.degrees ,
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
		testParseInclination( "240" , new UnitizedDouble<>( 240.0 , Angle.degrees ) );
		testParseInclination( "240q" , new UnitizedDouble<>( 240.0 , Angle.degrees ) );
		testParseInclination( "240.25" , new UnitizedDouble<>( 240.25 , Angle.degrees ) );
		testParseInclination( "240.25m" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseInclination( "240.25M/" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseInclination( "240.25g  " , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseInclination( "240.25GN" , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseInclination( "+240.25GN" , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseInclination( "-240.25GN" , new UnitizedDouble<>( -240.25 , Angle.gradians ) );
		testParseInclination( "-240.25pN" , new UnitizedDouble<>( -240.25 , Angle.percentGrade ) );

		testParseInclination( "10:20x" , new UnitizedDouble<>( 10 + 20.0 / 60.0 , Angle.degrees ) );
		testParseInclination( "10:20.5x" , new UnitizedDouble<>( 10 + 20.5 / 60.0 , Angle.degrees ) );
		testParseInclination( "-10:20.5x" , new UnitizedDouble<>( - ( 10 + 20.5 / 60.0 ) , Angle.degrees ) );
		testParseInclination( "-1" , new UnitizedDouble<>( -1.0 , Angle.degrees ) );
		testParseInclination( "10:20:30G" , new UnitizedDouble<>( 10 + 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseInclination( ":20:30" , new UnitizedDouble<>( 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseInclination( "-:20:30" , new UnitizedDouble<>( - ( 20.0 / 60.0 + 30.0 / 3600.0 ) , Angle.degrees ) );
		testParseInclination( ":20.5:30.6" , new UnitizedDouble<>( 20.5 / 60.0 + 30.6 / 3600.0 , Angle.degrees ) );
		testParseInclination( ":20:" , new UnitizedDouble<>( 20.0 / 60.0 , Angle.degrees ) );
		testParseInclination( "::20" , new UnitizedDouble<>( ( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) );
		testParseInclination( "-:20-:30" , new UnitizedDouble<>( - ( 20.0 / 60.0 ) , Angle.degrees ) );

		I18n i18n = new I18n( );
		Localizer localizer = i18n.forClass( WallsImporter.class );

		testParseInclination( "--:20-:30" , Severity.ERROR , localizer.getString( "expectedInclinationAngle" ) , 1 );
		testParseInclination( "::" , Severity.ERROR , localizer.getString( "colonsWithoutDMS" ) , 2 );
	}
}
