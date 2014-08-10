package org.andork.unit;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import org.andork.func.CharPredicate;

public class UnitizedDoubleArrayParser<T extends UnitType<T>> implements Function<CharacterIterator, UnitizedDoubleArray<T>>
{
	ToDoubleFunction<CharacterIterator>		doubleParser;
	CharPredicate							isDelimiter;
	Function<CharacterIterator, Unit<T>>	unitParser;
	Supplier<Unit<T>>						defaultUnit;
	
	public UnitizedDoubleArrayParser( ToDoubleFunction<CharacterIterator> doubleParser , CharPredicate isDelimiter , Function<CharacterIterator, Unit<T>> unitParser , Supplier<Unit<T>> defaultUnit )
	{
		super( );
		this.doubleParser = doubleParser;
		this.isDelimiter = isDelimiter;
		this.unitParser = unitParser;
		this.defaultUnit = defaultUnit;
	}
	
	@Override
	public UnitizedDoubleArray<T> apply( CharacterIterator t )
	{
		List<Double> values = new ArrayList<Double>( );
		
		while( true )
		{
			int lastStart = t.getIndex( );
			double value = doubleParser.applyAsDouble( t );
			if( Double.isNaN( value ) )
			{
				t.setIndex( lastStart );
				break;
			}
			values.add( value );
			
			char c = t.current( );
			while( Character.isWhitespace( c ) )
			{
				c = t.next( );
			}
			
			if( isDelimiter.test( c ) )
			{
				c = t.next( );
				while( Character.isWhitespace( c ) )
				{
					c = t.next( );
				}
			}
		}
		
		if( values.isEmpty( ) )
		{
			return null;
		}
		
		Unit<T> unit = unitParser.apply( t );
		if( unit == null )
		{
			unit = defaultUnit.get( );
		}
		
		UnitizedDoubleArray<T> result = new UnitizedDoubleArray<>( values.size( ) , unit );
		int k = 0;
		for( Double value : values )
		{
			result.set( k++ , value , unit );
		}
		
		return result;
	}
}
