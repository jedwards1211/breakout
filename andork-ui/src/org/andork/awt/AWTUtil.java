package org.andork.awt;

import java.awt.Component;
import java.awt.Container;

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
}
