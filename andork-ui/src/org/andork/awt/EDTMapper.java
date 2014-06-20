package org.andork.awt;

import org.andork.func.Mapper;
import org.andork.swing.FromEDT;

public class EDTMapper<I, O> implements Mapper<I, O>
{
	private Mapper<I, O>	wrapped;
	
	public EDTMapper( Mapper<I, O> wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	@Override
	public O map( final I in )
	{
		return new FromEDT<O>( )
		{
			@Override
			public O run( ) throws Throwable
			{
				return wrapped.map( in );
			}
		}.result( );
	}
}
