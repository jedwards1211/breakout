package org.andork.format;

import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.andork.util.StringUtils;

public class CollectionFormat<T, C extends Collection<T>> implements Format<C>
{
	private char		delimiter;
	private Format<T>	itemFormat;
	Supplier<C>			collectionSupplier;
	
	public static void main( String[ ] args ) throws Exception
	{
		CollectionFormat<String, Set<String>> format = new CollectionFormat<String, Set<String>>( ',' , StringFormat.instance ,
				( ) -> new HashSet<String>( ) );
		System.out.println( format.parse( "  a , b,  \"cbd\"\"\"\"\"abc, e," ) );
	}
	
	public CollectionFormat( char delimiter , Format<T> itemFormat , Supplier<C> collectionSupplier )
	{
		super( );
		this.delimiter = delimiter;
		this.itemFormat = itemFormat;
		this.collectionSupplier = collectionSupplier;
	}
	
	@Override
	public String format( C t )
	{
		StringBuilder sb = new StringBuilder( );
		for( T item : t )
		{
			String s = itemFormat.format( item );
			if( sb.length( ) > 0 )
			{
				sb.append( delimiter ).append( ' ' );
			}
			if( s.indexOf( delimiter ) >= 0 )
			{
				sb.append( '"' ).append( s.replaceAll( "\"" , "\"\"" ) ).append( '"' );
			}
			else
			{
				sb.append( s );
			}
		}
		return sb.toString( );
	}
	
	@Override
	public C parse( String s ) throws Exception
	{
		C result = collectionSupplier.get( );
		StringCharacterIterator i = new StringCharacterIterator( s );
		
		String item;
		while( ( item = nextItem( i ) ) != null )
		{
			result.add( itemFormat.parse( item ) );
		}
		return result;
	}
	
	private String nextItem( StringCharacterIterator i )
	{
		StringBuilder sb = null;
		
		while( i.current( ) != '\uffff' )
		{
			if( Character.isWhitespace( i.current( ) ) )
			{
				i.next( );
			}
			else if( i.current( ) == delimiter )
			{
				i.next( );
				break;
			}
			else
			{
				if( sb == null )
				{
					sb = new StringBuilder( );
				}
				if( i.current( ) == '"' )
				{
					readUntilEndQuote( i , sb );
				}
				else
				{
					sb.append( i.current( ) );
					i.next( );
				}
			}
		}
		
		return StringUtils.toStringOrNull( sb );
	}
	
	private void readUntilEndQuote( StringCharacterIterator i , StringBuilder sb )
	{
		while( true )
		{
			char c = i.next( );
			if( c == '"' )
			{
				char c2 = i.next( );
				if( c2 != '"' )
				{
					return;
				}
			}
			sb.append( c );
		}
	}
}
