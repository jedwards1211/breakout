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

	/**
	 * Adds a {@link UnitizedDouble} to this one. The result will be in this {@link UnitizedDouble}'s units.
	 * 
	 * @param addend
	 *            the {@link UnitizedDouble} to add to this one.
	 * @return
	 */
	public UnitizedDouble<T> add( UnitizedDouble<T> addend )
	{
		return new UnitizedDouble<T>( value + addend.doubleValue( unit ) , unit );
	}

	/**
	 * Subtracts a {@link UnitizedDouble} from this one. The result will be in this {@link UnitizedDouble}'s units.
	 * 
	 * @param addend
	 *            the {@link UnitizedDouble} to subtract from this one.
	 * @return
	 */
	public UnitizedDouble<T> subtract( UnitizedDouble<T> addend )
	{
		return new UnitizedDouble<T>( value - addend.doubleValue( unit ) , unit );
	}

	public UnitizedDouble<T> negate( )
	{
		return new UnitizedDouble<T>( -value , unit );
	}
}