package org.andork.breakout.table;

import java.util.function.Function;

/**
 * A {@link XSection} with distances to the floor, ceiling, left wall, and right wall from a given station.
 * 
 * @author James
 */
public class LrudXSection extends UdXSection
{
	/**
	 * Identifies the angle of an {@link LrudXSection} relative to its {@link Shot}.
	 * 
	 * @author James
	 */
	public static enum Angle
	{
		/**
		 * Indicates an {@link LrudXSection} is perpendicular to its {@link Shot}.
		 */
		PERPENDICULAR,
		/**
		 * Indicates an {@link LrudXSection} bisects the angle between two {@link Shot}s.
		 */
		BISECTOR;
	}

	private Double				left;
	private Double				right;
	private LrudXSection.Angle	angle;

	public Double getLeft( )
	{
		return left;
	}

	public void setLeft( Double left )
	{
		this.left = left;
	}

	public Double getRight( )
	{
		return right;
	}

	public void setRight( Double right )
	{
		this.right = right;
	}

	public LrudXSection.Angle getAngle( )
	{
		return angle;
	}

	public void setAngle( LrudXSection.Angle angle )
	{
		this.angle = angle;
	}

	@Override
	protected LrudXSection baseClone( )
	{
		return new LrudXSection( );
	}

	@Override
	public LrudXSection clone( Function<Object, Object> subcloner )
	{
		LrudXSection result = ( LrudXSection ) super.clone( subcloner );
		result.left = left;
		result.right = right;
		result.angle = angle;
		return result;
	}
}