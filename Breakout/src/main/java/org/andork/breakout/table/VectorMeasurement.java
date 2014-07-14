package org.andork.breakout.table;

public class VectorMeasurement implements ShotMeasurement
{
	public final double[ ]	vector;
	
	public VectorMeasurement( double x , double y , double z )
	{
		super( );
		vector = new double[ ] { x , y , z };
	}
}
