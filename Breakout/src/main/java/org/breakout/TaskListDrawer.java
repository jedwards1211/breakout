/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout;

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
