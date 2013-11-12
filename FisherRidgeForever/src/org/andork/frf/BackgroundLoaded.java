package org.andork.frf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class BackgroundLoaded<T>
{
	private final Object				lock			= new Object( );
	
	private State						state			= State.NOT_LOADED;
	
	private Throwable					error			= null;
	private T							value			= null;
	
	private final List<ChangeListener>	changeListeners	= new ArrayList<ChangeListener>( );
	
	public static enum State
	{
		NOT_LOADED , LOADING , LOADED , LOAD_FAILED;
	}
	
	public T tryGet( )
	{
		synchronized( lock )
		{
			if( state == State.NOT_LOADED )
			{
				loadInBackgroundIfNecessary( );
			}
			return state == State.LOADED ? value : null;
		}
	}
	
	public T get( )
	{
		synchronized( lock )
		{
			loadInBackgroundIfNecessary( );
			
			while( state == State.LOADING )
			{
				try
				{
					lock.wait( );
				}
				catch( InterruptedException ex )
				{
					
				}
			}
			
			return value;
		}
	}
	
	public State getState( )
	{
		synchronized( lock )
		{
			return state;
		}
	}
	
	public Throwable getLoadingError( )
	{
		synchronized( lock )
		{
			return error;
		}
	}
	
	public void loadInBackgroundIfNecessary( )
	{
		State state;
		
		synchronized( lock )
		{
			state = this.state;
			if( this.state == State.NOT_LOADED )
			{
				this.state = State.LOADING;
			}
		}
		
		if( state == State.NOT_LOADED )
		{
			loadInBackground( );
		}
	}
	
	protected abstract void loadInBackground( );
	
	protected void setValue( T value )
	{
		synchronized( lock )
		{
			if( state != State.LOADING )
			{
				throw new IllegalStateException( "Not in loading state" );
			}
			state = State.LOADED;
			
			this.value = value;
			
			lock.notifyAll( );
		}
		
		notifyChangeListeners( );
	}
	
	protected void setError( Throwable error )
	{
		synchronized( lock )
		{
			if( state != State.LOADING )
			{
				throw new IllegalStateException( "Not in loading state" );
			}
			state = State.LOAD_FAILED;
			
			this.error = error;
			
			lock.notifyAll( );
		}
		
		notifyChangeListeners( );
	}
	
	public void addChangeListener( ChangeListener listener )
	{
		if( !changeListeners.contains( listener ) )
		{
			changeListeners.add( listener );
		}
	}
	
	public void removeChangeListener( ChangeListener listener )
	{
		changeListeners.remove( listener );
	}
	
	private void notifyChangeListeners( )
	{
		ChangeEvent event = new ChangeEvent( this );
		for( ChangeListener listener : changeListeners )
		{
			listener.stateChanged( event );
		}
	}
}