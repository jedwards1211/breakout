package org.breakout.wallsimport;

public enum CaseType
{
	/**
	 * Convert station names to uppercase.
	 */
	Upper
	{
		@Override
		public String apply( String s )
		{
			return s.toUpperCase( );
		}
	},
	/**
	 * Convert station names to lowercase.
	 */
	Lower
	{
		@Override
		public String apply( String s )
		{
			return s.toLowerCase( );
		}
	},
	/**
	 * Preserve station name case.
	 */
	Mixed
	{
		@Override
		public String apply( String s )
		{
			return s;
		}
	};

	public abstract String apply( String s );
}