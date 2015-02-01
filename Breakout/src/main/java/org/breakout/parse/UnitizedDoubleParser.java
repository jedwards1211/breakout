package org.breakout.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.andork.unit.Unit;
import org.andork.unit.UnitNameType;
import org.andork.unit.UnitNames;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;

public class UnitizedDoubleParser<T extends UnitType<T>>
{
	protected GenericFormat<Double>	numberFormat;
	protected Collection<Unit<T>>	units;
	protected Unit<T>				defaultUnit;
	protected UnitNames				unitNames;
	protected Map<String, Unit<T>>	unitMap;
	protected int					unitsMaxLength;
	protected boolean				allowWhitespace;

	public UnitizedDoubleParser<T> numberFormat( GenericFormat<Double> numberFormat )
	{
		this.numberFormat = numberFormat;
		return this;
	}

	public UnitizedDoubleParser<T> units( Collection<Unit<T>> units )
	{
		this.units = Collections.unmodifiableList( new ArrayList<>( units ) );
		rebuildUnitMap( );
		return this;
	}

	public UnitizedDoubleParser<T> defaultUnit( Unit<T> defaultUnit )
	{
		this.defaultUnit = defaultUnit;
		return this;
	}

	public UnitizedDoubleParser<T> unitNames( UnitNames unitNames )
	{
		this.unitNames = unitNames;
		rebuildUnitMap( );
		return this;
	}

	public UnitizedDoubleParser<T> allowWhitespace( boolean allow )
	{
		this.allowWhitespace = allow;
		return this;
	}

	public Map<String, Unit<T>> unitMap( )
	{
		return unitMap;
	}

	private void rebuildUnitMap( )
	{
		unitMap = unitNames == null || units == null ? Collections.emptyMap( ) : Collections.unmodifiableMap(
			createUnitNameMap( unitNames , units ) );
		unitsMaxLength = unitMap.isEmpty( ) ? 0 :
			unitMap.keySet( ).stream( ).mapToInt( String::length ).max( ).getAsInt( );
	}

	private static <T extends UnitType<T>> Map<String, Unit<T>> createUnitNameMap( UnitNames unitNames ,
		Collection<Unit<T>> units )
	{
		Map<String, Unit<T>> result = new HashMap<>( );
		for( Unit<T> unit : units )
		{
			for( UnitNameType type : UnitNameType.values( ) )
			{
				result.put( unitNames.getName( unit , 1 , type ) , unit );
				result.put( unitNames.getName( unit , 2 , type ) , unit );
			}
		}
		return result;
	}

	public ValueToken<UnitizedDouble<T>>
		pullUnitizedDouble( LineTokenizer lineTokenizer )
	{
		ValueToken<Double> value = lineTokenizer.pull( numberFormat );
		if( value == null )
		{
			return null;
		}
		int pos = lineTokenizer.position( );
		pullWhitespaceIfAllowed( lineTokenizer );

		ValueToken<Unit<T>> unit = lineTokenizer.pull( unitMap , unitsMaxLength );
		if( unit == null )
		{
			lineTokenizer.position( pos );
		}
		return new ValueToken<>( new UnitizedDouble<>(
			value.value , unit != null ? unit.value : defaultUnit ) , value , unit );
	}

	protected void pullWhitespaceIfAllowed( LineTokenizer lineTokenizer )
	{
		if( allowWhitespace )
		{
			lineTokenizer.pull( Character::isWhitespace );
		}
	}
}
