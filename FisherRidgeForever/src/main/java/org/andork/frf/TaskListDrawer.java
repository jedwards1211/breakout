package org.andork.frf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.andork.awt.layout.Drawer;
import org.andork.awt.layout.Side;
import org.andork.event.HierarchicalBasicPropertyChangeAdapter;
import org.andork.swing.SpinnerButtonUI;
import org.andork.swing.async.TaskList;
import org.andork.swing.async.TaskService;

@SuppressWarnings( "serial" )
public class TaskListDrawer extends Drawer
{
	TaskService				taskService;
	TaskList				taskList;
	JScrollPane				taskListScrollPane;
	
	SpinnerButtonController	spinnerButtonController	= new SpinnerButtonController( );
	
	public TaskListDrawer( )
	{
		taskList = new TaskList( taskService );
		taskListScrollPane = new JScrollPane( taskList );
		taskListScrollPane.setPreferredSize( new Dimension( 400 , 100 ) );
		
		add( taskListScrollPane , BorderLayout.CENTER );
		delegate( ).dockingSide( Side.TOP );
		mainResizeHandle( );
		pinButton( ).setUI( new SpinnerButtonUI( ) );
		pinButton( ).setBackground( Color.BLACK );
		pinButtonDelegate( ).corner( null );
		pinButtonDelegate( ).side( Side.BOTTOM );
	}
	
	public TaskListDrawer( TaskService taskService )
	{
		this( );
		setTaskService( taskService );
	}
	
	public void setTaskService( TaskService taskService )
	{
		if( this.taskService != taskService )
		{
			if( this.taskService != null )
			{
				this.taskService.changeSupport( ).removePropertyChangeListener( spinnerButtonController );
			}
			
			this.taskService = taskService;
			taskList.setService( taskService );
			
			if( taskService != null )
			{
				taskService.changeSupport( ).addPropertyChangeListener( spinnerButtonController );
			}
		}
	}
	
	private class SpinnerButtonController extends HierarchicalBasicPropertyChangeAdapter
	{
		@Override
		public void childrenChanged( Object source , ChangeType changeType , Object child )
		{
			( ( SpinnerButtonUI ) pinButton( ).getUI( ) ).setSpinning( taskService.hasTasks( ) );
		}
	}
}
