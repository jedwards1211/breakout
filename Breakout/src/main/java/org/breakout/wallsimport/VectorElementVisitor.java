package org.breakout.wallsimport;

public interface VectorElementVisitor
{
	public void visitDistance( );

	public void visitAzimuth( );

	public void visitInclination( );

	public void visitEast( );

	public void visitNorth( );

	public void visitRectUp( );
}
