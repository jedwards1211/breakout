package org.breakout.table;

import java.util.function.Function;

import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;
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
	public static class XAngle
	{
		/**
		 * Indicates an {@link LrudXSection} is perpendicular to its {@link Shot}.
		 */
		public static final XAngle	PERPENDICULAR	= new XAngle( );
		/**
		 * Indicates an {@link LrudXSection} bisects the angle between two {@link Shot}s.
		 */
		public static final XAngle	BISECTOR		= new XAngle( );

		private XAngle( )
		{

		}
	}

	public static class FacingAzimuth extends XAngle implements PowerCloneable
	{
		private UnitizedDouble<Angle>	azimuth;

		public FacingAzimuth( )
		{

		}

		public FacingAzimuth( UnitizedDouble<Angle> azimuth )
		{
			super( );
			this.azimuth = azimuth;
		}

		public UnitizedDouble<Angle> getAzimuth( )
		{
			return azimuth;
		}

		public void setAzimuth( UnitizedDouble<Angle> azimuth )
		{
			this.azimuth = azimuth;
		}

		@Override
		public PowerCloneable clone( Function<Object, Object> subcloner )
		{
			return new FacingAzimuth( azimuth );
		}
	}

	private UnitizedDouble<Length>				left;
	private UnitizedDouble<Length>				right;
	private LrudXSection.XAngle	angle;

	public UnitizedDouble<Length> getLeft( )
	{
		return left;
	}

	public void setLeft( UnitizedDouble<Length> left )
	{
		this.left = left;
	}

	public UnitizedDouble<Length> getRight( )
	{
		return right;
	}

	public void setRight( UnitizedDouble<Length> right )
	{
		this.right = right;
	}

	public LrudXSection.XAngle getAngle( )
	{
		return angle;
	}

	public void setAngle( LrudXSection.XAngle angle )
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
		result.angle = ( XAngle ) subcloner.apply( angle );
		return result;
	}
}