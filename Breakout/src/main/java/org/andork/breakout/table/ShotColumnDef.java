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

	public int hashCode( )
	{
		return ( name.hashCode( ) * 23 ) ^ type.hashCode( );
	}

	public String toString( )
	{
		return name;
	}

	public static final ShotColumnDef	fromStationName	= new ShotColumnDef( "from" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	toStationName	= new ShotColumnDef( "to" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	vector			= new ShotColumnDef( "vector" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	xSectionAtFrom	= new ShotColumnDef( "fromXsect" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	xSectionAtTo	= new ShotColumnDef( "toXsect" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	lengthUnit		= new ShotColumnDef( "lengthUnit" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	angleUnit		= new ShotColumnDef( "angleUnit" , ShotColumnType.BUILTIN );
}
