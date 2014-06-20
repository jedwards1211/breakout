package org.andork.func;

import java.math.BigInteger;

public class BigIntegerBimapper implements Bimapper<BigInteger, Object>
{
	public static final BigIntegerBimapper	instance	= new BigIntegerBimapper();

	private BigIntegerBimapper() {

	}

	public Object map(BigInteger t)
	{
		return t;
	}

	public BigInteger unmap(Object s)
	{
		if (s instanceof BigInteger) {
			return (BigInteger) s;
		}
		return s == null ? null : new BigInteger(s.toString());
	}
}