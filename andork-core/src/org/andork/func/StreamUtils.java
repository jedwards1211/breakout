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

import java.util.function.BiConsumer;
import java.util.stream.Stream;
import static org.andork.func.StreamIterable.*;

public class StreamUtils
{
	public static <T> void forEachPairLooped( Stream<T> stream , BiConsumer<T, T> consumer )
	{
		T first = null;
		T previous = null;
		
		for( T t : iterable( stream ) )
		{
			if( first == null )
			{
				first = t;
			}
			else
			{
				consumer.accept( previous , t );
			}
			previous = t;
		}
		
		if( previous != null && first != null )
		{
			consumer.accept( previous , first );
		}
	}
}