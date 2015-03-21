package org.breakout.wallsimport;

import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.Segment;
import org.junit.Assert;
import org.junit.Test;

public class NewInclinationParsingTests
{
	public void testParseInclination( String text , UnitizedDouble<Angle> expected )
	{
		Segment segment = new Segment( text , "file" , 2 , 6 );

		Assert.assertEquals(
			expected.doubleValue( Angle.degrees ) ,
			NewWallsParser.parseInclination( segment , Angle.degrees ).doubleValue( Angle.degrees ) ,
			1e-6 );
	}

	@Test
	public void test001( )
	{
		testParseInclination( "0" , new UnitizedDouble<>( 0.0 , Angle.degrees ) );
		testParseInclination( "+40" , new UnitizedDouble<>( 40.0 , Angle.degrees ) );
		testParseInclination( "+40" , new UnitizedDouble<>( 40.0 , Angle.degrees ) );
		testParseInclination( "+40.25" , new UnitizedDouble<>( 40.25 , Angle.degrees ) );
		testParseInclination( "+40.25m" , new UnitizedDouble<>( 40.25 , Angle.milsNATO ) );
		testParseInclination( "+40.25M" , new UnitizedDouble<>( 40.25 , Angle.milsNATO ) );
		testParseInclination( "+40.25g" , new UnitizedDouble<>( 40.25 , Angle.gradians ) );
		testParseInclination( "+40.25G" , new UnitizedDouble<>( 40.25 , Angle.gradians ) );
		testParseInclination( "+40.25G" , new UnitizedDouble<>( 40.25 , Angle.gradians ) );
		testParseInclination( "-40.25G" , new UnitizedDouble<>( -40.25 , Angle.gradians ) );
		testParseInclination( "-40.25p" , new UnitizedDouble<>( -40.25 , Angle.percentGrade ) );

		testParseInclination( "+10:20" , new UnitizedDouble<>( 10 + 20.0 / 60.0 , Angle.degrees ) );
		testParseInclination( "+10:20.5" , new UnitizedDouble<>( 10 + 20.5 / 60.0 , Angle.degrees ) );
		testParseInclination( "-10:20.5" , new UnitizedDouble<>( - ( 10 + 20.5 / 60.0 ) , Angle.degrees ) );
		testParseInclination( "+10:20:30" , new UnitizedDouble<>( 10 + 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseInclination( "+:20:30" , new UnitizedDouble<>( 20.0 / 60.0 + 30.0 / 3600.0 , Angle.degrees ) );
		testParseInclination( "-:20:30" , new UnitizedDouble<>( - ( 20.0 / 60.0 + 30.0 / 3600.0 ) , Angle.degrees ) );
		testParseInclination( "+:20.5:30.6" , new UnitizedDouble<>( 20.5 / 60.0 + 30.6 / 3600.0 , Angle.degrees ) );
		testParseInclination( "+:20:" , new UnitizedDouble<>( 20.0 / 60.0 , Angle.degrees ) );
		testParseInclination( "+::20" , new UnitizedDouble<>( ( 0.0 + 20.0 / 60.0 ) / 60.0 , Angle.degrees ) );
	}
}
