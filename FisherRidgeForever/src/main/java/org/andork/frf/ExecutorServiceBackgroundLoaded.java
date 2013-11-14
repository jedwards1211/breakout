package org.andork.frf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ExecutorServiceBackgroundLoaded<T> extends BackgroundLoaded<T>
{
	ExecutorService	executor;
	
	public ExecutorServiceBackgroundLoaded( )
	{
		this( Executors.newSingleThreadExecutor( ) );
	}
	
	public ExecutorServiceBackgroundLoaded( ExecutorService executor )
	{
		super( );
		this.executor = executor;
	}
	
	@Override
	protected void loadInBackground( )
	{
		executor.submit( new Runnable( )
		{
			@Override
			public void run( )
			{
				try
				{
					setValue( load( ) );
				}
				catch( Exception ex )
				{
					setError( ex );
				}
			}
		} );
	}
	
	protected abstract T load( ) throws Exception;
}
