package org.andork.awt;

@SuppressWarnings( "serial" )
public class LocalizedException extends Exception
{
	private final String	key;
	private final Object[ ]	args;
	
	public LocalizedException( String key )
	{
		this( key , ( Object[ ] ) null );
	}
	
	public LocalizedException( String key , Object ... args )
	{
		this.key = key;
		this.args = args;
	}
	
	public String getKey( )
	{
		return key;
	}
	
	public Object[ ] getArgs( )
	{
		return args;
	}
}
