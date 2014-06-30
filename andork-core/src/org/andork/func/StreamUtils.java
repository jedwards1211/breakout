package org.andork.func;

import java.util.function.BiConsumer;
import java.util.stream.Stream;
import static org.andork.func.StreamIterable.*;

public class StreamUtils
{
	public static <T> void forEachPairLooped( Stream<T> stream , BiConsumer<T, T> consumer )
	{
		T first = null;
		T previous = null;
		
		for( T t : iterable( stream ) )
		{
			if( first == null )
			{
				first = t;
			}
			else
			{
				consumer.accept( previous , t );
			}
			previous = t;
		}
		
		if( previous != null && first != null )
		{
			consumer.accept( previous , first );
		}
	}
}
