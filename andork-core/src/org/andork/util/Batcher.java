package org.andork.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Controls the handling of events on a separate thread. When an event is added, if no events are currently being handled, {@code Batcher} will tell the
 * implementation to {@linkplain #handleLater(List) handle} it. Otherwise, {@code Batcher} will queue up events until the implementation is
 * {@linkplain #doneHandling(List) done} handling the last batch, at which point it will tell the implementation to handle the new batch.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the event type.
 */
public abstract class Batcher<T>
{
	private final Object	lock	= new Object( );
	private LinkedList<T>	queue	= new LinkedList<T>( );
	private int				maxSize	= Integer.MAX_VALUE;
	private boolean			busy;
	
	/**
	 * Calls {@link #handleLater(List)} if the implementation has called {@link #doneHandling(List)} since the last call to {@link #handleLater(List)}.
	 * Otherwise, adds the event to an internal queue.
	 * 
	 * @param t
	 *            the event to add.
	 */
	public final void add( T t )
	{
		LinkedList<T> queueToHandle = null;
		synchronized( lock )
		{
			if( queue.size( ) < maxSize )
			{
				queue.add( t );
				if( !busy )
				{
					busy = true;
					queueToHandle = this.queue;
					this.queue = new LinkedList<T>( );
				}
			}
		}
		if( queueToHandle != null )
		{
			handleLater( queueToHandle );
		}
		else
		{
			eventQueued( t );
		}
	}
	
	/**
	 * The implementation should call this method when it is done handling a set of events {@link Batcher} gave it by calling {@link #handleLater(List)}.
	 * 
	 * @param batch
	 *            the batch that the implementation is done handling
	 */
	public final void doneHandling( List<T> batch )
	{
		LinkedList<T> queueToHandle = null;
		synchronized( lock )
		{
			if( !busy )
			{
				throw new IllegalStateException( "operation only allowed when this Batcher is marked busy" );
			}
			if( queue.isEmpty( ) )
			{
				busy = false;
			}
			else
			{
				queueToHandle = this.queue;
				this.queue = new LinkedList<T>( );
			}
		}
		if( queueToHandle != null )
		{
			handleLater( queueToHandle );
		}
	}
	
	/**
	 * Batcher will call this when it is time for the implementation to start handling a batch of events (this method may return before it finishes handling
	 * them). When the implementation is done handling them it should call {@link #doneHandling(List)}.
	 * 
	 * @param batch
	 *            the batch of events to handle.
	 */
	protected abstract void handleLater( LinkedList<T> batch );
	
	/**
	 * Batcher will call this when an event is added while the implementation is handling a batch of events.
	 * 
	 * @param t
	 *            the event that was just added.
	 */
	protected void eventQueued( T t )
	{
		
	}
}