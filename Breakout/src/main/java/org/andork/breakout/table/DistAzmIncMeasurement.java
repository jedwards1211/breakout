package org.andork.breakout.table;

public class DistAzmIncMeasurement implements ShotMeasurement
{
	public final double	distance;
	public final Double	frontsightAzimuth;
	public final Double	backsightAzimuth;
	public final Double	frontsightInclination;
	public final Double	backsightInclination;
	
	public DistAzmIncMeasurement( double distance , Double frontsightAzimuth , Double backsightAzimuth , Double frontsightInclination , Double backsightInclination )
	{
		super( );
		this.distance = distance;
		this.frontsightAzimuth = frontsightAzimuth;
		this.backsightAzimuth = backsightAzimuth;
		this.frontsightInclination = frontsightInclination;
		this.backsightInclination = backsightInclination;
	}
	
	public DistAzmIncMeasurement( double distance , Double[ ] azimuth , Double[ ] inclination )
	{
		this.distance = distance;
		frontsightAzimuth = azimuth == null ? null : azimuth[ 0 ];
		backsightAzimuth = azimuth == null || azimuth.length < 2 ? null : azimuth[ 1 ];
		frontsightInclination = inclination[ 0 ];
		backsightInclination = inclination.length > 1 ? inclination[ 1 ] : null;
	}
}
