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
package org.andork.awt;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;

public class AWTUtil
{
	/**
	 * Convenience method for searching above <code>comp</code> in the component hierarchy and returns the first object of class <code>c</code> it finds. Can
	 * return {@code null}, if a class <code>c</code> cannot be found.
	 */
	public static <C extends Container> C getAncestorOfClass( Class<C> c , Component comp )
	{
		if( comp == null || c == null )
			return null;
		
		Container parent = comp.getParent( );
		while( parent != null && !( c.isInstance( parent ) ) )
			parent = parent.getParent( );
		return c.cast( parent );
	}
	
	public static void traverse( Component root , Consumer<Component> consumer )
	{
		LinkedList<Component> queue = new LinkedList<Component>( );
		queue.add( root );
		
		while( !queue.isEmpty( ) )
		{
			Component c = queue.poll( );
			consumer.accept( c );
			if( c instanceof Container )
			{
				queue.addAll( Arrays.asList( ( ( Container ) c ).getComponents( ) ) );
			}
		}
	}
}
