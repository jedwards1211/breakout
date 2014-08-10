package org.andork.io;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.andork.io.KVLiteChannel.Record;

public class KVLiteChannelWriteQueue implements Consumer<List<Record>>
{
	private LinkedBlockingQueue<List<Record>>	queue	= new LinkedBlockingQueue<>( );
	private ExecutorService						executor;
	private Consumer<List<Record>>				downstream;
	
	public KVLiteChannelWriteQueue( Consumer<List<Record>> downstream )
	{
		this.downstream = downstream;
		this.executor = Executors.newSingleThreadExecutor( r -> {
			Thread thread = new Thread( r );
			thread.setName( "KVLiteChannelWriteQueue" );
			thread.setDaemon( false );
			return thread;
		} );
		
		executor.submit( new Writer( ) );
	}
	
	public void accept( List<Record> records )
	{
		queue.add( records );
	}
	
	private class Writer implements Runnable
	{
		@Override
		public void run( )
		{
			List<List<Record>> records = new LinkedList<>( );
			queue.drainTo( records );
			
			List<Record> flatRecords = new LinkedList<>( );
			for( List<Record> list : records )
			{
				flatRecords.addAll( list );
			}
			
			try
			{
				downstream.accept( flatRecords );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
			finally
			{
				executor.submit( this );
			}
		}
	}
}
