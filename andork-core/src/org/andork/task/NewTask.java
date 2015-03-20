package org.andork.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.util.StringUtils;

/**
 * A task that performs some computation (typically on a background thread) and can notify listeners when its status or
 * progress changes. It may run subtasks within itself, so the progress and status message can be hierarchical.
 * 
 * @author Andy Edwards
 *
 * @param <R>
 *            the task result type.
 */
public abstract class NewTask<R> implements Callable<R>
{
	private volatile Thread				thread;
	private volatile NewTask<?>			parent;
	private volatile NewTask<?>			subtask;
	private volatile Double				subtaskProportion;
	private final List<ChangeListener>	listeners	= new ArrayList<>( );

	private volatile String				status;
	private volatile Double				progress;
	private volatile boolean			canceled;

	/**
	 * Called by {@link #run()} (which safely sets the state of this task before and after).
	 */
	public abstract R doCall( ) throws Exception;

	/**
	 * Runs this task. It must not be running before this call is made.
	 * 
	 * @return the result of calling {@link #doCall()}.
	 * @throws IllegalStateException
	 *             if this task is already running.
	 * @throws Exception
	 *             if {@link #doCall()} threw it
	 */
	public final R call( ) throws Exception
	{
		start( );
		try
		{
			return doCall( );
		}
		finally
		{
			stop( );
		}
	}

	public void setStatus( String newStatus )
	{
		this.status = newStatus;
		fireChanged( );
	}

	public String getStatus( )
	{
		return status;
	}

	/**
	 * @return the combined status of this task and its subtasks.
	 */
	public String getCombinedStatus( )
	{
		NewTask<?> subtask = this.subtask;
		String status = this.status;
		if( subtask != null )
		{
			String subtaskStatus = subtask.getCombinedStatus( );
			if( !StringUtils.isNullOrEmpty( subtaskStatus ) )
			{
				if( status == null )
				{
					return subtaskStatus;
				}

				return status + ": " + subtaskStatus;
			}
		}
		if( status == null )
		{
			return "";
		}
		return status + "...";
	}

	/**
	 * Sets the progress and notifies listeners if it was changed.
	 * 
	 * @param newProgress
	 *            the new progress. Should be between 0 and 1 (but this is only convention) or {@code null} (which means
	 *            progress is indeterminate).
	 */
	public void setProgress( Double newProgress )
	{
		this.progress = newProgress;
		fireChanged( );
	}

	/**
	 * @return the progress. {@code null} means progress is indeterminate.
	 */
	public Double getProgress( )
	{
		return progress;
	}

	/**
	 * @return the combined progress of this task and its subtasks.
	 */
	public Double getCombinedProgress( )
	{
		NewTask<?> subtask = this.subtask;
		if( subtask != null )
		{
			return add( progress , multiply( subtaskProportion , subtask.getCombinedProgress( ) ) );
		}

		return progress;
	}

	/**
	 * Same as {@link #setCanceled(boolean) setCanceled(true)}.
	 */
	public void cancel( )
	{
		canceled = true;
	}

	/**
	 * Sets whether this task is canceled. You may call this at any time; this task does not have to be running.
	 */
	public void setCanceled( boolean canceled )
	{
		this.canceled = canceled;
		fireChanged( );
	}

	public boolean isCanceled( )
	{
		return canceled;
	}

	public boolean isCanceledOrAncestorCanceled( )
	{
		NewTask<?> parent = this.parent;
		if( parent != null )
		{
			return parent.isCanceledOrAncestorCanceled( ) || canceled;
		}
		return canceled;
	}

	public boolean isRunning( )
	{
		return thread != null;
	}

	public NewTask<?> getParent( )
	{
		return parent;
	}

	public NewTask<?> getSubtask( )
	{
		return subtask;
	}

	public NewTask<?> getDeepestSubtask( )
	{
		NewTask<?> subtask = this.subtask;
		return subtask == null ? this : subtask.getDeepestSubtask( );
	}

	public String getDeepestSubtaskStatus( )
	{
		return getDeepestSubtask( ).getStatus( );
	}

	public Double getDeepestSubtaskProgress( )
	{
		return getDeepestSubtask( ).getProgress( );
	}

	public void addChangeListener( ChangeListener listener )
	{
		listeners.add( listener );
	}

	public void removeChangeListener( ChangeListener listener )
	{
		listeners.remove( listener );
	}

	protected final void fireChanged( )
	{
		ChangeEvent event = new ChangeEvent( this );
		for( ChangeListener listener : listeners )
		{
			listener.stateChanged( event );
		}

		NewTask<?> parent = this.parent;
		if( parent != null )
		{
			parent.fireChanged( );
		}
	}

	protected final <R2> R2 callSubtask( Double proportion , NewTask<R2> subtask ) throws Exception
	{
		setSubtask( proportion , subtask );

		try
		{
			return subtask.call( );
		}
		finally
		{
			clearSubtask( );
		}
	}

	private void start( )
	{
		synchronized( this )
		{
			if( thread != null )
			{
				throw new IllegalStateException( "already running" );
			}
			thread = Thread.currentThread( );
		}
		fireChanged( );
	}

	private void stop( )
	{
		synchronized( this )
		{
			thread = null;
		}
		fireChanged( );
	}

	private void setSubtask( Double proportion , NewTask<?> subtask )
	{
		synchronized( this )
		{
			if( thread == null )
			{
				throw new IllegalStateException( "a subtask may only be run when this task is running" );
			}
			if( Thread.currentThread( ) != thread )
			{
				throw new IllegalStateException( "a subtask must be run on the same thread as this task" );
			}

			if( this.subtask != null )
			{
				throw new IllegalStateException( "the current subtask has not finished" );
			}

			synchronized( subtask )
			{
				if( subtask.parent != null )
				{
					throw new IllegalStateException( "subtask is already running under another task" );
				}

				this.subtaskProportion = proportion;
				this.subtask = subtask;
				subtask.parent = this;
			}
		}
		fireChanged( );
	}

	private void clearSubtask( )
	{
		synchronized( this )
		{
			synchronized( subtask )
			{
				subtask.parent = null;
				subtask = null;
				subtaskProportion = null;
			}
		}
		fireChanged( );
	}

	private static Double add( Double a , Double b )
	{
		return a == null || b == null ? null : a + b;
	}

	private static Double multiply( Double a , Double b )
	{
		return a == null || b == null ? null : a * b;
	}
}
