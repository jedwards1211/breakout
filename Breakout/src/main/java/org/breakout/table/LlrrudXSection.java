package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

/**
 * A {@link XSection} with distances to the floor, ceiling, and north/east offsets of the left and right walls
 * from a given station.
 * 
 * @author James
 */
public class LlrrudXSection extends UdXSection
{
	private UnitizedDouble<Length>	leftNorthing;
	private UnitizedDouble<Length>	leftEasting;
	private UnitizedDouble<Length>	rightNorthing;
	private UnitizedDouble<Length>	rightEasting;

	public UnitizedDouble<Length> getLeftNorthing( )
	{
		return leftNorthing;
	}

	public void setLeftNorthing( UnitizedDouble<Length> leftNorthing )
	{
		this.leftNorthing = leftNorthing;
	}

	public UnitizedDouble<Length> getLeftEasting( )
	{
		return leftEasting;
	}

	public void setLeftEasting( UnitizedDouble<Length> leftEasting )
	{
		this.leftEasting = leftEasting;
	}

	public UnitizedDouble<Length> getRightNorthing( )
	{
		return rightNorthing;
	}

	public void setRightNorthing( UnitizedDouble<Length> rightNorthing )
	{
		this.rightNorthing = rightNorthing;
	}

	public UnitizedDouble<Length> getRightEasting( )
	{
		return rightEasting;
	}

	public void setRightEasting( UnitizedDouble<Length> rightEasting )
	{
		this.rightEasting = rightEasting;
	}

	@Override
	protected LlrrudXSection baseClone( )
	{
		return new LlrrudXSection( );
	}

	@Override
	public LlrrudXSection clone( Function<Object, Object> subcloner )
	{
		LlrrudXSection result = ( LlrrudXSection ) super.clone( subcloner );
		result.leftNorthing = leftNorthing;
		result.leftEasting = leftEasting;
		result.rightNorthing = rightNorthing;
		result.rightEasting = rightEasting;
		return result;
	}
}