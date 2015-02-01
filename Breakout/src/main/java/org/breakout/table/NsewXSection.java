package org.breakout.table;

import java.util.function.Function;

/**
 * A {@link XSection} with distances to the north, south, east, and west walls from a given station.
 * 
 * @author James
 */
public class NsewXSection extends XSection
{
	private Double	north;
	private Double	south;
	private Double	east;
	private Double	west;

	public Double getNorth( )
	{
		return north;
	}

	public void setNorth( Double north )
	{
		this.north = north;
	}

	public Double getSouth( )
	{
		return south;
	}

	public void setSouth( Double south )
	{
		this.south = south;
	}

	public Double getEast( )
	{
		return east;
	}

	public void setEast( Double east )
	{
		this.east = east;
	}

	public Double getWest( )
	{
		return west;
	}

	public void setWest( Double west )
	{
		this.west = west;
	}

	@Override
	protected NsewXSection baseClone( )
	{
		return new NsewXSection( );
	}

	@Override
	public NsewXSection clone( Function<Object, Object> subcloner )
	{
		NsewXSection result = baseClone( );
		result.north = north;
		result.south = south;
		result.east = east;
		result.west = west;
		return result;
	}
}