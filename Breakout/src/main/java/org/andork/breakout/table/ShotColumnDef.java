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

	public boolean equals( Object o )
	{
		if( o instanceof ShotColumnDef )
		{
			ShotColumnDef od = ( ShotColumnDef ) o;
			return name.equals( od.name ) && type.equals( od.type );
		}
		return false;
	}
}
