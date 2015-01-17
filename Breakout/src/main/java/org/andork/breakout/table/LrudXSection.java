package org.andork.breakout.table;

import java.util.function.Function;

import org.andork.util.PowerCloneable;

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
	public static class Angle
	{
		/**
		 * Indicates an {@link LrudXSection} is perpendicular to its {@link Shot}.
		 */
		public static final Angle	PERPENDICULAR	= new Angle( );
		/**
		 * Indicates an {@link LrudXSection} bisects the angle between two {@link Shot}s.
		 */
		public static final Angle	BISECTOR		= new Angle( );

		private Angle( )
		{

		}
	}

	public static class FacingAzimuth extends Angle implements PowerCloneable
	{
		private Double	azimuth;

		public FacingAzimuth( )
		{

		}

		public FacingAzimuth( Double azimuth )
		{
			super( );
			this.azimuth = azimuth;
		}

		public Double getAzimuth( )
		{
			return azimuth;
		}

		public void setAzimuth( Double azimuth )
		{
			this.azimuth = azimuth;
		}

		@Override
		public PowerCloneable clone( Function<Object, Object> subcloner )
		{
			return new FacingAzimuth( azimuth );
		}
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
		result.angle = ( Angle ) subcloner.apply( angle );
		return result;
	}
}