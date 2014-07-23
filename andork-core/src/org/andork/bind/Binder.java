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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Binder<T>
{
	private final LinkedList<Binder<?>>	downstream				= new LinkedList<Binder<?>>( );
	private final List<Binder<?>>		unmodifiableDownstream	= Collections.unmodifiableList( downstream );
	
	public abstract T get( );
	
	public abstract void set( T newValue );
	
	public List<Binder<?>> getDownstream( )
	{
		return unmodifiableDownstream;
	}
	
	public abstract void update( boolean force );
	
	protected static void bind0( Binder<?> upstream , Binder<?> downstream )
	{
		if( !upstream.downstream.contains( downstream ) )
		{
			upstream.downstream.add( downstream );
		}
	}
	
	protected static void unbind0( Binder<?> upstream , Binder<?> downstream )
	{
		upstream.downstream.remove( downstream );
	}
	
	protected final void updateDownstream( boolean force )
	{
		for( Binder<?> binder : downstream )
		{
			binder.update( force );
		}
	}
}
