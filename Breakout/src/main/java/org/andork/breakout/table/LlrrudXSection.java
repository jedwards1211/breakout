package org.andork.breakout.table;

import java.util.function.Function;

/**
 * A {@link XSection} with distances to the floor, ceiling, and north/east offsets of the left and right walls
 * from a given station.
 * 
 * @author James
 */
public class LlrrudXSection extends UdXSection
{
	private Double	leftNorthing;
	private Double	leftEasting;
	private Double	rightNorthing;
	private Double	rightEasting;

	public Double getLeftNorthing( )
	{
		return leftNorthing;
	}

	public void setLeftNorthing( Double leftNorthing )
	{
		this.leftNorthing = leftNorthing;
	}

	public Double getLeftEasting( )
	{
		return leftEasting;
	}

	public void setLeftEasting( Double leftEasting )
	{
		this.leftEasting = leftEasting;
	}

	public Double getRightNorthing( )
	{
		return rightNorthing;
	}

	public void setRightNorthing( Double rightNorthing )
	{
		this.rightNorthing = rightNorthing;
	}

	public Double getRightEasting( )
	{
		return rightEasting;
	}

	public void setRightEasting( Double rightEasting )
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