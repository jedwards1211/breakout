package org.andork.bind;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Binder<T>
{
	private final LinkedList<Binder<?>>	downstream				= new LinkedList<Binder<?>>( );
	private final List<Binder<?>>		unmodifiableDownstream	= Collections.unmodifiableList( downstream );
	
	public abstract T get( );
	
	public abstract void set( T newValue );
	
	public List<Binder<?>> getDownstream( )
	{
		return unmodifiableDownstream;
	}
	
	public abstract void update( boolean force );
	
	protected static void bind( Binder<?> upstream , Binder<?> downstream )
	{
		if( !upstream.downstream.contains( downstream ) )
		{
			upstream.downstream.add( downstream );
		}
	}
	
	protected static void unbind( Binder<?> upstream , Binder<?> downstream )
	{
		upstream.downstream.remove( downstream );
	}
	
	protected final void updateDownstream( boolean force )
	{
		for( Binder<?> binder : downstream )
		{
			binder.update( force );
		}
	}
}
