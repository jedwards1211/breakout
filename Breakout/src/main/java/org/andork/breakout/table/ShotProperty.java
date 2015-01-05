package org.andork.breakout.table;

import org.andork.q2.QSpec.Property;

public class ShotProperty<T> extends Property<T>
{
	@SuppressWarnings( "unchecked" )
	public ShotProperty( String name , ShotPropertyType propertyType )
	{
		super( name , ( Class<? super T> ) propertyType.valueClass );
		this.propertyType = propertyType;
	}

	@SuppressWarnings( "unchecked" )
	public ShotProperty( String name , ShotPropertyType propertyType , T initValue )
	{
		super( name , ( Class<? super T> ) propertyType.valueClass , initValue );
		this.propertyType = propertyType;
	}

	final ShotPropertyType	propertyType;

	public final ShotPropertyType propertyType( )
	{
		return propertyType;
	}
}
