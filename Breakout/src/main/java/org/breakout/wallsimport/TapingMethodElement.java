package org.breakout.wallsimport;

public enum TapingMethodElement
{
	INSTRUMENT_HEIGHT
	{
		@Override
		public void visit( TapingMethodElementVisitor visitor )
		{
			visitor.visitInstrumentHeight( );
		}
	},
	TARGET_HEIGHT
	{
		@Override
		public void visit( TapingMethodElementVisitor visitor )
		{
			visitor.visitTargetHeight( );
		}
	};

	public abstract void visit( TapingMethodElementVisitor visitor );
}
