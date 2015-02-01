package org.breakout.parse;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class DoubleNumberFormat implements GenericFormat<Double>
{
	private final NumberFormat	wrapped;

	public DoubleNumberFormat( NumberFormat wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}

	@Override
	public Double parseObject( String source , ParsePosition position )
	{
		Number n = wrapped.parse( source , position );
		return n == null ? null : n.doubleValue( );
	}
}
