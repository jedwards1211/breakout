package org.andork.torquescape.model.index;

public class FixedIndexFn implements IIndexFn
{
	char[ ]	indices;
	
	public FixedIndexFn( char ... indices )
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
