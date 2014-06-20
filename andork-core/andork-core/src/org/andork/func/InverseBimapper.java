package org.andork.func;

public class InverseBimapper<O, I> implements Bimapper<O, I>
{
	Bimapper<I, O>	bimapper;
	
	private InverseBimapper( Bimapper<I, O> bimapper )
	{
		this.bimapper = bimapper;
	}
	
	public static <O, I> InverseBimapper<O, I> inverse( Bimapper<I, O> bimapper )
	{
		return new InverseBimapper<O, I>( bimapper );
	}
	
	@Override
	public I map( O in )
	{
		return bimapper.unmap( in );
	}
	
	@Override
	public O unmap( I out )
	{
		return bimapper.map( out );
	}
}
