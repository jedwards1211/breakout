package org.andork.q2;

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
public abstract class QObject<S extends QSpec> extends QElement
{
	S	spec;

	public QObject( S spec )
	{
		this.spec = spec;
	}

	public S spec( )
	{
		return spec;
	}

	public abstract <T> T get( Property<T> property );

	public abstract <T> T set( Property<T> property , T newValue );

	public boolean equals( Object other )
	{
		return spec.equals( this , other );
	}

	public int hashCode( )
	{
		return spec.hashCode( this );
	}
}
