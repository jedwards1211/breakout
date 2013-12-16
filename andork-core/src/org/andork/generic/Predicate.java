
package org.andork.generic;

public interface Predicate<P, R>
{
	public R eval( P param );
}
