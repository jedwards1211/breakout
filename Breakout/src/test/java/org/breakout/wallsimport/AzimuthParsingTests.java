package org.breakout.wallsimport;

import org.andork.parse.Segment;
import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.junit.Assert;
import org.junit.Test;

public class AzimuthParsingTests
{
	private void testParseAzimuth( String text , UnitizedDouble<Angle> expected )
	{
		Segment segment = new Segment( text , "file" , 2 , 6 );

		WallsParser parser = new WallsParser( );

		Assert.assertEquals(
			expected.doubleValue( Angle.degrees ) ,
			parser.new WallsLineParser( segment ).azimuth( Angle.degrees ).doubleValue( Angle.degrees ) ,
			1e-6 );
	}

	@Test
	public void test001( )
	{
		testParseAzimuth( "240" , new UnitizedDouble<>( 240.0 , Angle.degrees ) );
		testParseAzimuth( "240.25" , new UnitizedDouble<>( 240.25 , Angle.degrees ) );
		testParseAzimuth( "240.25m" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseAzimuth( "240.25M" , new UnitizedDouble<>( 240.25 , Angle.milsNATO ) );
		testParseAzimuth( "240.25g" , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseAzimuth( "240.25G" , new UnitizedDouble<>( 240.25 , Angle.gradians ) );
		testParseAzimuth( "n" , CardinalDirection.NORTH.angle );
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

		testParseAzimuth( "10:20" , new UnitizedDouble<>( 10 + 20.0 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "10:20.5" , new UnitizedDouble<>( 10 + 20.5 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "10:20:30" , new UnitizedDouble<>( 10 + 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20:30" , new UnitizedDouble<>( 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20.5:30.6" , new UnitizedDouble<>( 20.5 / 60.0 + 30.6 / 3600.0 , Angle.degrees ) );
		testParseAzimuth( ":20:" , new UnitizedDouble<>( 20.0 / 60.0 , Angle.degrees ) );
		testParseAzimuth( "::20" , new UnitizedDouble<>( ( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) );
		testParseAzimuth( "S::20E" , CardinalDirection.SOUTH.angle.subtract( new UnitizedDouble<>(
			( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) ) );
		testParseAzimuth( "S:20E" , CardinalDirection.SOUTH.angle.subtract( new UnitizedDouble<>(
			20.0 / 60.0 , Angle.degrees ) ) );
	}
}
