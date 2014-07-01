package org.andork.swing.async;

import java.util.concurrent.ExecutionException;

public abstract class FutureTask<R> extends Task
{
	private R	result;
	
	@Override
	protected final void execute( ) throws Exception
	{
		result = doGet( );
	}
	
	protected abstract R doGet( ) throws Exception;
	
	public R get( ) throws InterruptedException , ExecutionException
	{
		waitUntilHasFinished( );
		return result;
	}
}
