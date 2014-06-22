package org.andork.awt;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.LinkedList;

import org.andork.collect.Visitor;

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
	
	public static void traverse( Component root , Visitor<Component> visitor )
	{
		LinkedList<Component> queue = new LinkedList<Component>( );
		queue.add( root );
		
		while( !queue.isEmpty( ) )
		{
			Component c = queue.poll( );
			if( !visitor.visit( c ) )
			{
				return;
			}
			if( c instanceof Container )
			{
				queue.addAll( Arrays.asList( ( ( Container ) c ).getComponents( ) ) );
			}
		}
	}
}
