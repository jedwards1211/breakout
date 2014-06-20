package org.andork.awt;

import org.andork.func.Bimapper;
import org.andork.swing.FromEDT;

public class EDTBimapper<I, O> extends EDTMapper<I, O> implements Bimapper<I, O>
{
	private Bimapper<I, O>	wrapped;
	
	public EDTBimapper( Bimapper<I, O> wrapped )
	{
		super( wrapped );
		this.wrapped = wrapped;
	}
	
	public static <I, O> EDTBimapper<I, O> newInstance( Bimapper<I, O> wrapped )
	{
		return new EDTBimapper<I, O>( wrapped );
	}
	
	@Override
	public I unmap( final O out )
	{
		return new FromEDT<I>( )
		{
			@Override
			public I run( ) throws Throwable
			{
				return wrapped.unmap( out );
			}
		}.result( );
	}
}
