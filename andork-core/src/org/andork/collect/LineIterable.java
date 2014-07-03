package org.andork.collect;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Absolutely the easiest way to read a file or URL line-by-line:
 * 
 * <pre>
 * for( String line : new LineIterable( &quot;test.txt&quot; ) )
 * {
 * 	System.out.println( line );
 * }
 * </pre>
 * 
 * @author andy.edwards
 */
public class LineIterable implements Iterable<String>
{
	File	file;
	URL		url;
	
	public LineIterable( String file )
	{
		this( new File( file ) );
	}
	
	public LineIterable( File file )
	{
		super( );
		this.file = file;
	}
	
	public LineIterable( URL url )
	{
		super( );
		this.url = url;
	}
	
	@Override
	public Iterator<String> iterator( )
	{
		if( file != null )
		{
			return new LineIterator( file );
		}
		if( url != null )
		{
			return new LineIterator( url );
		}
		return null;
	}
	
	public static LineIterable linesOf( String file )
	{
		return new LineIterable( file );
	}
	
	public static LineIterable linesOf( File file )
	{
		return new LineIterable( file );
	}
	
	public static LineIterable linesOf( URL url )
	{
		return new LineIterable( url );
	}
}
