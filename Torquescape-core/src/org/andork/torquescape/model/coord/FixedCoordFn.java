package org.andork.torquescape.model.coord;

public class FixedCoordFn implements ICoordFn
{
	int			coordCount;
	float[ ]	coords;
	
	public FixedCoordFn( float ... coords )
	{
		this.coords = coords;
		this.coordCount = coords.length / 3;
	}
	
	@Override
	public int getCoordCount( float param )
	{
		return coordCount;
	}
	
	@Override
	public void eval( float param , int index , float[ ] result )
	{
		System.arraycopy( coords , index * 3 , result , 0 , 3 );
	}
}
