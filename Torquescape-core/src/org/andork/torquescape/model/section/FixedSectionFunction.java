package org.andork.torquescape.model.section;

public class FixedSectionFunction implements ISectionFunction
{
	float[ ]	points;
	
	public FixedSectionFunction( float[ ] points )
	{
		super( );
		this.points = points;
	}
	
	@Override
	public void eval( float param , IPointVisitor sectionPointVisitor )
	{
		int i = 0;
		while( i < points.length )
		{
			sectionPointVisitor.visit( points[ i++ ] , points[ i++ ] , points[ i++ ] );
		}
	}
}
