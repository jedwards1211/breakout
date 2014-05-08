package org.andork.event;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;

@SuppressWarnings( "serial" )
public class HierarchicalBasicPropertyChangeSupport extends BasicPropertyChangeSupport
{
	public void fireChildAdded( Object parent , Object ... addedChildren )
	{
		if( listeners != null )
		{
			for( BasicPropertyChangeListener listener : listeners )
			{
				if( listener instanceof HierarchicalBasicPropertyChangeListener )
				{
					( ( HierarchicalBasicPropertyChangeListener ) listener ).childrenChanged( parent , ChangeType.CHILDREN_ADDED , addedChildren );
				}
			}
		}
	}
	
	public void fireChildRemoved( Object parent , Object ... removedChildren )
	{
		if( listeners != null )
		{
			for( BasicPropertyChangeListener listener : listeners )
			{
				if( listener instanceof HierarchicalBasicPropertyChangeListener )
				{
					( ( HierarchicalBasicPropertyChangeListener ) listener ).childrenChanged( parent , ChangeType.CHILDREN_REMOVED , removedChildren );
				}
			}
		}
	}
	
	public void fireChildrenChanged( Object parent )
	{
		if( listeners != null )
		{
			for( BasicPropertyChangeListener listener : listeners )
			{
				if( listener instanceof HierarchicalBasicPropertyChangeListener )
				{
					( ( HierarchicalBasicPropertyChangeListener ) listener ).childrenChanged( parent , ChangeType.ALL_CHILDREN_CHANGED );
				}
			}
		}
	}
	
	public void fireChildrenChanged( Object parent , ChangeType changeType , Object ... children )
	{
		if( listeners != null )
		{
			for( BasicPropertyChangeListener listener : listeners )
			{
				if( listener instanceof HierarchicalBasicPropertyChangeListener )
				{
					( ( HierarchicalBasicPropertyChangeListener ) listener ).childrenChanged( parent , changeType , children );
				}
			}
		}
	}
}
