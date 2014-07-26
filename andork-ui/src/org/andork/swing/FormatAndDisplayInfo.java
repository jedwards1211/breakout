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
package org.andork.swing;

import javax.swing.Icon;

import org.andork.format.Format;

public class FormatAndDisplayInfo<T> implements Format<T>
{
	private final Format<T>	wrapped;
	private final String	description;
	private final String	name;
	private final Icon		icon;
	
	public FormatAndDisplayInfo( Format<T> wrapped  , String name  , String description  , Icon icon  )
	{
		super( );
		this.wrapped = wrapped;
		this.description = description;
		this.name = name;
		this.icon = icon;
	}
	
	public Format<T> format( )
	{
		return wrapped;
	}
	
	public String description( )
	{
		return description;
	}
	
	public String name( )
	{
		return name;
	}
	
	public Icon icon( )
	{
		return icon;
	}
	
	@Override
	public String format( T t )
	{
		return wrapped.format( t );
	}
	
	@Override
	public T parse( String s ) throws Exception
	{
		return wrapped.parse( s );
	}
}
