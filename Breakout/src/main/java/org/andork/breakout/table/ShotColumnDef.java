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

	public static final ShotColumnDef	from	= new ShotColumnDef( "from" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	to		= new ShotColumnDef( "to" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	vector	= new ShotColumnDef( "vector" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	dist	= new ShotColumnDef( "dist" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	azmFsBs	= new ShotColumnDef( "azmFsBs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	azmFs	= new ShotColumnDef( "azmFs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	azmBs	= new ShotColumnDef( "azmBs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	incFsBs	= new ShotColumnDef( "incFsBs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	incFs	= new ShotColumnDef( "incFs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	incBs	= new ShotColumnDef( "incBs" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	offsN	= new ShotColumnDef( "offsN" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	offsE	= new ShotColumnDef( "offsE" , ShotColumnType.BUILTIN );
	public static final ShotColumnDef	offsV	= new ShotColumnDef( "offsV" , ShotColumnType.BUILTIN );

}
