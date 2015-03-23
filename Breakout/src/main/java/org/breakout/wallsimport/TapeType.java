package org.breakout.wallsimport;

public enum TapeType
{
	/**
	 * Instrument-to-target
	 */
	IT
	{
		@Override
		public void visit( TapeTypeVisitor visitor )
		{
			visitor.visitIT( );
		}
	},
	/**
	 * Station-to-station
	 */
	SS
	{
		@Override
		public void visit( TapeTypeVisitor visitor )
		{
			visitor.visitSS( );
		}
	},
	/**
	 * Instrument-to-station
	 */
	IS
	{
		@Override
		public void visit( TapeTypeVisitor visitor )
		{
			visitor.visitIS( );
		}
	},
	/**
	 * Station-to-target
	 */
	ST
	{
		@Override
		public void visit( TapeTypeVisitor visitor )
		{
			visitor.visitST( );
		}
	};

	public abstract void visit( TapeTypeVisitor visitor );
}