package org.andork.torquescape.model.index;

public interface IIndexFn
{
	public int getIndexCount( float param );
	
	public char eval( float param , int index );
}
