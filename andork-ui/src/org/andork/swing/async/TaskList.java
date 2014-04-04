package org.andork.swing.async;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedHashMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.andork.collect.CollectionUtils;
import org.andork.event.HierarchicalBasicPropertyChangeAdapter;

@SuppressWarnings( "serial" )
public class TaskList extends JPanel implements Scrollable
{
	TaskService						service;
	LinkedHashMap<Task, TaskPane>	taskMap				= CollectionUtils.newLinkedHashMap( );
	
	private ModelChangeHandler		modelChangeHandler	= new ModelChangeHandler( );
	
	public TaskList( )
	{
		init( );
	}
	
	public TaskList( TaskService service )
	{
		this( );
		setService( service );
	}
	
	protected void init( )
	{
		setLayout( new BoxLayout( this , BoxLayout.Y_AXIS ) );
	}
	
	public void setService( TaskService service )
	{
		if( this.service != service )
		{
			if( this.service != null )
			{
				this.service.changeSupport( ).removePropertyChangeListener( modelChangeHandler );
			}
			this.service = service;
			if( service != null )
			{
				service.changeSupport( ).addPropertyChangeListener( modelChangeHandler );
			}
			rebuild( );
		}
	}
	
	public TaskService getService( )
	{
		return service;
	}
	
	protected void rebuild( )
	{
		for( TaskPane pane : taskMap.values( ) )
		{
			pane.setTask( null );
		}
		removeAll( );
		
		if( service != null )
		{
			for( Task task : service.getTasks( ) )
			{
				TaskPane pane = new TaskPane( task );
				taskMap.put( task , pane );
				add( pane );
			}
		}
		
		revalidate( );
	}
	
	private class ModelChangeHandler extends HierarchicalBasicPropertyChangeAdapter
	{
		@Override
		public void childrenChanged( Object source , ChangeType changeType , Object ... children )
		{
			switch( changeType )
			{
				case ALL_CHILDREN_CHANGED:
					rebuild( );
					break;
				case CHILDREN_ADDED:
					for( Task task : ( Task[ ] ) children )
					{
						TaskPane taskPane = new TaskPane( task );
						taskMap.put( task , taskPane );
						add( taskPane );
					}
					revalidate( );
					break;
				case CHILDREN_REMOVED:
					for( Task task : ( Task[ ] ) children )
					{
						TaskPane taskPane = taskMap.remove( task );
						if( taskPane != null )
						{
							taskPane.setTask( null );
							remove( taskPane );
						}
					}
					break;
			}
		}
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize( )
	{
		return getPreferredSize( );
	}
	
	@Override
	public int getScrollableUnitIncrement( Rectangle visibleRect , int orientation , int direction )
	{
		return 0;
	}
	
	@Override
	public int getScrollableBlockIncrement( Rectangle visibleRect , int orientation , int direction )
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth( )
	{
		return true;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight( )
	{
		// TODO Auto-generated method stub
		return false;
	}
}
