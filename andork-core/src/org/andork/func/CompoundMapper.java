package org.andork.func;

public class CompoundMapper<I, O> implements Mapper<I, O>
{
	Mapper	m0;
	Mapper	m1;
	
	protected CompoundMapper( Mapper m0 , Mapper m1 )
	{
		this.m0 = m0;
		this.m1 = m1;
	}
	
	public static <I, M, O> CompoundMapper<I, O> compose( Mapper<I, M> m0 , Mapper<M, O> m1 )
	{
		return new CompoundMapper<I, O>( m0 , m1 );
	}
	
	@Override
	public O map( I in )
	{
		return ( O ) m1.map( m0.map( in ) );
	}
	
}
