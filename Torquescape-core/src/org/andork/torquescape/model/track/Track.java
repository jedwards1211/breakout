package org.andork.torquescape.model.track;

import org.andork.torquescape.model.coord.ICoordFn;
import org.andork.torquescape.model.index.IIndexFn;

public class Track
{
	protected ICoordFn		coordFn;
	protected IIndexFn		indexFn;
	
	public ICoordFn getCoordFn( )
	{
		return coordFn;
	}
	
	public void setCoordFn( ICoordFn coordFn )
	{
		this.coordFn = coordFn;
	}
	
	public IIndexFn getIndexFn( )
	{
		return indexFn;
	}
	
	public void setIndexFn( IIndexFn indexFn )
	{
		this.indexFn = indexFn;
	}
}
