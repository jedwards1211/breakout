package org.andork.torquescape.model.meshing;

public interface IMeshingFn
{
	public int getIndexCount( float param );
	
	public char eval( float param , int index );
}
