package org.breakout.parse;

import java.text.Format;
import java.text.ParsePosition;

/**
 * Interface for lambda wrappers of {@link Format}s that add a generic return type.
 * 
 * @author James
 *
 * @param <V>
 *            the return type of {@link #parseObject(String, ParsePosition)}.
 */
@FunctionalInterface
public interface GenericFormat<V>
{
	public V parseObject( String source , ParsePosition position );
}
