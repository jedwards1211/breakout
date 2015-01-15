package org.andork.breakout.table;

import java.util.function.Function;

import org.andork.util.PowerCloneable;

/**
 * Measurements of a passage's cross section shape at a given survey station. There are several subtypes.
 * 
 * @author James
 */
public abstract class XSection implements PowerCloneable
{
	protected abstract XSection baseClone( );

	public abstract XSection clone( Function<Object, Object> subcloner );
}
