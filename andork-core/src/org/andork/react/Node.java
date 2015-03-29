package org.andork.react;

import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Node<T>
{
	private final CopyOnWriteArraySet<Rxn<?>> bindings = new CopyOnWriteArraySet<>( );

	public void bind( Rxn<?> reaction )
	{
		if( bindings.add( reaction ) )
		{
			reaction.invalidate( );
		}
	}

	public void unbind( Rxn<?> reaction )
	{
		if( bindings.remove( reaction ) )
		{
			reaction.invalidate( );
		}
	}

	protected void fireValueChanged( )
	{
		for( Rxn<?> binding : bindings )
		{
			binding.invalidate( );
		}
	}

	public abstract T get( );
}
