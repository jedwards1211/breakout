package org.andork.func;

import java.util.IdentityHashMap;

/**
 * A {@link Bimapper} whose {@link #map(Object)} and {@link #unmap(Object)} methods will return the same values for the same parameters, even if the wrapped
 * {@link Bimapper} returns different (but equal) values for the same parameters.
 * 
 * @author andy.edwards
 */
public class IdentityHashMapBimapper<I, O> extends IdentityHashMapMapper<I, O> implements Bimapper<I, O>
{
	private final Bimapper<I, O>		wrapped;
	private final IdentityHashMap<O, I>	outToIn	= new IdentityHashMap<O, I>( );
	
	public IdentityHashMapBimapper( Bimapper<I, O> wrapped )
	{
		super( wrapped );
		this.wrapped = wrapped;
	}
	
	@Override
	public I unmap( O out )
	{
		I result = outToIn.get( out );
		if( result == null )
		{
			outToIn.put( out , result = wrapped.unmap( out ) );
		}
		return result;
	}
}
