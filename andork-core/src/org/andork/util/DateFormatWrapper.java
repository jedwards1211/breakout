package org.andork.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatWrapper implements Format<Date>
{
	DateFormat	wrapped;
	
	public DateFormatWrapper( DateFormat wrapped )
	{
		super( );
		this.wrapped = wrapped;
	}
	
	public DateFormatWrapper( String format )
	{
		this( new SimpleDateFormat( format ) );
	}
	
	@Override
	public String format( Date t )
	{
		if( t == null )
		{
			return null;
		}
		return wrapped.format( t );
	}
	
	@Override
	public Date parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		try
		{
			return wrapped.parse( s );
		}
		catch( Exception e )
		{
			throw new IllegalArgumentException( "Invalid format" , e );
		}
	}
}
