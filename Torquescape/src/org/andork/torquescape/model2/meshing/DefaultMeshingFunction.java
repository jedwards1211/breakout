package org.andork.torquescape.model2.meshing;

public class DefaultMeshingFunction implements IMeshingFunction
{
	int	sectionPointCount;
	
	public DefaultMeshingFunction( int sectionPointCount )
	{
		super( );
		this.sectionPointCount = sectionPointCount;
	}

	@Override
	public void eval( float param , IIntVisitor indexVisitor )
	{
		for( int i = 0 ; i < sectionPointCount ; i++ )
		{
			indexVisitor.visit( i );
			indexVisitor.visit( ( i + 1 ) % sectionPointCount );
			indexVisitor.visit( i + sectionPointCount );
			indexVisitor.visit( ( i + 1 ) % sectionPointCount + sectionPointCount );
			indexVisitor.visit( i + sectionPointCount );
			indexVisitor.visit( ( i + 1 ) % sectionPointCount );
		}
	}
}
