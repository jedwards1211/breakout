package org.andork.q2;

import java.util.HashMap;
import java.util.Map;

import org.andork.func.Mapper;
import org.andork.q2.QSpec.Property;

public class QHashMapObject<S extends QSpec> extends QMapObject<S>
{

	public QHashMapObject( S spec )
	{
		super( spec );
	}

	public static <S extends QSpec> QHashMapObject<S> create( S spec )
	{
		return new QHashMapObject<S>( spec );
	}

	@Override
	protected Map<Property<?>, Object> createValuesMap( )
	{
		return new HashMap<>( );
	}

	@Override
	public QElement deepClone( Mapper<Object, Object> childMapper )
	{
		QHashMapObject<S> result = new QHashMapObject<S>( spec );
		result.values.putAll( values );
		return result;
	}
}
