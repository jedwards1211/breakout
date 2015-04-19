package org.breakout.wallsimport;

public enum RectElement
{
	E
	{
		@Override
		public void visit( RectElementVisitor visitor )
		{
			visitor.visitEast( );
		}
	},
	N
	{
		@Override
		public void visit( RectElementVisitor visitor )
		{
			visitor.visitNorth( );
		}
	},
	U
	{
		@Override
		public void visit( RectElementVisitor visitor )
		{
			visitor.visitRectUp( );
		}
	};

	abstract void visit( RectElementVisitor visitor );
}
