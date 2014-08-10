package org.andork.unit;

public class Unit<T extends UnitType<T>>
{
	public final T		type;
	public final String	id;
	
	public Unit( T type , String id )
	{
		super( );
		this.type = type;
		this.id = id;
	}
	
	public String toString( )
	{
		return id;
	}
}
