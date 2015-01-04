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
import java.util.Collection;

import org.andork.func.Mapper;

public class QArrayList<E> extends QList<E, ArrayList<E>>
{
	public static <E> QArrayList<E> newInstance( )
	{
		return new QArrayList<E>( );
	}
	
	public static <E> QArrayList<E> newInstance( Collection<? extends E> c )
	{
		QArrayList<E> result = newInstance( );
		result.addAll( c );
		return result;
	}
	
	@Override
	protected ArrayList<E> createCollection( )
	{
		return new ArrayList<E>( );
	}
	
	public void trimToSize( )
	{
		collection.trimToSize( );
	}
	
	public void ensureCapacity( int minCapacity )
	{
		collection.ensureCapacity( minCapacity );
	}
	
	@Override
	public QArrayList<E> deepClone( Mapper<Object, Object> childMapper )
	{
		QArrayList<E> result = newInstance( );
		for( E elem : this )
		{
			result.add( ( E ) childMapper.map( elem ) );
		}
		return result;
	}
}
