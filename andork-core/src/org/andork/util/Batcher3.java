package org.andork.util;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Batcher3<T> implements Consumer<T>
{
	private final Object			lock	= new Object( );
	private LinkedList<T>			queue	= new LinkedList<T>( );
	private int						maxSize	= Integer.MAX_VALUE;

	private BooleanSupplier			isBusy;
	private Consumer<LinkedList<T>>	handler;
	private Consumer<T>				interruptor;
	private Timer					timer;
	private long					delay;

	public Batcher3( Consumer<LinkedList<T>> handler , BooleanSupplier isBusy )
	{
		super( );
		this.handler = handler;
		this.isBusy = isBusy;
	}

	public int getMaxSize( )
	{
		return maxSize;
	}

	public void setMaxSize( int maxSize )
	{
		this.maxSize = maxSize;
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
			if( queue.size( ) < maxSize )
			{
				queue.add( t );
			}
			if( queue.size( ) == 1 )
			{
				timer.scheduleAtFixedRate( new HandleTask( ) , delay , delay );
			}
		}

		if( interruptor != null )
		{
			interruptor.accept( t );
		}
	}

	private class HandleTask extends TimerTask
	{
		@Override
		public void run( )
		{
			if( !isBusy.getAsBoolean( ) )
			{
				timer.cancel( );

				LinkedList<T> queueToHandle = null;

				synchronized( lock )
				{
					if( !queue.isEmpty( ) )
					{
						queueToHandle = queue;
						queue = new LinkedList<>( );
					}
				}

				if( queueToHandle != null )
				{
					handler.accept( queueToHandle );
				}
			}
		}
	}
}
