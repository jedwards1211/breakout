package org.breakout.table;

import java.util.function.Function;

/**
 * A {@link XSection} with distances to the floor and ceiling from a given station.
 * 
 * @author James
 */
public abstract class UdXSection extends XSection
{
	private Double	up;
	private Double	down;

	public Double getUp( )
	{
		return up;
	}

	public void setUp( Double up )
	{
		this.up = up;
	}

	public Double getDown( )
	{
		return down;
	}

	public void setDown( Double down )
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