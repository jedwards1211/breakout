package org.andork.collect;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

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
public class InputStreamLineIterable implements Iterable<String>
{
	InputStream	in;
	
	public InputStreamLineIterable( InputStream in )
	{
		this.in = in;
	}
	
	@Override
	public Iterator<String> iterator( )
	{
		return new LineIterator( in );
	}
	
	public static InputStreamLineIterable linesOf( InputStream in )
	{
		return new InputStreamLineIterable( in );
	}
	
	public static Iterable<String> perlDiamond( String ... args )
	{
		if( args.length == 0 )
		{
			return new InputStreamLineIterable( System.in );
		}
		else
		{
			IterableChain<String> chain = new IterableChain<String>( );
			for( String s : args )
			{
				try
				{
					FileInputStream in = new FileInputStream( s );
					chain = chain.add( new InputStreamLineIterable( in ) );
				}
				catch( Exception ex )
				{
					
				}
			}
			return chain;
		}
	}

	public static Iterable<String> perlDiamond( int startIndex , String ... args )
	{
		if( args.length - startIndex == 0 )
		{
			return new InputStreamLineIterable( System.in );
		}
		else
		{
			IterableChain<String> chain = new IterableChain<String>( );
			for( int i = startIndex ; i < args.length ; i++ )
			{
				try
				{
					FileInputStream in = new FileInputStream( args[ i ] );
					chain = chain.add( new InputStreamLineIterable( in ) );
				}
				catch( Exception ex )
				{
					
				}
			}
			return chain;
		}
	}
}
