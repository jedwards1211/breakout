package org.andork.func;

public class ToStringMapper implements Mapper<Object, String>
{
	public static final ToStringMapper	instance	= new ToStringMapper( );
	
	private ToStringMapper( )
	{
		
	}
	
	@Override
	public String map( Object in )
	{
		return in == null ? null : in.toString( );
	}
}
