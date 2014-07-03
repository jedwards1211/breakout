package org.andork.collect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.andork.util.IterableUtils;

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
	
	public static Stream<Path> grepFiles( String expr )
	{
		int starIndex = expr.indexOf( '*' );
		if( starIndex < 0 )
		{
			return Stream.<Path>builder( ).add( Paths.get( expr ) ).build( );
		}
		Path basePath = Paths.get( expr.substring( 0 , starIndex ) );
		String regex = "^.*" + expr.substring( starIndex ).replaceAll( "\\*" , ".*" ) + "$";
		
		try
		{
			return Files.find( basePath , 10 , ( path , attributes ) -> path.toString( ).matches( regex ) );
		}
		catch( IOException e )
		{
			e.printStackTrace( );
			return Stream.<Path>builder( ).build( );
		}
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
				for( Path path : IterableUtils.iterable( grepFiles( s ).iterator( ) ) )
				{
					try
					{
						FileInputStream in = new FileInputStream( path.toFile( ) );
						chain = chain.add( new InputStreamLineIterable( in ) );
					}
					catch( Exception ex )
					{
						
					}
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
	
	public static Stream<Matcher> grep( File in , Pattern p )
	{
		return grep( StreamSupport.stream( LineIterable.linesOf( in ).spliterator( ) , false ) , p );
	}
	
	public static Stream<Matcher> grep( String diamondArg , Pattern p )
	{
		return grep( StreamSupport.stream( perlDiamond( diamondArg ).spliterator( ) , false ) , p );
	}
	
	public static Stream<Matcher> grep( InputStream in , Pattern p )
	{
		return grep( StreamSupport.stream( linesOf( in ).spliterator( ) , false ) , p );
	}
	
	public static Stream<Matcher> grep( Stream<String> in , Pattern p )
	{
		return in.map( s -> {
			Matcher m = p.matcher( s );
			return m.find( ) ? m : null;
		} ).filter( m -> m != null );
	}
}
