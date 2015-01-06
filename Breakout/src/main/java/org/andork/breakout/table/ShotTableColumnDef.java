package org.andork.breakout.table;

public class ShotTableColumnDef
{
	public final String					name;
	public final ShotTableColumnType	type;

	public ShotTableColumnDef( String name , ShotTableColumnType type )
	{
		super( );
		this.name = name;
		this.type = type;
	}
}
