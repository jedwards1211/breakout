package org.andork.torquescape.model.coord;

public interface ICoordFn
{
	public int getCoordCount( float param );
	
	public void eval( float param , int index , float[ ] result );
}
