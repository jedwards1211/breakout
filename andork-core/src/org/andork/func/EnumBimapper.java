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
package org.andork.func;


public class EnumBimapper<E extends Enum<E>> implements Bimapper<E, Object>
{
	Class<E>	cls;
	
	private EnumBimapper( Class<E> cls )
	{
		this.cls = cls;
	}
	
	public static <E extends Enum<E>> EnumBimapper<E> newInstance( Class<E> cls )
	{
		return new EnumBimapper<E>( cls );
	}
	
	@Override
	public Object map( E t )
	{
		return t == null ? null : t.name( );
	}
	
	@Override
	public E unmap( Object s )
	{
		return s == null || s == null ? null : Enum.valueOf( cls , s.toString( ) );
	}
}