package org.andork.func;

import java.util.Iterator;
import java.util.stream.Stream;

public class StreamIterable<T> implements Iterable<T>
{
	Stream<T>	stream;
	
	private StreamIterable( Stream<T> stream )
	{
		super( );
		this.stream = stream;
	}
	
	public static <T> StreamIterable<T> iterable( Stream<T> stream )
	{
		return new StreamIterable<>( stream );
	}
	
	@Override
	public Iterator<T> iterator( )
	{
		return stream.iterator( );
	}
}
