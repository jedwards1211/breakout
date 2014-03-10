package org.andork.snakeyaml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.andork.func.Bimapper;

public class YamlArrayList<E> extends YamlList<E>
{
	private YamlArrayList( Bimapper<? super E, Object> format )
	{
		super( format );
	}
	
	public static <E> YamlArrayList<E> newInstance( Bimapper<? super E, Object> format )
	{
		return new YamlArrayList<E>( format );
	}
	
	@Override
	protected Collection<E> createCollection( )
	{
		return new ArrayList<E>( );
	}
	
	public static <E> YamlArrayList<E> fromYaml( List<?> array , Bimapper<? super E, Object> format ) throws Exception
	{
		YamlArrayList<E> list = newInstance( format );
		fromYaml( array , list );
		return list;
	}
}
