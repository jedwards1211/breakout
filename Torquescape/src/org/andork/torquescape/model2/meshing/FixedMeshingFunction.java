package org.andork.torquescape.model2.meshing;

public class FixedMeshingFunction implements IMeshingFunction
{
	char[ ]	indices;
	
	public FixedMeshingFunction( char[ ] indices )
	{
		super( );
		this.indices = indices;
	}

	@Override
	public void eval( float param , IIndexVisitor indexVisitor )
	{
		for( char i : indices )
		{
			indexVisitor.visit( i );
		}
	}
}
