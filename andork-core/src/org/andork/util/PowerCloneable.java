package org.andork.util;

import java.util.IdentityHashMap;
import java.util.function.Function;

/**
 * A general-purpose replacement to the cruftlike {@link Object#clone()} method.
 * 
 * @author James
 */
public interface PowerCloneable
{
	/**
	 * Clones this object. The returned object should not be {@code ==} to this object, but should {@code #equals(this)}
	 * .
	 * 
	 * @param subcloner
	 *            a function that clones fields or other "elements" of this object. Being able to choose the function
	 *            allows you to perform different styles of cloning, from a basic
	 *            "return clone() if it's a PowerCloneable, otherwise return the same object"
	 *            to a {@link Function} using an {@link IdentityHashMap} to preserve graph structure.
	 * @return A clone of this object.
	 */
	public PowerCloneable clone( Function<Object, Object> subcloner );

	/**
	 * Various hierarchical cloning functions.
	 * 
	 * @author James
	 */
	public static class Cloners
	{
		/**
		 * The default subcloner implementation.
		 * 
		 * @param toClone
		 *            the object to clone.
		 * @return if {@code toClone} is a {@link PowerCloneable}, returns
		 *         {@code toClone.clone( Cloners::defaultClone )}.
		 *         Otherwise, returns {@code toClone}.
		 */
		public static Object defaultClone( Object toClone )
		{
			if( toClone instanceof PowerCloneable )
			{
				return ( ( PowerCloneable ) toClone ).clone( Cloners::defaultClone );
			}
			return toClone;
		}

		/**
		 * Clones an array of Objects.
		 * 
		 * @param toClone
		 *            the array to clone.
		 * @param subcloner
		 *            the subcloner used to clone each element of the array.
		 * @return
		 *         a new array of the same length as {@code toClone} where each element was the result of applying
		 *         {@code subcloner} to the corresponding element in {@code toClone}.
		 */
		public static Object[ ] cloneArray( Object[ ] toClone , Function<Object, Object> subcloner )
		{
			Object[ ] result = new Object[ toClone.length ];
			for( int i = 0 ; i < toClone.length ; i++ )
			{
				result[ i ] = subcloner.apply( toClone[ i ] );
			}
			return result;
		}
	}
}
