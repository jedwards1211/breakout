package org.andork.func;

import java.math.BigDecimal;

public class BigDecimalBimapper implements Bimapper<BigDecimal, Object>
{
	public static final BigDecimalBimapper	instance	= new BigDecimalBimapper();

	private BigDecimalBimapper() {

	}

	public Object map(BigDecimal t)
	{
		return t;
	}

	public BigDecimal unmap(Object s)
	{
		if (s instanceof BigDecimal) {
			return (BigDecimal) s;
		}
		return s == null ? null : new BigDecimal(s.toString());
	}
}