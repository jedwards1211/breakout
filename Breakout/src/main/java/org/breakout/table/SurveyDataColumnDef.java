package org.breakout.table;

public class SurveyDataColumnDef
{
	public final String					name;
	public final SurveyDataColumnType	type;

	public SurveyDataColumnDef( String name , SurveyDataColumnType type )
	{
		super( );
		this.name = name;
		this.type = type;
	}

	public boolean equals( Object o )
	{
		if( o instanceof SurveyDataColumnDef )
		{
			SurveyDataColumnDef od = ( SurveyDataColumnDef ) o;
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
}
