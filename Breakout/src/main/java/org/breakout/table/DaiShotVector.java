package org.breakout.table;

import java.util.function.Function;

/**
 * A distance, azimuth, and inclination (DAI) measurement between two survey stations.
 */
public class DaiShotVector extends ShotVector
{
	private Double	distance;
	private Double	frontsightAzimuth;
	private Double	backsightAzimuth;
	private Double	frontsightInclination;
	private Double	backsightInclination;
	private boolean	backsightsAreCorrected;

	public DaiShotVector( )
	{

	}

	public DaiShotVector( Double distance , Double frontsightAzimuth , Double backsightAzimuth ,
		Double frontsightInclination , Double backsightInclination , boolean backsightsAreCorrected )
	{
		super( );
		this.distance = distance;
		this.frontsightAzimuth = frontsightAzimuth;
		this.backsightAzimuth = backsightAzimuth;
		this.frontsightInclination = frontsightInclination;
		this.backsightInclination = backsightInclination;
		this.backsightsAreCorrected = backsightsAreCorrected;
	}

	public Double getDistance( )
	{
		return distance;
	}

	public void setDistance( Double distance )
	{
		this.distance = distance;
	}

	public Double getFrontsightAzimuth( )
	{
		return frontsightAzimuth;
	}

	public void setFrontsightAzimuth( Double frontsightAzimuth )
	{
		this.frontsightAzimuth = frontsightAzimuth;
	}

	public Double getBacksightAzimuth( )
	{
		return backsightAzimuth;
	}

	public void setBacksightAzimuth( Double backsightAzimuth )
	{
		this.backsightAzimuth = backsightAzimuth;
	}

	public Double getFrontsightInclination( )
	{
		return frontsightInclination;
	}

	public void setFrontsightInclination( Double frontsightInclination )
	{
		this.frontsightInclination = frontsightInclination;
	}

	public Double getBacksightInclination( )
	{
		return backsightInclination;
	}

	public void setBacksightInclination( Double backsightInclination )
	{
		this.backsightInclination = backsightInclination;
	}

	public boolean areBacksightsCorrected( )
	{
		return backsightsAreCorrected;
	}

	public void setBacksightsAreCorrected( boolean backsightsAreCorrected )
	{
		this.backsightsAreCorrected = backsightsAreCorrected;
	}

	@Override
	public DaiShotVector clone( Function<Object, Object> subcloner )
	{
		return new DaiShotVector( distance , frontsightAzimuth , backsightAzimuth , frontsightInclination ,
			backsightInclination , backsightsAreCorrected );
	}
}