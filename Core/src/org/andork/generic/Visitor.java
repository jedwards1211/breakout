
package org.andork.generic;

public interface Visitor<P, R>
{
	public R visit( P param );
}
