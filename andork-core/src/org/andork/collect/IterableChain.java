package org.andork.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterable} that concatenates the results from multiple {@code Iterable}s together.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the element type.
 */
public class IterableChain<T> implements Iterable<T>
{
	public IterableChain( Iterable<? extends T> ... chain )
	{
		this( Arrays.asList( chain ) );
	}
	
	public IterableChain( Collection<? extends Iterable<? extends T>> chain )
	{
		this.chain = chain;
	}
	
	private final Iterable<? extends Iterable<? extends T>>	chain;
	
	public Iterator<T> iterator( )
	{
		return new ChainIterator( );
	}
	
	public static <T> Iterable<T> cat( Iterable<? extends T> ... chain )
	{
		return new IterableChain<T>( chain );
	}
	
	private class ChainIterator implements Iterator<T>
	{
		public ChainIterator( )
		{
			chainIter = chain.iterator( );
		}
		
		Iterator<? extends Iterable<? extends T>>	chainIter;
		Iterator<? extends T>						linkIter;
		
		public boolean hasNext( )
		{
			return ( linkIter != null && linkIter.hasNext( ) )
					|| chainIter.hasNext( );
		}
		
		public T next( )
		{
			while( linkIter == null || !linkIter.hasNext( ) )
			{
				linkIter = chainIter.next( ).iterator( );
			}
			return linkIter.next( );
		}
		
		public void remove( )
		{
			linkIter.remove( );
		}
	}
}
