package org.andork.torquescape.model2.meshing;

public class FixedMeshingFunction implements IMeshingFunction
{
	int[ ]	indices;
	
	public FixedMeshingFunction( int[ ] indices )
	{
		super( );
		this.indices = indices;
	}

	@Override
	public void eval( float param , IIntVisitor indexVisitor )
	{
		for( int i : indices )
		{
			indexVisitor.visit( i );
		}
	}
}
