package org.andork.util;

/**
 * Converts objects of some type value to and from {@link String}s.
 * 
 * @author james.a.edwards
 * 
 * @param <T>
 *            the type of the objects to format.
 */
public interface Format<T>
{
	public String format( T t );
	
	public T parse( String s ) throws Exception;
}