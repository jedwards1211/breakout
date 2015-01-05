package org.andork.breakout.table;

import org.andork.unit.Unit;

public enum ShotPropertyType
{
	STRING( String.class , true ),
	INTEGER( ParsedText.class , true ),
	DOUBLE( ParsedText.class , true ),
	DATE( ParsedText.class , true ),
	VECTOR( ShotVector.class , false ),
	CROSS_SECTION( CrossSection.class , false ),
	TAGS( String[ ].class , true ),
	PATH( String[ ].class , true ),
	UNIT( Unit.class , true );

	public final boolean	sortable;
	public final Class<?>	valueClass;

	private ShotPropertyType( Class<?> valueClass , boolean isSortable )
	{
		this.valueClass = valueClass;
		this.sortable = isSortable;
	}
}