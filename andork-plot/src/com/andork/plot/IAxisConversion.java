
package com.andork.plot;

/**
 * A conversion from one axis coordinate system to another, normally from data to screen. The implementation
 * should be a continuous monotonic function defined for all real numbers.
 * 
 * @author andy.edwards
 */
public interface IAxisConversion
{
	public abstract double convert( double d );
	
	public abstract double invert( double d );
}
