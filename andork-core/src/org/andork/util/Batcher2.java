package org.andork.util;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Batcher2<T> implements Consumer<T>
{
	private final Object			lock	= new Object( );
	private LinkedList<T>			queue	= new LinkedList<T>( );
	private int						maxSize	= Integer.MAX_VALUE;
	private boolean					busy;
	
	private Consumer<Runnable>		runner;
	private Consumer<LinkedList<T>>	handler;
	private Consumer<T>				interruptor;
	
	public Batcher2( Consumer<Runnable> runner , Consumer<LinkedList<T>> handler )
	{
		super( );
		this.runner = runner;
		this.handler = handler;
	}
	
	public int getMaxSize( )
	{
		return maxSize;
	}
	
	public void setMaxSize( int maxSize )
	{
		this.maxSize = maxSize;
	}
	
	public boolean isBusy( )
	{
		synchronized( this )
		{
			return busy;
		}
	}
	
	public Consumer<T> getInterruptor( )
	{
		return interruptor;
	}
	
	public void setInterruptor( Consumer<T> interruptor )
	{
		this.interruptor = interruptor;
	}
	
	@Override
	public void accept( T t )
	{
		synchronized( lock )
		{
			LinkedList<T> queueToHandle;
			synchronized( lock )
			{
				if( queue.size( ) < maxSize )
				{
					queue.add( t );
					if( !busy )
					{
						busy = true;
						queueToHandle = this.queue;
						this.queue = new LinkedList<>( );
					}
					else
					{
						queueToHandle = null;
					}
				}
				else
				{
					queueToHandle = null;
				}
			}
			
			if( queueToHandle != null )
			{
				runner.accept( ( ) ->
				{
					LinkedList<T> currentQueue = queueToHandle;
					
					while( currentQueue != null )
					{
						try
						{
							handler.accept( currentQueue );
						}
						catch( Exception ex )
						{
							ex.printStackTrace( );
						}
						
						synchronized( lock )
						{
							if( queue.isEmpty( ) )
							{
								currentQueue = null;
								busy = false;
							}
							else
							{
								currentQueue = queue;
								queue = new LinkedList<>( );
							}
						}
					}
				} );
			}
			else
			{
				if( interruptor != null )
				{
					interruptor.accept( t );
				}
			}
		}
	}
}
