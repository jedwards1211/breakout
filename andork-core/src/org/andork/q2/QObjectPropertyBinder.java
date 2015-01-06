package org.andork.q2;

import org.andork.bind2.Binding;
import org.andork.bind2.CachingBinder;
import org.andork.bind2.Link;
import org.andork.q2.QSpec.Property;

public class QObjectPropertyBinder<T> extends CachingBinder<T> implements Binding , QObjectListener
{
	public final Link<QObject<?>>	objLink	= new Link<QObject<?>>( this );
	public final Property<T>		property;
	private QObject<?>				curObject;

	public QObjectPropertyBinder( Property<T> property )
	{
		this.property = property;
	}

	public static <T> QObjectPropertyBinder<T> create( Property<T> property )
	{
		return new QObjectPropertyBinder<T>( property );
	}

	public void update( boolean force )
	{
		QObject<?> newObject = objLink.get( );
		if( curObject != newObject )
		{
			if( curObject != null )
			{
				curObject.removeListener( this );
			}
			curObject = newObject;
			if( newObject != null )
			{
				newObject.addListener( this );
			}
		}

		T newValue = newObject == null ? null : newObject.get( property );

		set( newValue , force );
	}

	public void
		objectChanged( QObject<?> source  , Property<?> property  , Object oldValue  , Object newValue  )
	{
		update( false );
	}
}
