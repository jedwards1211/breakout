package org.breakout.wallsimport;

import java.util.Arrays;

import org.andork.parse.Segment;
import org.andork.unit.Length;
import org.junit.Assert;
import org.junit.Test;

public class UnitsOptionsParsingTests
{
//	@Test
//	public void testBasicUnitsOptions( )
//	{
//		Segment segment = new Segment( "hello=world flag=\"quoted text\"" , null , 7 , 3 );
//
//		UnitsOptionsSaver options = new UnitsOptionsSaver( );
//		WallsParser.parseUnitsOptions( segment , options );
//
//		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
//		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
//		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
//		Assert.assertEquals( new Segment( "\"quoted text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
//	}
//
//	@Test
//	public void testQuotedQuote( )
//	{
//		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );
//
//		UnitsOptionsSaver options = new UnitsOptionsSaver( );
//		WallsParser.parseUnitsOptions( segment , options );
//
//		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
//		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
//		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
//		Assert.assertEquals( new Segment( "\"\\\"quoted\\\" text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
//
//		Assert.assertEquals( "\"quoted\" text" , WallsParser.dequoteUnitsArg( options.get( 1 ).getValue( ) ) );
//		Assert.assertEquals( "world" , WallsParser.dequoteUnitsArg( options.get( 0 ).getValue( ) ) );
//	}
//
//	@Test
//	public void testSemicolon( )
//	{
//		Segment segment = new Segment( "hello=world;flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );
//
//		UnitsOptionsSaver options = new UnitsOptionsSaver( );
//		WallsParser.parseUnitsOptions( segment , options );
//
//		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
//		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
//		Assert.assertEquals( 1 , options.size( ) );
//
//		segment = new Segment( "hello=;world flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );
//
//		options = new UnitsOptionsSaver( );
//		WallsParser.parseUnitsOptions( segment , options );
//
//		Assert.assertEquals( new Segment( "" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
//		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
//		Assert.assertEquals( 1 , options.size( ) );
//
//	}
//
//	@Test
//	public void testSemicolonInQuote( )
//	{
//		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text\" then=" , null , 7 , 3 );
//
//		UnitsOptionsSaver options = new UnitsOptionsSaver( );
//		WallsParser.parseUnitsOptions( segment , options );
//
//		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
//		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
//		Assert.assertEquals( new Segment( "\"\\\"quoted\\\" ;text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
//		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
//		Assert.assertEquals( new Segment( "" , null , 0 , 0 ) , options.get( 2 ).getValue( ) );
//		Assert.assertEquals( new Segment( "then" , null , 0 , 0 ) , options.get( 2 ).getKey( ) );
//	}
//
//	@Test
//	public void testUnclosedQuote( )
//	{
//		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" ;text then=\\\"" , null , 7 , 0 );
//
//		try
//		{
//			UnitsOptionsSaver options = new UnitsOptionsSaver( );
//			WallsParser.parseUnitsOptions( segment , options );
//		}
//		catch( SegmentParseExpectedException ex )
//		{
//			System.err.println( ex.getLocalizedMessage( ) );
//			Assert.assertEquals( ex.segment.startCol , segment.substring( segment.length( ) ).startCol );
//			return;
//		}
//
//		Assert.fail( "expected SegmentParseExpectedException" );
//	}

	@Test
	public void testSaveAndRestore( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		Assert.assertEquals( 0 , parser.stack.size( ) );

		parser.parseLine( new Segment( "#u save save" , null , 0 , 0 ) );
		Assert.assertEquals( 2 , parser.stack.size( ) );

		WallsUnits top = parser.stack.peek( );

		parser.parseLine( new Segment( "#u restore" , null , 0 , 0 ) );
		Assert.assertEquals( 1 , parser.stack.size( ) );
		Assert.assertSame( parser.units , top );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u save=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u save=test" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u restore=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u restore=test" , null , 0 , 0 ) ) );
	}

	@Test
	public void testFeetAndMeters( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u meters" , null , 0 , 0 ) );
		Assert.assertEquals( Length.meters , parser.units.d_unit );
		Assert.assertEquals( Length.meters , parser.units.s_unit );

		parser.parseLine( new Segment( "#u m f" , null , 0 , 0 ) );
		Assert.assertEquals( Length.feet , parser.units.d_unit );
		Assert.assertEquals( Length.feet , parser.units.s_unit );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u m=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u meters=test" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u f=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u feet=test" , null , 0 , 0 ) ) );
	}

	@Test
	public void testDAndS( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u d=f s=meters" , null , 0 , 0 ) );
		Assert.assertEquals( Length.feet , parser.units.d_unit );
		Assert.assertEquals( Length.meters , parser.units.s_unit );

		parser.parseLine( new Segment( "#u d=f s=meters s=feet d=m;test" , null , 0 , 0 ) );
		Assert.assertEquals( Length.meters , parser.units.d_unit );
		Assert.assertEquals( Length.feet , parser.units.s_unit );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u d" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u d=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u s" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u s=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u s=;feet" , null , 0 , 0 ) ) );
	}

	private void assertThrows( Runnable r )
	{
		try
		{
			r.run( );
			Assert.fail( "expected exception" );
		}
		catch( Exception ex )
		{
			System.err.println( ex.getLocalizedMessage( ) );
		}
	}

	@Test
	public void testTypeAB( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u typeab=Corrected,2,X" , null , 0 , 0 ) );
		Assert.assertEquals( true , parser.units.typeab_corrected );
		Assert.assertEquals( 2.0 , parser.units.typeab_tolerance , 0.0 );
		Assert.assertEquals( true , parser.units.typeab_noAverage );

		parser.parseLine( new Segment( "#u typeab=n" , null , 0 , 0 ) );
		Assert.assertEquals( false , parser.units.typeab_corrected );
		Assert.assertEquals( null , parser.units.typeab_tolerance );
		Assert.assertEquals( false , parser.units.typeab_noAverage );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u typeab=n,2,x,y" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u typeab" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u typeab=" , null , 0 , 0 ) ) );
	}

	@Test
	public void testOrder( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u o=vad" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( CtElement.V , CtElement.A , CtElement.D ) , parser.units.ctOrder );

		parser.parseLine( new Segment( "#u o=ad" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( CtElement.A , CtElement.D ) , parser.units.ctOrder );

		parser.parseLine( new Segment( "#u o=nue" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( RectElement.N , RectElement.U , RectElement.E ) , parser.units.rectOrder );

		parser.parseLine( new Segment( "#u o=ne" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( RectElement.N , RectElement.E ) , parser.units.rectOrder );

		parser.parseLine( new Segment( "#u o=en" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( RectElement.E , RectElement.N ) , parser.units.rectOrder );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=d" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=dv" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=dad" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=av" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=dan" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=n" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=nen" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=nu" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u o=eu" , null , 0 , 0 ) ) );
	}

	@Test
	public void testLrud( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u lrud=f:rlud" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.From , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.R , LrudElement.L , LrudElement.U , LrudElement.D ) , parser.units.lrud_order );

		parser.parseLine( new Segment( "#u lrud=tb" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.TB , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.L , LrudElement.R , LrudElement.U , LrudElement.D ) , parser.units.lrud_order );

		parser.parseLine( new Segment( "#u lrud=from:urld" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.From , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.U , LrudElement.R , LrudElement.L , LrudElement.D ) , parser.units.lrud_order );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=x" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=from:" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=x:lrud" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=from:lr" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u lrud=from:lrru" , null , 0 , 0 ) ) );
	}

	@Test
	public void testMacroDefinition( )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		parser.parseLine( new Segment( "#u $hello=world" , null , 0 , 0 ) );
		Assert.assertEquals( "world" , parser.macros.get( "hello" ) );

		parser.parseLine( new Segment( "#u $hello=\"beautiful world\"" , null , 0 , 0 ) );
		Assert.assertEquals( "beautiful world" , parser.macros.get( "hello" ) );

		parser.parseLine( new Segment( "#u $hello=\"\\\"beautiful \\\" \\nworld\"" , null , 0 , 0 ) );
		Assert.assertEquals( "\"beautiful \" \nworld" , parser.macros.get( "hello" ) );

		parser.parseLine( new Segment( "#u $hello  " , null , 0 , 0 ) );
		Assert.assertEquals( null , parser.macros.get( "hello" ) );

		assertThrows( ( ) -> parser.parseLine( new Segment( "#u $hello= " , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.parseLine( new Segment( "#u $hello=\"world " , null , 0 , 0 ) ) );
	}
}
