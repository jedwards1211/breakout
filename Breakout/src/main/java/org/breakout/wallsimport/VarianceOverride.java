package org.breakout.wallsimport;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public abstract class VarianceOverride
{
	public static final VarianceOverride FLOATED = new VarianceOverride( ) {};
	public static final VarianceOverride FLOATED_TRAVERSE = new VarianceOverride( ) {};

	public static final class LengthOverride
	{
		public final UnitizedDouble<Length> lengthOverride;

		public LengthOverride( UnitizedDouble<Length> lengthOverride )
		{
			this.lengthOverride = lengthOverride;
		}
	}

	public static final class RMSError
	{
		public final UnitizedDouble<Length> error;

		public RMSError( UnitizedDouble<Length> error )
		{
			this.error = error;
		}
	}
}
