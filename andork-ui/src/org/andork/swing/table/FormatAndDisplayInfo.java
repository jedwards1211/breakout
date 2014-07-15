package org.andork.swing.table;

import javax.swing.Icon;

import org.andork.util.Format;

public class FormatAndDisplayInfo<T> implements Format<T>
{
	private final Format<T>	wrapped;
	private final String	description;
	private final String	name;
	private final Icon		icon;
	
	public FormatAndDisplayInfo( Format<T> wrapped  , String name  , String description  , Icon icon  )
	{
		super( );
		this.wrapped = wrapped;
		this.description = description;
		this.name = name;
		this.icon = icon;
	}
	
	public Format<T> format( )
	{
		return wrapped;
	}
	
	public String description( )
	{
		return description;
	}
	
	public String name( )
	{
		return name;
	}
	
	public Icon icon( )
	{
		return icon;
	}
	
	@Override
	public String format( T t )
	{
		return wrapped.format( t );
	}
	
	@Override
	public T parse( String s ) throws Exception
	{
		return wrapped.parse( s );
	}
}
