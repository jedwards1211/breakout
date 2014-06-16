package org.andork.swing.async;

import org.andork.util.Java7;

public class Subtask
{
	private Object	parent;
	private Subtask	child;
	
	private String	status;
	private boolean	indeterminate;
	private int		completed;
	private int		total;
	private int		proportion;
	
	public Subtask( Task parent )
	{
		this.parent = parent;
		this.proportion = 1;
	}
	
	private Subtask( Subtask parent , int proportion )
	{
		this.parent = parent;
		this.proportion = proportion;
	}
	
	public Object getParent( )
	{
		return parent;
	}
	
	public Subtask getChild( )
	{
		return child;
	}
	
	public String getStatus( )
	{
		return status;
	}
	
	public boolean isIndeterminate( )
	{
		return indeterminate;
	}
	
	public int getCompleted( )
	{
		return completed;
	}
	
	public int getTotal( )
	{
		return total;
	}
	
	public int getProportion( )
	{
		return proportion;
	}
	
	public boolean isCanceling( )
	{
		return parent instanceof Task ? ( ( Task ) parent ).isCanceling( )
				: parent instanceof Subtask ? ( ( Subtask ) parent ).isCanceling( ) : null;
	}
	
	public double getCompletedRecursive( )
	{
		double result = completed;
		if( child != null )
		{
			result += child.getCompletedRecursive( );
		}
		return result * proportion / total;
	}
	
	public String getStatusRecursive( )
	{
		String childStatus = child == null ? null : child.getStatusRecursive( );
		return status == null ? childStatus : childStatus == null ? status + "..." : status + ": " + childStatus;
	}
	
	public boolean getIndeterminateRecursive( )
	{
		return indeterminate || ( child != null && child.getIndeterminateRecursive( ) );
	}
	
	private void updateParent( )
	{
		if( parent instanceof Task )
		{
			Task task = ( Task ) parent;
			String status = getStatusRecursive( );
			if( status != null )
			{
				task.setStatus( status );
			}
			task.setIndeterminate( getIndeterminateRecursive( ) );
			task.setCompleted( ( int ) Math.round( getCompletedRecursive( ) * task.getTotal( ) ) );
		}
		else if( parent instanceof Subtask )
		{
			( ( Subtask ) parent ).updateParent( );
		}
	}
	
	public static Subtask defaultCreate( Task parent )
	{
		return parent != null ? new Subtask( parent ) : dummySubtask( );
	}
	
	public static Subtask dummySubtask( )
	{
		return new Subtask( null );
	}
	
	public Subtask beginSubtask( int proportion )
	{
		if( proportion < 1 )
		{
			throw new IllegalArgumentException( "proportion must be >= 1" );
		}
		
		if( child != null )
		{
			throw new IllegalStateException( "there is an incomplete subtask." );
		}
		return child = new Subtask( this , proportion );
	}
	
	public void end( )
	{
		if( parent instanceof Subtask )
		{
			Subtask parentSubtask = ( Subtask ) parent;
			parentSubtask.child = null;
			parentSubtask.updateParent( );
		}
	}
	
	public void setStatus( String status )
	{
		if( !Java7.Objects.equals( this.status , status ) )
		{
			this.status = status;
			updateParent( );
		}
	}
	
	public void setIndeterminate( boolean indeterminate )
	{
		if( this.indeterminate != indeterminate )
		{
			this.indeterminate = indeterminate;
			updateParent( );
		}
	}
	
	public void setCompleted( int completed )
	{
		if( this.completed != completed )
		{
			this.completed = completed;
			updateParent( );
		}
	}
	
	public void setTotal( int total )
	{
		if( this.total != total )
		{
			this.total = total;
			updateParent( );
		}
	}
}
