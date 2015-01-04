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

import java.util.HashSet;

import org.andork.func.Mapper;

public class QHashSet<E> extends QSet<E, HashSet<E>>
{
	public static <E> QHashSet<E> newInstance( )
	{
		return new QHashSet<E>( );
	}
	
	@Override
	protected HashSet<E> createCollection( )
	{
		return new HashSet<E>( );
	}
	
	@Override
	public QHashSet<E> deepClone( Mapper<Object, Object> childMapper )
	{
		QHashSet<E> result = newInstance( );
		for( E elem : this )
		{
			result.add( ( E ) childMapper.map( elem ) );
		}
		return result;
	}
}
