package org.breakout.wallsimport;

import org.andork.unit.Length;
import org.andork.unit.UnitizedDouble;

public abstract class VarianceOverride
{
	public static final VarianceOverride FLOATED = new VarianceOverride( ) {
		public String toString( )
		{
			return "?";
		}
	};
	public static final VarianceOverride FLOATED_TRAVERSE = new VarianceOverride( ) {
		public String toString( )
		{
			return "*";
		}
	};

	public static final class LengthOverride extends VarianceOverride
	{
		public final UnitizedDouble<Length> lengthOverride;

		public LengthOverride( UnitizedDouble<Length> lengthOverride )
		{
			this.lengthOverride = lengthOverride;
		}

		public String toString( )
		{
			return lengthOverride.toString( );
		}
	}

	public static final class RMSError extends VarianceOverride
	{
		public final UnitizedDouble<Length> error;

		public RMSError( UnitizedDouble<Length> error )
		{
			this.error = error;
		}

		public String toString( )
		{
			return "R" + error.toString( );
		}
	}
}
