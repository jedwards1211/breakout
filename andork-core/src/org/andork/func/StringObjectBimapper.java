package org.andork.func;

public class StringObjectBimapper implements Bimapper<String, Object>
{
	public static final StringObjectBimapper	instance	= new StringObjectBimapper( );
	
	@Override
	public Object map( String in )
	{
		return in;
	}
	
	@Override
	public String unmap( Object out )
	{
		return out == null ? null : out.toString( );
	}
}
