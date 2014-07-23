package org.andork.spec.json;

import java.util.ArrayList;
import java.util.Collection;

import org.andork.spec.json.JsonSpec.Format;

import com.google.gson.JsonArray;

public class JsonSpecArrayList<E> extends JsonSpecList<E>
{
	private JsonSpecArrayList( Format<? super E> format )
	{
		super( format );
	}
	
	public static <E> JsonSpecArrayList<E> newInstance( Format<? super E> format )
	{
		return new JsonSpecArrayList<E>( format );
	}
	
	@Override
	protected Collection<E> createCollection( )
	{
		return new ArrayList<E>( );
	}
	
	public static <E> JsonSpecArrayList<E> fromJson( JsonArray array , Format<? super E> format ) throws Exception
	{
		JsonSpecArrayList<E> list = newInstance( format );
		fromJson( array , list );
		return list;
	}
}
