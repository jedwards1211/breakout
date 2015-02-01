package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * A distance, azimuth, and inclination (DAI) measurement between two survey stations.
 */
public class DaiShotVector extends ShotVector
{
	private UnitizedDouble<Length>	distance;
	private UnitizedDouble<Angle>	frontsightAzimuth;
	private UnitizedDouble<Angle>	backsightAzimuth;
	private UnitizedDouble<Angle>	frontsightInclination;
	private UnitizedDouble<Angle>	backsightInclination;
	private boolean					backsightsAreCorrected;
	private UnitizedDouble<Length>	instrumentHeight;
	private UnitizedDouble<Length>	targetHeight;

	public DaiShotVector( )
	{

	}

	public DaiShotVector( UnitizedDouble<Length> distance , UnitizedDouble<Angle> frontsightAzimuth ,
		UnitizedDouble<Angle> backsightAzimuth ,
		UnitizedDouble<Angle> frontsightInclination , UnitizedDouble<Angle> backsightInclination ,
		boolean backsightsAreCorrected )
	{
		super( );
		this.distance = distance;
		this.frontsightAzimuth = frontsightAzimuth;
		this.backsightAzimuth = backsightAzimuth;
		this.frontsightInclination = frontsightInclination;
		this.backsightInclination = backsightInclination;
		this.backsightsAreCorrected = backsightsAreCorrected;
	}

	public DaiShotVector( UnitizedDouble<Length> distance , UnitizedDouble<Angle> frontsightAzimuth ,
		UnitizedDouble<Angle> backsightAzimuth ,
		UnitizedDouble<Angle> frontsightInclination , UnitizedDouble<Angle> backsightInclination ,
		boolean backsightsAreCorrected , UnitizedDouble<Length> instrumentHeight ,
		UnitizedDouble<Length> targetHeight )
	{
		super( );
		this.distance = distance;
		this.frontsightAzimuth = frontsightAzimuth;
		this.backsightAzimuth = backsightAzimuth;
		this.frontsightInclination = frontsightInclination;
		this.backsightInclination = backsightInclination;
		this.backsightsAreCorrected = backsightsAreCorrected;
		this.instrumentHeight = instrumentHeight;
		this.targetHeight = targetHeight;
	}

	public UnitizedDouble<Length> getDistance( )
	{
		return distance;
	}

	public void setDistance( UnitizedDouble<Length> distance )
	{
		this.distance = distance;
	}

	public UnitizedDouble<Angle> getFrontsightAzimuth( )
	{
		return frontsightAzimuth;
	}

	public void setFrontsightAzimuth( UnitizedDouble<Angle> frontsightAzimuth )
	{
		this.frontsightAzimuth = frontsightAzimuth;
	}

	public UnitizedDouble<Angle> getBacksightAzimuth( )
	{
		return backsightAzimuth;
	}

	public void setBacksightAzimuth( UnitizedDouble<Angle> backsightAzimuth )
	{
		this.backsightAzimuth = backsightAzimuth;
	}

	public UnitizedDouble<Angle> getFrontsightInclination( )
	{
		return frontsightInclination;
	}

	public void setFrontsightInclination( UnitizedDouble<Angle> frontsightInclination )
	{
		this.frontsightInclination = frontsightInclination;
	}

	public UnitizedDouble<Angle> getBacksightInclination( )
	{
		return backsightInclination;
	}

	public void setBacksightInclination( UnitizedDouble<Angle> backsightInclination )
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
	
	public UnitizedDouble<Length> getInstrumentHeight( )
	{
		return instrumentHeight;
	}

	public void setInstrumentHeight( UnitizedDouble<Length> instrumentHeight )
	{
		this.instrumentHeight = instrumentHeight;
	}

	public UnitizedDouble<Length> getTargetHeight( )
	{
		return targetHeight;
	}

	public void setTargetHeight( UnitizedDouble<Length> targetHeight )
	{
		this.targetHeight = targetHeight;
	}

	@Override
	public DaiShotVector clone( Function<Object, Object> subcloner )
	{
		return new DaiShotVector( distance , frontsightAzimuth , backsightAzimuth , frontsightInclination ,
			backsightInclination , backsightsAreCorrected , instrumentHeight , targetHeight );
	}
}