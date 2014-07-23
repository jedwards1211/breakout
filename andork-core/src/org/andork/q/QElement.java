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
package org.andork.q;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.andork.event.HierarchicalBasicPropertyChangeListener.ChangeType;
import org.andork.event.HierarchicalBasicPropertyChangePropagator;
import org.andork.event.HierarchicalBasicPropertyChangeSupport;
import org.andork.func.Mapper;
import org.andork.model.HasChangeSupport;

public abstract class QElement implements HasChangeSupport
{
	protected final HierarchicalBasicPropertyChangeSupport		changeSupport	= new HierarchicalBasicPropertyChangeSupport( );
	protected final HierarchicalBasicPropertyChangePropagator	propagator		= new HierarchicalBasicPropertyChangePropagator( this , changeSupport );
	protected final Set<Object>									children		= new LinkedHashSet<Object>( );
	
	public HierarchicalBasicPropertyChangeSupport.External changeSupport( )
	{
		return changeSupport.external( );
	}
	
	public abstract QElement deepClone( Mapper<Object, Object> childMapper );
	
	protected final void addChild( Object child )
	{
		if( children.add( child ) )
		{
			if( child instanceof QElement )
			{
				( ( QElement ) child ).changeSupport.addPropertyChangeListener( propagator );
			}
			changeSupport.fireChildAdded( this , child );
		}
	}
	
	protected final void addChildren( Object ... children )
	{
		Set<Object> added = new LinkedHashSet<Object>( );
		for( Object child : children )
		{
			if( this.children.add( child ) )
			{
				added.add( child );
				if( child instanceof QElement )
				{
					( ( QElement ) child ).changeSupport.addPropertyChangeListener( propagator );
				}
			}
		}
		changeSupport.fireChildrenChanged( this , ChangeType.CHILDREN_ADDED , added );
	}
	
	protected final void addChildren( Collection<?> children )
	{
		addChildren( children.toArray( ) );
	}
	
	protected final void removeChild( Object child )
	{
		if( children.remove( child ) )
		{
			if( child instanceof QElement )
			{
				( ( QElement ) child ).changeSupport.removePropertyChangeListener( propagator );
			}
			changeSupport.fireChildRemoved( this , child );
		}
	}
	
	protected final void removeChildren( Object ... children )
	{
		Set<Object> removed = new LinkedHashSet<Object>( );
		for( Object child : children )
		{
			if( this.children.remove( child ) )
			{
				removed.add( child );
				if( child instanceof QElement )
				{
					( ( QElement ) child ).changeSupport.addPropertyChangeListener( propagator );
				}
			}
		}
		changeSupport.fireChildrenChanged( this , ChangeType.CHILDREN_REMOVED , removed );
	}
	
	protected final void removeChildren( Collection<?> children )
	{
		removeChildren( children.toArray( ) );
	}
	
	protected final void clearChildren( )
	{
		Set<Object> removed = new LinkedHashSet<Object>( children );
		for( Object child : children )
		{
			if( child instanceof QElement )
			{
				( ( QElement ) child ).changeSupport.removePropertyChangeListener( propagator );
			}
		}
		children.clear( );
		changeSupport.fireChildrenChanged( this , ChangeType.CHILDREN_REMOVED , removed );
	}
}
