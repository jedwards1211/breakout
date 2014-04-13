package org.andork.swing.async;

import javax.swing.SwingUtilities;

import org.andork.event.BasicPropertyChangeSupport;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.logging.Java17SimpleFormatter;
import org.andork.model.HasChangeSupport;
import org.andork.util.ArrayUtils;
import org.andork.util.Java7;

public abstract class Task implements HasChangeSupport
{
	public static enum Property
	{
		STATE , SERVICE , STATUS , INDETERMINATE , COMPLETED , TOTAL;
	}
	
	public static enum State
	{
		NOT_SUBMITTED , WAITING , RUNNING , CANCELING , CANCELED , FINISHED , FAILED;
	}
	
	private final long							creationTimestamp;
	
	private final Object						lock					= new Object( );
	private State								state					= State.NOT_SUBMITTED;
	private TaskService							service;
	private Throwable							throwable;
	
	private String								status;
	
	private boolean								indeterminate			= true;
	
	private int									completed;
	private int									total;
	
	private final BasicPropertyChangeSupport	propertyChangeSupport	= new BasicPropertyChangeSupport( );
	
	public Task( )
	{
		creationTimestamp = System.nanoTime( );
	}
	
	public Task( String status )
	{
		this( );
		setStatus( status );
	}
	
	public long getCreationTimestamp( )
	{
		return creationTimestamp;
	}
	
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( )
	{
		return propertyChangeSupport.external( );
	}
	
	private final void firePropertyChange( final Property property , final Object oldValue , final Object newValue )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				propertyChangeSupport.firePropertyChange( Task.this , property , oldValue , newValue );
			}
		} );
	}
	
	public final State getState( )
	{
		synchronized( lock )
		{
			return state;
		}
	}
	
	public boolean isCancelable( )
	{
		return false;
	}
	
	public final boolean isCanceled( )
	{
		return getState( ) == State.CANCELED;
	}
	
	public final boolean isCanceling( )
	{
		return getState( ) == State.CANCELING;
	}
	
	public String getStatus( )
	{
		return status;
	}
	
	public void setStatus( String status )
	{
		String oldValue = null;
		synchronized( lock )
		{
			if( !Java7.Objects.equals( this.status , status ) )
			{
				oldValue = this.status;
				this.status = status;
			}
		}
		if( oldValue != null )
		{
			firePropertyChange( Property.STATUS , oldValue , status );
		}
	}
	
	public boolean isIndeterminate( )
	{
		return indeterminate;
	}
	
	public void setIndeterminate( boolean indefinite )
	{
		Boolean oldValue = null;
		synchronized( lock )
		{
			if( this.indeterminate != indefinite )
			{
				oldValue = this.indeterminate;
				this.indeterminate = indefinite;
			}
		}
		if( oldValue != null )
		{
			firePropertyChange( Property.INDETERMINATE , oldValue , indefinite );
		}
	}
	
	public int getCompleted( )
	{
		return completed;
	}
	
	public void setCompleted( int completed )
	{
		if( completed < 0 )
		{
			throw new IllegalArgumentException( "completed must be >= 0" );
		}
		Integer oldValue = null;
		synchronized( lock )
		{
			if( this.completed != completed )
			{
				oldValue = this.completed;
				this.completed = completed;
			}
		}
		if( oldValue != null )
		{
			firePropertyChange( Property.COMPLETED , oldValue , completed );
		}
	}
	
	public int getTotal( )
	{
		return total;
	}
	
	public void setTotal( int total )
	{
		if( total < 0 )
		{
			throw new IllegalArgumentException( "total must be >= 0" );
		}
		
		Integer oldValue = null;
		synchronized( lock )
		{
			if( this.total != total )
			{
				oldValue = this.total;
				this.total = total;
			}
		}
		if( oldValue != null )
		{
			firePropertyChange( Property.TOTAL , oldValue , total );
		}
	}
	
	public Throwable getThrowable( )
	{
		synchronized( lock )
		{
			return throwable;
		}
	}
	
	public final void setService( TaskService service )
	{
		if( service == null )
		{
			throw new IllegalArgumentException( "service must be non-null" );
		}
		
		synchronized( lock )
		{
			if( this.service != null )
			{
				throw new IllegalStateException( "Task is still registered with a service" );
			}
			
			checkState( State.NOT_SUBMITTED );
			state = State.WAITING;
			this.service = service;
		}
		
		firePropertyChange( Property.STATE , State.NOT_SUBMITTED , State.WAITING );
	}
	
	public boolean canRunInParallelWith( Task other )
	{
		return false;
	}
	
	public final void run( )
	{
		try
		{
			setRunning( );
			execute( );
			setCanceledOrFinished( );
		}
		catch( Throwable e )
		{
			synchronized( lock )
			{
				if( state == State.CANCELING )
				{
					setCanceledOrFinished( );
				}
				else
				{
					setFailed( e );
				}
			}
		}
	}
	
	protected abstract void execute( ) throws Exception;
	
	public final void cancel( )
	{
		if( !isCancelable( ) )
		{
			throw new UnsupportedOperationException( "task is not cancelable" );
		}
		State oldState;
		State newState;
		TaskService service;
		synchronized( lock )
		{
			if( state == State.CANCELED || state == State.CANCELING || state == State.FINISHED || state == State.FAILED )
			{
				return;
			}
			oldState = state;
			newState = state = State.CANCELING;
			service = this.service;
		}
		service.cancel( this );
		firePropertyChange( Property.STATE , oldState , newState );
	}
	
	public final void reset( )
	{
		State oldState;
		synchronized( lock )
		{
			checkState( State.CANCELED , State.FINISHED , State.FAILED );
			oldState = state;
			state = State.NOT_SUBMITTED;
			service = null;
		}
		firePropertyChange( Property.STATE , oldState , State.NOT_SUBMITTED );
		afterReset( );
	}
	
	private void checkState( State required )
	{
		if( state != required )
		{
			throw new IllegalStateException( "Operation not allowed unless state is " + required + "; state is currently " + state );
		}
	}
	
	private void checkState( State ... required )
	{
		if( ArrayUtils.indexOf( required , state ) < 0 )
		{
			throw new IllegalStateException( "Operation not allowed unless state is " + ArrayUtils.cat( required , " or " ) + "; state is currently " + state );
		}
	}
	
	protected void afterReset( )
	{
	}
	
	private void setRunning( )
	{
		synchronized( lock )
		{
			checkState( State.WAITING );
			state = State.RUNNING;
		}
		firePropertyChange( Property.STATE , State.WAITING , State.RUNNING );
	}
	
	private void setFailed( Throwable t )
	{
		State oldState;
		synchronized( lock )
		{
			oldState = state;
			state = State.FAILED;
			throwable = t;
		}
		t.printStackTrace( );
		firePropertyChange( Property.STATE , oldState , State.FAILED );
	}
	
	private void setCanceledOrFinished( )
	{
		State oldState;
		State newState;
		synchronized( lock )
		{
			oldState = state;
			switch( state )
			{
				case CANCELING:
					state = State.CANCELED;
					break;
				case RUNNING:
					state = State.FINISHED;
					break;
				default:
					throw new IllegalStateException( "Operation not allowed unless state == CANCELED or FINISHED" );
			}
			newState = state;
		}
		firePropertyChange( Property.STATE , oldState , newState );
	}
	
	public final int hashCode( )
	{
		return super.hashCode( );
	}
	
	public final boolean equals( Object o )
	{
		return super.equals( o );
	}
}
