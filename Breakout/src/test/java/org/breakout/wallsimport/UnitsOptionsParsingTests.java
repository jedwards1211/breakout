package org.breakout.wallsimport;

import java.util.Arrays;
import java.util.List;

import org.andork.unit.Length;
import org.andork.util.Pair;
import org.breakout.parse.Segment;
import org.breakout.parse.SegmentParseExpectedException;
import org.junit.Assert;
import org.junit.Test;

public class UnitsOptionsParsingTests
{
	@Test
	public void testBasicUnitsOptions( )
	{
		Segment segment = new Segment( "hello=world flag=\"quoted text\"" , null , 7 , 3 );

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
		Assert.assertEquals( new Segment( "\"quoted text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
	}

	@Test
	public void testQuotedQuote( )
	{
		Segment segment = new Segment( "hello=world flag=\"\\\"quoted\\\" text\"" , null , 7 , 3 );

		List<Pair<Segment, Segment>> options = NewWallsParser.parseUnitsOptions( segment );

		Assert.assertEquals( new Segment( "world" , null , 0 , 0 ) , options.get( 0 ).getValue( ) );
		Assert.assertEquals( new Segment( "hello" , null , 0 , 0 ) , options.get( 0 ).getKey( ) );
		Assert.assertEquals( new Segment( "flag" , null , 0 , 0 ) , options.get( 1 ).getKey( ) );
		Assert.assertEquals( new Segment( "\"\\\"quoted\\\" text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );

		Assert.assertEquals( "\"quoted\" text" , NewWallsParser.dequoteUnitsArg( options.get( 1 ).getValue( ) ) );
		Assert.assertEquals( "world" , NewWallsParser.dequoteUnitsArg( options.get( 0 ).getValue( ) ) );
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
		Assert.assertEquals( new Segment( "\"\\\"quoted\\\" ;text\"" , null , 0 , 0 ) , options.get( 1 ).getValue( ) );
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
			NewWallsParser.parseUnitsOptions( segment );
		}
		catch( SegmentParseExpectedException ex )
		{
			Assert.assertEquals( ex.segment.startCol , segment.substring( segment.length( ) ).startCol );
			return;
		}

		Assert.fail( "expected SegmentParseExpectedException" );
	}

	@Test
	public void testSaveAndRestore( )
	{
		NewWallsParser parser = new NewWallsParser( );

		Assert.assertEquals( 0 , parser.stack.size( ) );

		parser.processUnits( new Segment( "save save" , null , 0 , 0 ) );
		Assert.assertEquals( 2 , parser.stack.size( ) );

		WallsUnits top = parser.stack.peek( );

		parser.processUnits( new Segment( "restore" , null , 0 , 0 ) );
		Assert.assertEquals( 1 , parser.stack.size( ) );
		Assert.assertSame( parser.units , top );

		assertThrows( ( ) -> parser.processUnits( new Segment( "save=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "save=test" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "restore=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "restore=test" , null , 0 , 0 ) ) );
	}

	@Test
	public void testFeetAndMeters( )
	{
		NewWallsParser parser = new NewWallsParser( );

		parser.processUnits( new Segment( "meters" , null , 0 , 0 ) );
		Assert.assertEquals( Length.meters , parser.units.d_unit );
		Assert.assertEquals( Length.meters , parser.units.s_unit );

		parser.processUnits( new Segment( "m f" , null , 0 , 0 ) );
		Assert.assertEquals( Length.feet , parser.units.d_unit );
		Assert.assertEquals( Length.feet , parser.units.s_unit );

		assertThrows( ( ) -> parser.processUnits( new Segment( "m=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "meters=test" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "f=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "feet=test" , null , 0 , 0 ) ) );
	}

	@Test
	public void testDAndS( )
	{
		NewWallsParser parser = new NewWallsParser( );

		parser.processUnits( new Segment( "d=f s=meters" , null , 0 , 0 ) );
		Assert.assertEquals( Length.feet , parser.units.d_unit );
		Assert.assertEquals( Length.meters , parser.units.s_unit );

		parser.processUnits( new Segment( "d=f s=meters s=feet d=m;test" , null , 0 , 0 ) );
		Assert.assertEquals( Length.meters , parser.units.d_unit );
		Assert.assertEquals( Length.feet , parser.units.s_unit );

		assertThrows( ( ) -> parser.processUnits( new Segment( "d" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "d=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "s" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "s=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "s=;feet" , null , 0 , 0 ) ) );
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

		}
	}

	@Test
	public void testTypeAB( )
	{
		NewWallsParser parser = new NewWallsParser( );

		parser.processUnits( new Segment( "typeab=Corrected,2,X" , null , 0 , 0 ) );
		Assert.assertEquals( true , parser.units.typeab_corrected );
		Assert.assertEquals( 2.0 , parser.units.typeab_tolerance , 0.0 );
		Assert.assertEquals( true , parser.units.typeab_noAverage );

		parser.processUnits( new Segment( "typeab=n" , null , 0 , 0 ) );
		Assert.assertEquals( false , parser.units.typeab_corrected );
		Assert.assertEquals( null , parser.units.typeab_tolerance );
		Assert.assertEquals( false , parser.units.typeab_noAverage );

		assertThrows( ( ) -> parser.processUnits( new Segment( "typeab=n,2,x,y" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "typeab" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "typeab=" , null , 0 , 0 ) ) );
	}

	@Test
	public void testOrder( )
	{
		NewWallsParser parser = new NewWallsParser( );

		parser.processUnits( new Segment( "o=vad" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( VectorElement.V , VectorElement.A , VectorElement.D ) , parser.units.order );

		parser.processUnits( new Segment( "o=ad" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( VectorElement.A , VectorElement.D ) , parser.units.order );

		parser.processUnits( new Segment( "o=nue" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( VectorElement.N , VectorElement.U , VectorElement.E ) , parser.units.order );

		parser.processUnits( new Segment( "o=ne" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( VectorElement.N , VectorElement.E ) , parser.units.order );

		parser.processUnits( new Segment( "o=en" , null , 0 , 0 ) );
		Assert.assertEquals( Arrays.asList( VectorElement.E , VectorElement.N ) , parser.units.order );

		assertThrows( ( ) -> parser.processUnits( new Segment( "o" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=d" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=dv" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=dad" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=av" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=dan" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=n" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=nen" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=nu" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "o=eu" , null , 0 , 0 ) ) );
	}

	@Test
	public void testLrud( )
	{
		NewWallsParser parser = new NewWallsParser( );

		parser.processUnits( new Segment( "lrud=f:rlud" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.From , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.R , LrudElement.L , LrudElement.U , LrudElement.D ) , parser.units.lrud_order );

		parser.processUnits( new Segment( "lrud=tb" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.TB , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.L , LrudElement.R , LrudElement.U , LrudElement.D ) , parser.units.lrud_order );

		parser.processUnits( new Segment( "lrud=from:urld" , null , 0 , 0 ) );
		Assert.assertEquals( LrudType.From , parser.units.lrud );
		Assert.assertEquals( Arrays.asList( LrudElement.U , LrudElement.R , LrudElement.L , LrudElement.D ) , parser.units.lrud_order );

		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud=" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud=x" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud=from:" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud=from:lr" , null , 0 , 0 ) ) );
		assertThrows( ( ) -> parser.processUnits( new Segment( "lrud=from:lrru" , null , 0 , 0 ) ) );
	}
}
