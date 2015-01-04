package org.andork.q2;

import org.andork.bind2.Binding;
import org.andork.bind2.CachingBinder;
import org.andork.bind2.Link;
import org.andork.event.BasicPropertyChangeListener;
import org.andork.q2.QSpec.Property;

public class QObjectPropertyBinder<T> extends CachingBinder<T> implements Binding , BasicPropertyChangeListener
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
				curObject.removePropertyChangeListener( this );
			}
			curObject = newObject;
			if( newObject != null )
			{
				newObject.addPropertyChangeListener( this );
			}
		}

		T newValue = newObject == null ? null : property.get( newObject );

		set( newValue , force );
	}

	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		update( false );
	}
}
