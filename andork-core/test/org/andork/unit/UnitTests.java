package org.andork.unit;

import java.text.StringCharacterIterator;
import java.util.Locale;

import org.andork.format.DoubleParser;
import org.junit.Assert;
import org.junit.Test;

public class UnitTests
{
	public static void main( String[ ] args )
	{
		System.out.println( new UnitizedDouble<>( 2.0 , Length.meters ).in( Length.feet ) );

		System.out.println( UnitNames.getName( Locale.CANADA , Angle.degrees , 1.1 , UnitNameType.FULL ) );

		System.out.println( UnitNames.lookup( Locale.CANADA , "meters" , Length.type ) );

		System.out.println( UnitNames.lookup( Locale.CANADA , "deg" , Length.type ) );

		System.out.println( UnitNames.lookup( Locale.CANADA , "rad" , Angle.type ) );

		DoubleParser dp = new DoubleParser( );
		UnitParser<Length> up = new UnitParser<Length>( Length.type ,
			c -> Character.isLetter( c ) ,
			c -> Character.isLetter( c ) ,
			name -> UnitNames.lookup( Locale.getDefault( ) , name , Length.type ) );

		UnitizedDoubleParser<Length> udp = new UnitizedDoubleParser<>( dp , up , ( ) -> Length.meters );

		UnitizedDoubleArrayParser<Length> udap = new UnitizedDoubleArrayParser<>(
			dp , c -> c == ';' , up , ( ) -> Length.meters );

		StringCharacterIterator i = new StringCharacterIterator( "2.5ft/8m/6 7" );

		System.out.println( udp.apply( i ) );
		i.next( );
		System.out.println( udp.apply( i ) );
		i.next( );
		System.out.println( udp.apply( i ) );
		System.out.println( udp.apply( i ) );

		System.out.println( UnitizedDoubleArray.toString( udap
			.apply( new StringCharacterIterator( "3 2\t5.25; 6;7ft" ) ) ) );
	}

	@Test
	public void unitTests( )
	{
		Assert.assertEquals( 1.609344 , Length.type.convert( 1.0 , Length.miles , Length.kilometers ) , 0.0 );
		Assert.assertEquals( 0.9144 , Length.type.convert( 1.0 , Length.yards , Length.meters ) , 0.0 );
	}
}
