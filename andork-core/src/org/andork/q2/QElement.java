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
import java.util.function.Consumer;

import org.andork.func.Mapper;

public abstract class QElement
{
	private Object	listeners;

	public abstract QElement deepClone( Mapper<Object, Object> childMapper );

	@SuppressWarnings( "unchecked" )
	protected void addListener( QListener listener )
	{
		if( listeners instanceof List )
		{
			List<QListener> casted = ( List<QListener> ) listeners;
			if( !casted.contains( listener ) )
			{
				casted.add( listener );
			}
		}
		else if( listeners instanceof QListener && !listeners.equals( listener ) )
		{
			ArrayList<QListener> newList = new ArrayList<>( 2 );
			newList.add( ( QListener ) listeners );
			newList.add( listener );
			listeners = newList;
		}
		else
		{
			listeners = listener;
		}
	}

	@SuppressWarnings( "unchecked" )
	protected void removeListener( QListener listener )
	{
		if( listeners instanceof List )
		{
			List<QListener> casted = ( List<QListener> ) listeners;
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
		else if( listeners instanceof QListener )
		{
			if( listeners.equals( listener ) )
			{
				listeners = null;
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	protected void forEachListener( Consumer<QListener> consumer )
	{
		if( listeners instanceof List )
		{
			for( QListener listener : ( List<QListener> ) listeners )
			{
				consumer.accept( listener );
			}
		}
		else if( listeners instanceof QListener )
		{
			consumer.accept( ( QListener ) listeners );
		}
	}

	protected <L extends QListener> void forEachListener( Class<? extends L> type , Consumer<L> consumer )
	{
		forEachListener( l ->
		{
			if( type.isInstance( l ) )
			{
				consumer.accept( type.cast( l ) );
			}
		} );
	}
}
