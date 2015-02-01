package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * A {@link XSection} with distances to the north, south, east, and west walls from a given station.
 * 
 * @author James
 */
public class NsewXSection extends XSection
{
	private UnitizedDouble<Length>	north;
	private UnitizedDouble<Length>	south;
	private UnitizedDouble<Length>	east;
	private UnitizedDouble<Length>	west;

	public UnitizedDouble<Length> getNorth( )
	{
		return north;
	}

	public void setNorth( UnitizedDouble<Length> north )
	{
		this.north = north;
	}

	public UnitizedDouble<Length> getSouth( )
	{
		return south;
	}

	public void setSouth( UnitizedDouble<Length> south )
	{
		this.south = south;
	}

	public UnitizedDouble<Length> getEast( )
	{
		return east;
	}

	public void setEast( UnitizedDouble<Length> east )
	{
		this.east = east;
	}

	public UnitizedDouble<Length> getWest( )
	{
		return west;
	}

	public void setWest( UnitizedDouble<Length> west )
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