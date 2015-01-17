package org.andork.breakout.table;

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

	public static final SurveyDataColumnDef	fromStationName	= new SurveyDataColumnDef( "from" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	toStationName	= new SurveyDataColumnDef( "to" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	vector			= new SurveyDataColumnDef( "vector" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	xSectionAtFrom	= new SurveyDataColumnDef( "fromXsect" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	xSectionAtTo	= new SurveyDataColumnDef( "toXsect" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	lengthUnit		= new SurveyDataColumnDef( "lengthUnit" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	angleUnit		= new SurveyDataColumnDef( "angleUnit" ,
																SurveyDataColumnType.BUILTIN );
}
