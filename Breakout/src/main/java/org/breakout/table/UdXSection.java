package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * A {@link XSection} with distances to the floor and ceiling from a given station.
 * 
 * @author James
 */
public abstract class UdXSection extends XSection
{
	private UnitizedDouble<Length>	up;
	private UnitizedDouble<Length>	down;

	public UnitizedDouble<Length> getUp( )
	{
		return up;
	}

	public void setUp( UnitizedDouble<Length> up )
	{
		this.up = up;
	}

	public UnitizedDouble<Length> getDown( )
	{
		return down;
	}

	public void setDown( UnitizedDouble<Length> down )
	{
		this.down = down;
	}

	protected abstract UdXSection baseClone( );

	@Override
	public UdXSection clone( Function<Object, Object> subcloner )
	{
		UdXSection result = baseClone( );
		result.up = up;
		result.down = down;
		return result;
	}
}