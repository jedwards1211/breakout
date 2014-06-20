package org.andork.func;

public class StringBimapper implements Bimapper<String, Object>
{
	public static final StringBimapper	instance	= new StringBimapper();

	private StringBimapper() {

	}

	@Override
	public Object map(String in)
	{
		return in;
	}

	@Override
	public String unmap(Object out)
	{
		return out == null ? null : out.toString();
	}
}
