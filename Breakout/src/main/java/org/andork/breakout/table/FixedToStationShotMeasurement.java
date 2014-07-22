package org.andork.breakout.table;

public class FixedToStationShotMeasurement implements ShotMeasurement
{
	public final double[ ]	location;
	
	public FixedToStationShotMeasurement( double x , double y , double z )
	{
		super( );
		location = new double[ ] { x , y , z };
	}
}
