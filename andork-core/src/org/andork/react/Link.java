package org.andork.react;

/**
 * Contains the boilerplate code for changing the {@link Node} a {@link Rxn} is bound to.
 * 
 * @author andy.edwards
 * @param <T>
 *            the value type.
 */
public class Link<T>
{
	private Node<? extends T> node;
	private final Rxn<?> rxn;

	public Link( Rxn<?> rxn )
	{
		super( );
		this.rxn = rxn;
	}

	public Node<? extends T> node( )
	{
		return node;
	}

	public T get( )
	{
		return node == null ? null : node.get( );
	}

	public <B extends Node<? extends T>> B bind( B node )
	{
		if( this.node != node )
		{
			if( this.node != null )
			{
				this.node.unbind( rxn );
			}
			this.node = node;
			if( node != null )
			{
				node.bind( rxn );
			}
		}
		return node;
	}

	public void unbind( )
	{
		bind( null );
	}
}
