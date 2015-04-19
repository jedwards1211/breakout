package org.breakout.wallsimport;

public enum CtElement
{
	D
	{
		@Override
		public void visit( CtElementVisitor visitor )
		{
			visitor.visitDistance( );
		}
	},
	A
	{
		@Override
		public void visit( CtElementVisitor visitor )
		{
			visitor.visitAzimuth( );
		}
	},
	V
	{
		@Override
		public void visit( CtElementVisitor visitor )
		{
			visitor.visitInclination( );
		}
	};

	abstract void visit( CtElementVisitor visitor );
}
