package org.andork.q2;

import org.andork.func.Mapper;
import org.andork.q2.QSpec.Property;

/**
 * {@code QObject} (along with {@link QSpec}) provides the closest functionality
 * to reflection on a POJO that is possible without actually using reflection.
 * It also provides property {@linkplain #changeSupport() change support} that
 * will notify listeners of any property changes, so that you don't have to
 * write any boilerplate property change notification code.<br>
 * <br>
 * The Q doesn't stand for anything.
 * 
 * @author andy.edwards
 * @param <S>
 *            the type of {@link QSpec} for this object.
 */
public final class QArrayObject<S extends QSpec> extends QObject<S>
{
	Object[ ]	values;

	public QArrayObject( S spec )
	{
		super( spec );
		values = new Object[ spec.properties.length ];
		for( int i = 0 ; i < values.length ; i++ )
		{
			values[ i ] = spec.properties[ i ].initValue( );
		}
	}

	public static <S extends QSpec> QArrayObject<S> create( S spec )
	{
		return new QArrayObject<S>( spec );
	}

	public <T> T get( Property<T> property )
	{
		return property.get( this );
	}

	public <T> T set( Property<T> property , T newValue )
	{
		return property.set( this , newValue );
	}

	@Override
	public QArrayObject<S> deepClone( Mapper<Object, Object> childMapper )
	{
		QArrayObject<S> result = new QArrayObject<S>( spec );
		for( int i = 0 ; i < values.length ; i++ )
		{
			result.values[ i ] = childMapper.map( values[ i ] );
		}
		return result;
	}
}