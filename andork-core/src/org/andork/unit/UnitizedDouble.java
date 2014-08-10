package org.andork.unit;

public class UnitizedDouble<T extends UnitType<T>> extends UnitizedNumber<T>
{
	private final double	value;
	
	public UnitizedDouble( double value , Unit<T> unit )
	{
		super( unit );
		this.value = value;
	}
	
	public UnitizedDouble<T> in( Unit<T> unit )
	{
		if( unit == this.unit )
		{
			return this;
		}
		return new UnitizedDouble<>( doubleValue( unit ) , unit );
	}
	
	public double doubleValue( Unit<T> unit )
	{
		if( unit == this.unit )
		{
			return value;
		}
		return this.unit.type.convert( value , this.unit , unit );
	}
	
	public int hashCode( )
	{
		return ( Double.hashCode( value ) * 31 ) ^ unit.hashCode( );
	}
	
	public boolean equals( Object o )
	{
		if( o instanceof UnitizedDouble )
		{
			UnitizedDouble<?> u = ( UnitizedDouble<?> ) o;
			return value == u.value && unit == u.unit;
		}
		return false;
	}
	
	public String toString( )
	{
		return value + " " + unit;
	}
}