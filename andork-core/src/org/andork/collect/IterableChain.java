package org.andork.collect;

import java.util.Arrays;
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
	private final Iterable<? extends T>	first;
	private final Iterable<? extends T>	second;
	
	public IterableChain( )
	{
		this( null , null );
	}
	
	public IterableChain( Iterable<? extends T> first )
	{
		this( first , null );
	}
	
	public IterableChain( Iterable<? extends T> first , Iterable<? extends T> second )
	{
		super( );
		if( first == null && second != null )
		{
			first = second;
			second = null;
		}
		this.first = first;
		this.second = second;
	}
	
	public IterableChain<T> add( Iterable<? extends T> next )
	{
		return new IterableChain<T>( this , next );
	}
	
	public Iterator<T> iterator( )
	{
		return new ChainIterator<T>( first == null ? null : first.iterator( ) , second == null ? null : second.iterator( ) );
	}
	
	public static <T> Iterable<T> join( Iterable<? extends Iterable<? extends T>> chain )
	{
		Iterator<? extends Iterable<? extends T>> i = chain.iterator( );
		if( !i.hasNext( ) )
		{
			return new IterableChain<T>( );
		}
		Iterable<? extends T> first = i.next( );
		if( !i.hasNext( ) )
		{
			return new IterableChain<T>( first );
		}
		Iterable<? extends T> second = i.next( );
		
		IterableChain<T> result = new IterableChain<T>( first , second );
		while( i.hasNext( ) )
		{
			result = result.add( i.next( ) );
		}
		return result;
	}
	
	public static <T> Iterable<T> join( Iterable<? extends T> ... chain )
	{
		return join( Arrays.asList( chain ) );
	}
	
	private static class ChainIterator<T> implements Iterator<T>
	{
		private final Iterator<? extends T>	first;
		private final Iterator<? extends T>	second;
		private Iterator<? extends T>		last;
		
		private ChainIterator( )
		{
			this( null , null );
		}
		
		private ChainIterator( Iterator<? extends T> first )
		{
			this( first , null );
		}
		
		private ChainIterator( Iterator<? extends T> first , Iterator<? extends T> second )
		{
			super( );
			if( first == null && second != null )
			{
				first = second;
				second = null;
			}
			this.first = first;
			this.second = second;
		}
		
		public boolean hasNext( )
		{
			return ( first != null && first.hasNext( ) ) || ( second != null && second.hasNext( ) );
		}
		
		public T next( )
		{
			last = first.hasNext( ) || second == null ? first : second;
			return last.next( );
		}
		
		public void remove( )
		{
			last.remove( );
		}
	}
}
