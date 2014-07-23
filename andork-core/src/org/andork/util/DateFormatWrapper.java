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
package org.andork.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatWrapper implements Format<Date>
{
	DateFormat	wrapped;
	
	public DateFormatWrapper( DateFormat wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	public DateFormatWrapper( String format )
	{
		this( new SimpleDateFormat( format ) );
	}
	
	@Override
	public String format( Date t )
	{
		if( t == null )
		{
			return null;
		}
		return wrapped.format( t );
	}
	
	@Override
	public Date parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		try
		{
			return wrapped.parse( s );
		}
		catch( Exception e )
		{
			throw new IllegalArgumentException( "Invalid format" , e );
		}
	}
}
