package org.andork.breakout.table;

import java.util.function.Function;

/**
 * A north, east, and vertical offset between to survey stations.
 */
public class NevShotVector extends ShotVector
{
	private Double	northOffset;
	private Double	eastOffset;
	private Double	verticalOffset;
	private boolean	downwardIsPositive;

	public NevShotVector( )
	{

	}

	public NevShotVector( Double northOffset , Double eastOffset , Double verticalOffset , boolean downwardIsPositive )
	{
		super( );
		this.northOffset = northOffset;
		this.eastOffset = eastOffset;
		this.verticalOffset = verticalOffset;
		this.downwardIsPositive = downwardIsPositive;
	}

	public Double getNorthOffset( )
	{
		return northOffset;
	}

	public void setNorthOffset( Double northOffset )
	{
		this.northOffset = northOffset;
	}

	public Double getEastOffset( )
	{
		return eastOffset;
	}

	public void setEastOffset( Double eastOffset )
	{
		this.eastOffset = eastOffset;
	}

	public Double getVerticalOffset( )
	{
		return verticalOffset;
	}

	public void setVerticalOffset( Double verticalOffset )
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