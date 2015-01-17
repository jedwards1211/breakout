package org.andork.breakout.table;

import org.andork.q2.QArrayObject;
import org.andork.q2.QObject;
import org.andork.q2.QSpec;

public class SurveyModel extends QSpec
{
	public static NonNullProperty<QObject<DataDefaults>>	defaults	= new NonNullProperty<>(
																			"defaults" ,
																			QObject.class ,
																			( ) -> QArrayObject
																				.create( DataDefaults.spec ) );

	public static Property<SurveyDataList<Shot>>			shotList	= property( "shotList" ,
																			SurveyDataList.class );

	public static Property<SurveyDataList<Station>>			stationList	= property( "stationList" ,
																			SurveyDataList.class );

	public static final SurveyModel							spec		= new SurveyModel( );

	private SurveyModel( )
	{
		super( defaults ,
			shotList ,
			stationList );
	}
}
