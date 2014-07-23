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
package org.andork.bind;

import org.andork.event.BasicPropertyChangeListener;
import org.andork.q.QObject;
import org.andork.q.QSpec.Attribute;
import org.andork.util.Java7;

public class QObjectAttributeBinder<T> extends Binder<T> implements BasicPropertyChangeListener
{
	private Binder<? extends QObject<?>>	upstream;
	private final Attribute<T>				attribute;
	QObject<?>								object;
	T										value;
	
	public QObjectAttributeBinder( Attribute<T> attribute )
	{
		this.attribute = attribute;
	}
	
	public static <T> QObjectAttributeBinder<T> bind( Attribute<T> attribute , Binder<? extends QObject<?>> upstream )
	{
		return new QObjectAttributeBinder<T>( attribute ).bind( upstream );
	}
	
	public QObjectAttributeBinder<T> bind( Binder<? extends QObject<?>> upstream )
	{
		if( this.upstream != upstream )
		{
			if( this.upstream != null )
			{
				unbind0( this.upstream , this );
			}
			this.upstream = upstream;
			if( this.upstream != null )
			{
				bind0( this.upstream , this );
			}
			
			update( false );
		}
		return this;
	}
	
	public void unbind( )
	{
		bind( null );
	}
	
	@Override
	public T get( )
	{
		return value;
	}
	
	@Override
	public void set( T newValue )
	{
		if( object != null )
		{
			object.set( attribute , newValue );
		}
	}
	
	@Override
	public void update( boolean force )
	{
		QObject<?> newObject = upstream == null ? null : upstream.get( );
		if( object != newObject )
		{
			if( object != null )
			{
				object.changeSupport( ).removePropertyChangeListener( attribute , this );
			}
			object = newObject;
			if( newObject != null )
			{
				newObject.changeSupport( ).addPropertyChangeListener( attribute , this );
			}
		}
		T newValue = object == null ? null : object.get( attribute );
		if( force || !Java7.Objects.equals( value , newValue ) )
		{
			value = ( T ) newValue;
			updateDownstream( force );
		}
	}
	
	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		if( source == object && property == attribute )
		{
			update( false );
		}
	}
}
