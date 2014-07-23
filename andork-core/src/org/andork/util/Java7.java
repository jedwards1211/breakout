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

public class Java7
{
	private Java7( )
	{
		
	}
	
	public static class Objects
	{
		private Objects( )
		{
			
		}
		
		/**
		 * Returns {@code true} if the arguments are equal to each other and {@code false} otherwise. Consequently, if both arguments are {@code null},
		 * {@code true} is returned and if exactly one argument is {@code null}, {@code false} is returned. Otherwise, equality is determined by using the
		 * {@link Object#equals equals} method of the first argument.
		 * 
		 * @param a
		 *            an object
		 * @param b
		 *            an object to be compared with {@code a} for equality
		 * @return {@code true} if the arguments are equal to each other and {@code false} otherwise
		 * @see Object#equals(Object)
		 */
		public static boolean equals( Object a , Object b )
		{
			return ( a == b ) || ( a != null && a.equals( b ) );
		}
		
	}
	
}
