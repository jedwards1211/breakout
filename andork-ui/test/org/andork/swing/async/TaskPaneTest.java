package org.andork.swing.async;

import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.andork.swing.QuickTestFrame;

public class TaskPaneTest
{
	public static void main( String[ ] args ) throws Exception
	{
		SingleThreadedTaskService service = new SingleThreadedTaskService( );
		TaskList taskList = new TaskList( service );
		JScrollPane taskListScrollPane = new JScrollPane( taskList );
		taskListScrollPane.setPreferredSize( new Dimension( 300 , 500 ) );
		
		QuickTestFrame.frame( taskListScrollPane ).setVisible( true );
		
		for( int i = 0 ; i < 50 ; i++ )
		{
			TestTask task = new TestTask( "Task " + i );
			service.submit( task );
			
			Thread.sleep( 1000 );
		}
	}
	
	static class TestTask extends Task
	{
		public TestTask( String status )
		{
			setStatus( status );
			setCompleted( 0 );
			setTotal( 10 );
		}
		
		@Override
		protected void execute( ) throws Exception
		{
			for( int i = 0 ; i < 10 && getState( ) != State.CANCELING ; i++ )
			{
				setCompleted( i );
				Thread.sleep( 3000 );
			}
		}
	}
}
