package org.andork.breakout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

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
	final Set<TaskService>	taskServices			= new HashSet<TaskService>( );
	TaskList				taskList;
	JScrollPane				taskListScrollPane;
	
	SpinnerButtonController	spinnerButtonController	= new SpinnerButtonController( );
	
	public TaskListDrawer( )
	{
		taskList = new TaskList( );
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
	
	public void addTaskService( TaskService taskService )
	{
		if( taskServices.add( taskService ) )
		{
			taskList.addService( taskService );
			taskService.changeSupport( ).addPropertyChangeListener( spinnerButtonController );
		}
	}
	
	public void removeTaskService( TaskService taskService )
	{
		if( taskServices.add( taskService ) )
		{
			taskList.removeService( taskService );
			taskService.changeSupport( ).removePropertyChangeListener( spinnerButtonController );
		}
	}
	
	private class SpinnerButtonController extends HierarchicalBasicPropertyChangeAdapter
	{
		@Override
		public void childrenChanged( Object source , ChangeType changeType , Object ... children )
		{
			boolean hasTasks = false;
			for( TaskService service : taskServices )
			{
				hasTasks |= service.hasTasks( );
			}
			( ( SpinnerButtonUI ) pinButton( ).getUI( ) ).setSpinning( hasTasks );
		}
	}
}
