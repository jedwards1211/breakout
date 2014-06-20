package org.andork.func;

public class BooleanBimapper implements Bimapper<Boolean, Object>
{
	public static final BooleanBimapper	instance	= new BooleanBimapper();

	private BooleanBimapper() {

	}

	public Object map(Boolean t)
	{
		return t;
	}

	public Boolean unmap(Object s)
	{
		if (s instanceof Boolean) {
			return (Boolean) s;
		}
		return s == null ? null : Boolean.valueOf(s.toString());
	}
}