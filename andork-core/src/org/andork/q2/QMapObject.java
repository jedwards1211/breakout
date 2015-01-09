package org.andork.q2;

import java.util.Map;

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
public abstract class QMapObject<S extends QSpec> extends QObject<S>
{
	final Map<Property<?>, Object>	values;

	public QMapObject( S spec )
	{
		super( spec );
		values = createValuesMap( );
		for( Property<?> property : spec.propertyList )
		{
			Object initValue = property.initValue( ).get( );
			if( initValue != null )
			{
				values.put( property , initValue );
			}
		}
	}

	protected abstract Map<Property<?>, Object> createValuesMap( );

	public <T> T get( Property<T> property )
	{
		return property.get( this );
	}

	public <T> T set( Property<T> property , T newValue )
	{
		return property.set( this , newValue );
	}
}
