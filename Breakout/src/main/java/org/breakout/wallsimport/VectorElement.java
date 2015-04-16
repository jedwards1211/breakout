package org.breakout.wallsimport;

public enum VectorElement
{
	D
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitDistance( );
		}
	},
	A
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitAzimuth( );
		}
	},
	V
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitInclination( );
		}
	},
	E
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitEast( );
		}
	},
	N
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitNorth( );
		}
	},
	U
	{
		@Override
		public void visit( VectorElementVisitor visitor )
		{
			visitor.visitRectUp( );
		}
	};

	abstract void visit( VectorElementVisitor visitor );
}
