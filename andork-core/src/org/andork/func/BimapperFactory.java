package org.andork.func;

public abstract class BimapperFactory<I, O> implements Bimapper<I, O>
{
	public abstract Bimapper<I, O> newInstance( );
	
	@Override
	public O map( I in )
	{
		return newInstance( ).map( in );
	}
	
	@Override
	public I unmap( O out )
	{
		return newInstance( ).unmap( out );
	}
	
}
