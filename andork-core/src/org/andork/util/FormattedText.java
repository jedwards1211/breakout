package org.andork.util;

public class FormattedText
{
	String		text;
	Object		value;
	Format		format;
	Exception	formatException;
	
	public FormattedText( )
	{
		
	}
	
	public FormattedText( Format format )
	{
		this.format = format;
		format( );
	}
	
	public String getText( )
	{
		return text;
	}
	
	public void setText( String text )
	{
		this.text = text;
		format( );
	}
	
	public Format getFormat( )
	{
		return format;
	}
	
	public void setFormat( Format format )
	{
		if( this.format != format )
		{
			this.format = format;
			format( );
		}
	}
	
	private void format( )
	{
		if( format != null )
		{
			try
			{
				value = format.parse( text );
				formatException = null;
			}
			catch( FormatWarning warning )
			{
				value = warning.getFormattedValue( );
				formatException = warning;
			}
			catch( Exception ex )
			{
				value = null;
				formatException = ex;
			}
		}
		else
		{
			value = null;
			formatException = null;
		}
	}
	
	public Object getValue( )
	{
		return value;
	}
	
	public Exception getFormatException( )
	{
		return formatException;
	}
	
	public String toString( )
	{
		return value == null || format == null ? text : format.format( value );
	}
}
