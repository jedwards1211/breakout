package org.andork.torquescape.model.meshing;

public class FixedMeshingFn implements IMeshingFn
{
	char[ ]	indices;
	
	public FixedMeshingFn( char ... indices )
	{
		this.indices = indices;
	}
	
	@Override
	public int getIndexCount( float param )
	{
		return indices.length;
	}
	
	@Override
	public char eval( float param , int index )
	{
		return indices[ index ];
	}
}
