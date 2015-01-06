package org.andork.breakout.table;

public class ShotColumnDef
{
	public final String			name;
	public final ShotColumnType	type;

	public ShotColumnDef( String name , ShotColumnType type )
	{
		super( );
		this.name = name;
		this.type = type;
	}
}
