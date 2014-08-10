package org.andork.unit;

import java.text.CharacterIterator;
import java.util.function.Function;

import org.andork.func.CharPredicate;

public class UnitParser<T extends UnitType<T>> implements Function<CharacterIterator, Unit<T>>
{
	T							unitType;
	CharPredicate				isUnitStartChar;
	CharPredicate				isUnitChar;
	Function<String, Unit<T>>	unitLookup;
	
	public UnitParser( T unitType , CharPredicate isUnitStartChar , CharPredicate isUnitChar , Function<String, Unit<T>> unitLookup )
	{
		super( );
		this.unitType = unitType;
		this.isUnitStartChar = isUnitStartChar;
		this.isUnitChar = isUnitChar;
		this.unitLookup = unitLookup;
	}
	
	@Override
	public Unit<T> apply( CharacterIterator t )
	{
		char c = t.current( );
		
		while( Character.isWhitespace( c ) )
		{
			c = t.next( );
		}
		
		if( !isUnitStartChar.test( c ) )
		{
			return null;
		}
		
		StringBuilder sb = new StringBuilder( );
		
		while( isUnitStartChar.test( c ) )
		{
			if( sb.length( ) > 0 )
			{
				sb.append( ' ' );
			}
			
			while( isUnitChar.test( c ) )
			{
				sb.append( c );
				c = t.next( );
			}
			
			while( Character.isWhitespace( c ) )
			{
				c = t.next( );
			}
		}
		
		Unit<T> result = unitLookup.apply( sb.toString( ) );
		if( result == null )
		{
			throw new IllegalArgumentException( "Unrecognized unit: " + sb.toString( ) );
		}
		return result;
	}
}
