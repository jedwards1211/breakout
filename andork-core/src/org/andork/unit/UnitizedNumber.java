package org.andork.unit;

public abstract class UnitizedNumber<T extends UnitType<T>>
{
	public final Unit<T>	unit;
	
	protected UnitizedNumber( Unit<T> unit )
	{
		super( );
		if( unit == null )
		{
			throw new IllegalArgumentException( "unit must be non-null" );
		}
		this.unit = unit;
	}
	
	public abstract double doubleValue( Unit<T> unit );
}