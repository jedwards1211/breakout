package org.andork.unit;

import java.util.Objects;

public class MaybeUnitizedDouble<T extends UnitType<T>> extends MaybeUnitizedNumber<T>
{
	private final double	value;

	public MaybeUnitizedDouble( double value , Unit<T> unit )
	{
		super( unit );
		this.value = value;
	}

	public MaybeUnitizedDouble<T> in( Unit<T> unit , Unit<T> defaultUnit )
	{
		Unit<T> actual = unit == null ? defaultUnit : unit;
		if( unit == actual )
		{
			return this;
		}
		return new MaybeUnitizedDouble<>( doubleValue( unit , defaultUnit ) , unit );
	}

	public double doubleValue( Unit<T> unit , Unit<T> defaultUnit )
	{
		Unit<T> actual = unit == null ? defaultUnit : unit;
		if( unit == actual )
		{
			return value;
		}
		return unit.type.convert( value , actual , unit );
	}

	public int hashCode( )
	{
		return ( Double.hashCode( value ) * 31 ) ^ Objects.hashCode( unit );
	}

	public boolean equals( Object o )
	{
		if( o instanceof MaybeUnitizedDouble )
		{
			MaybeUnitizedDouble<?> u = ( MaybeUnitizedDouble<?> ) o;
			return value == u.value && unit == u.unit;
		}
		return false;
	}

	public String toString( )
	{
		if( unit == null )
		{
			return Double.toString( value );
		}
		return value + " " + unit;
	}
}