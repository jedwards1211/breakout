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
package org.andork.q2;

import java.util.ArrayList;
import java.util.List;

import org.andork.event.BasicPropertyChangeListener;
import org.andork.event.HierarchicalBasicPropertyChangeListener;
import org.andork.event.SourcePath;
import org.andork.func.Mapper;

public abstract class QElement implements HierarchicalBasicPropertyChangeListener
{
	private Object	listeners;

	public abstract QElement deepClone( Mapper<Object, Object> childMapper );

	@SuppressWarnings( "unchecked" )
	public void addPropertyChangeListener( BasicPropertyChangeListener listener )
	{
		if( listeners instanceof List )
		{
			List<BasicPropertyChangeListener> casted = ( List<BasicPropertyChangeListener> ) listeners;
			if( !casted.contains( listener ) )
			{
				casted.add( listener );
			}
		}
		else if( listeners instanceof BasicPropertyChangeListener && !listeners.equals( listener ) )
		{
			ArrayList<BasicPropertyChangeListener> newList = new ArrayList<>( 2 );
			newList.add( ( BasicPropertyChangeListener ) listeners );
			newList.add( listener );
			listeners = newList;
		}
		else
		{
			listeners = listener;
		}
	}

	@SuppressWarnings( "unchecked" )
	public void removePropertyChangeListener( BasicPropertyChangeListener listener )
	{
		if( listeners instanceof List )
		{
			List<BasicPropertyChangeListener> casted = ( List<BasicPropertyChangeListener> ) listeners;
			casted.remove( listener );
			if( casted.size( ) == 1 )
			{
				listeners = casted.get( 0 );
			}
			else if( casted.isEmpty( ) )
			{
				listeners = null;
			}
		}
		else if( listeners instanceof BasicPropertyChangeListener )
		{
			if( listeners.equals( listener ) )
			{
				listeners = null;
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	protected void firePropertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		if( listeners instanceof List )
		{
			for( BasicPropertyChangeListener listener : ( List<BasicPropertyChangeListener> ) listeners )
			{
				listener.propertyChange( source , property , oldValue , newValue , index );
			}
		}
		else if( listeners instanceof BasicPropertyChangeListener )
		{
			( ( BasicPropertyChangeListener ) listeners ).propertyChange( source , property , oldValue ,
				newValue , index );
		}
	}

	protected void firePropertyChange( Object source , Object property , Object oldValue , Object newValue )
	{
		firePropertyChange( source , property , oldValue , newValue , -1 );
	}

	protected void firePropertyChange( Object property , Object oldValue , Object newValue )
	{
		firePropertyChange( this , property , oldValue , newValue , -1 );
	}

	protected void firePropertyChange( Object property , Object oldValue , Object newValue , int index )
	{
		firePropertyChange( this , property , oldValue , newValue , index );
	}

	protected void fireChildrenChanged( ChangeType changeType , Object ... children )
	{
		fireChildrenChanged( this , changeType , children );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireChildrenChanged( Object source , ChangeType changeType , Object ... children )
	{
		if( listeners instanceof List )
		{
			for( BasicPropertyChangeListener listener : ( List<BasicPropertyChangeListener> ) listeners )
			{
				if( listener instanceof HierarchicalBasicPropertyChangeListener )
				{
					( ( HierarchicalBasicPropertyChangeListener ) listener ).childrenChanged( source , changeType ,
						children );
				}
			}
		}
		else if( listeners instanceof HierarchicalBasicPropertyChangeListener )
		{
			( ( HierarchicalBasicPropertyChangeListener ) listeners ).childrenChanged( source , changeType , children );
		}
	}

	protected void fireChildrenAdded( Object source , Object ... children )
	{
		fireChildrenChanged( source , ChangeType.CHILDREN_ADDED , children );
	}

	protected void fireChildrenRemoved( Object source , Object ... children )
	{
		fireChildrenChanged( source , ChangeType.CHILDREN_REMOVED , children );
	}

	protected void fireChildrenChanged( )
	{
		fireChildrenChanged( ChangeType.ALL_CHILDREN_CHANGED );
	}

	protected void fireChildrenChanged( Object source )
	{
		fireChildrenChanged( source , ChangeType.ALL_CHILDREN_CHANGED );
	}

	protected void handleChildAdded( Object child )
	{
		if( child instanceof QElement )
		{
			( ( QElement ) child ).addPropertyChangeListener( this );
		}
		fireChildrenAdded( this , child );
	}

	protected void handleChildRemoved( Object child )
	{
		if( child instanceof QElement )
		{
			( ( QElement ) child ).removePropertyChangeListener( this );
		}
		fireChildrenRemoved( this , child );
	}

	protected void handleChildrenAdded( Object ... children )
	{
		for( Object child : children )
		{
			if( child instanceof QElement )
			{
				( ( QElement ) child ).addPropertyChangeListener( this );
			}
		}
		fireChildrenAdded( this , children );
	}

	protected void handleChildrenRemoved( Object ... children )
	{
		for( Object child : children )
		{
			if( child instanceof QElement )
			{
				( ( QElement ) child ).removePropertyChangeListener( this );
			}
		}
		fireChildrenRemoved( this , children );
	}

	@Override
	public final void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		firePropertyChange( new SourcePath( this , source ) , property , oldValue , newValue , index );
	}

	@Override
	public final void childrenChanged( Object source , ChangeType changeType , Object ... children )
	{
		fireChildrenChanged( new SourcePath( this , source ) , changeType , children );
	}
}
