package org.andork.util;

@SuppressWarnings( "serial" )
public class FormatWarning extends Exception
{
	final Object	formattedValue;
	
	public FormatWarning( String message , Object formattedValue )
	{
		super( message );
		this.formattedValue = formattedValue;
	}
	
	public Object getFormattedValue( )
	{
		return formattedValue;
	}
}
