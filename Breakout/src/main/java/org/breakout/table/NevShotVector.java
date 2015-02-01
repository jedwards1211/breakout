package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * A north, east, and vertical offset between to survey stations.
 */
public class NevShotVector extends ShotVector
{
	private UnitizedDouble<Length>	northOffset;
	private UnitizedDouble<Length>	eastOffset;
	private UnitizedDouble<Length>	verticalOffset;
	private boolean					downwardIsPositive;

	public NevShotVector( )
	{

	}

	public NevShotVector( UnitizedDouble<Length> northOffset , UnitizedDouble<Length> eastOffset ,
		UnitizedDouble<Length> verticalOffset , boolean downwardIsPositive )
	{
		super( );
		this.northOffset = northOffset;
		this.eastOffset = eastOffset;
		this.verticalOffset = verticalOffset;
		this.downwardIsPositive = downwardIsPositive;
	}

	public UnitizedDouble<Length> getNorthOffset( )
	{
		return northOffset;
	}

	public void setNorthOffset( UnitizedDouble<Length> northOffset )
	{
		this.northOffset = northOffset;
	}

	public UnitizedDouble<Length> getEastOffset( )
	{
		return eastOffset;
	}

	public void setEastOffset( UnitizedDouble<Length> eastOffset )
	{
		this.eastOffset = eastOffset;
	}

	public UnitizedDouble<Length> getVerticalOffset( )
	{
		return verticalOffset;
	}

	public void setVerticalOffset( UnitizedDouble<Length> verticalOffset )
	{
		this.verticalOffset = verticalOffset;
	}

	public boolean isDownwardPositive( )
	{
		return downwardIsPositive;
	}

	public void setDownwardIsPositive( boolean downwardIsPositive )
	{
		this.downwardIsPositive = downwardIsPositive;
	}

	@Override
	public NevShotVector clone( Function<Object, Object> subcloner )
	{
		return new NevShotVector( northOffset , eastOffset , verticalOffset , downwardIsPositive );
	}
}