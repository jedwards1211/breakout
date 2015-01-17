package org.andork.breakout.table;

public class ShotColumnDefs
{

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
	public static final SurveyDataColumnDef	frontsightUnit	= new SurveyDataColumnDef( "frontsightUnit" ,
																SurveyDataColumnType.BUILTIN );
	public static final SurveyDataColumnDef	backsightUnit	= new SurveyDataColumnDef( "backsightUnit" ,
																SurveyDataColumnType.BUILTIN );

}
