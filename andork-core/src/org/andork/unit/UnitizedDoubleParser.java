package org.andork.unit;

import java.text.CharacterIterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public class UnitizedDoubleParser<T extends UnitType<T>> implements Function<CharacterIterator, UnitizedDouble<T>>
{
	ToDoubleFunction<CharacterIterator>		doubleParser;
	Function<CharacterIterator, Unit<T>>	unitParser;
	Supplier<Unit<T>>						defaultUnit;
	
	public UnitizedDoubleParser( ToDoubleFunction<CharacterIterator> doubleParser , Function<CharacterIterator, Unit<T>> unitParser , Supplier<Unit<T>> defaultUnit )
	{
		super( );
		this.doubleParser = doubleParser;
		this.unitParser = unitParser;
		this.defaultUnit = defaultUnit;
	}
	
	@Override
	public UnitizedDouble<T> apply( CharacterIterator t )
	{
		double value = doubleParser.applyAsDouble( t );
		if( Double.isNaN( value ) )
		{
			return null;
		}
		
		Unit<T> unit = unitParser.apply( t );
		if( unit == null )
		{
			unit = defaultUnit.get( );
		}
		
		return new UnitizedDouble<>( value , unit );
	}
}
