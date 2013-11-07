
package com.andork.plot;

public interface ITracePlotter
{
	
	public abstract void reset( );
	
	public abstract void addPoint( double domain , double value , ITraceRenderer traceRenderer , IAxisConversion domainConversion , IAxisConversion valueConversion );
	
	public abstract void flush( ITraceRenderer traceRenderer , IAxisConversion domainConversion , IAxisConversion valueConversion );
}
