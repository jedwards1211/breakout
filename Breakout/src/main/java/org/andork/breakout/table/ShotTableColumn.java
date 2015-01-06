package org.andork.breakout.table;

public class ShotTableColumn
{
	public final String					name;
	public final ShotTableColumnType	type;

	public ShotTableColumn( String name , ShotTableColumnType type )
	{
		super( );
		this.name = name;
		this.type = type;
	}
}
