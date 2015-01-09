package org.andork.q2;

import java.util.HashMap;
import java.util.Map;

import org.andork.bind2.Binder;
import org.andork.bind2.Binding;
import org.andork.bind2.CachingBinder;
import org.andork.bind2.Link;
import org.andork.q2.QSpec.Property;

public class QObjectBinder<S extends QSpec> extends CachingBinder<QObject<S>> implements Binding , QObjectListener
{
	public final Link<QObject<S>>						objLink		= new Link<QObject<S>>( this );
	public final S										spec;
	private final Map<Property<?>, PropertyBinder<?>>	properties	= new HashMap<>( );

	public QObjectBinder( S spec )
	{
		this.spec = spec;
	}

	public static <S extends QSpec> QObjectBinder<S> create( S spec )
	{
		return new QObjectBinder<S>( spec );
	}

	public <T> Binder<T> property( Property<T> property )
	{
		@SuppressWarnings( "unchecked" )
		PropertyBinder<T> binder = ( PropertyBinder<T> ) properties.get( property.requirePropertyOf( spec ) );
		if( binder == null )
		{
			binder = new PropertyBinder<>( );
			QObject<S> curObject = get( );
			if( curObject != null )
			{
				binder.set( curObject.get( property ) , false );
			}
			properties.put( property , binder );
		}
		return binder;
	}

	@SuppressWarnings( { "unchecked" , "rawtypes" } )
	public void update( boolean force )
	{
		QObject<S> curObject = get( );
		QObject<S> newObject = objLink.get( );
		if( curObject != newObject )
		{
			if( curObject != null )
			{
				curObject.removeListener( this );
			}
			set( newObject );
			if( newObject != null )
			{
				newObject.addListener( this );
			}

			updateBindings( force );

			for( Map.Entry<Property<?>, PropertyBinder<?>> entry : properties.entrySet( ) )
			{
				Property<?> property = entry.getKey( );
				PropertyBinder binder = entry.getValue( );
				binder.set( newObject == null ? null : newObject.get( property ) , force );
			}
		}
	}

	public void
		objectChanged( QObject<?> source , Property<?> property , Object oldValue , Object newValue )
	{
		PropertyBinder binder = properties.get( property );
		if( binder != null )
		{
			binder.set( newValue , false );
		}
	}

	public static class PropertyBinder<T> extends CachingBinder<T>
	{
		@Override
		protected void set( T newValue )
		{
			super.set( newValue );
		}

		@Override
		protected void set( T newValue , boolean forceUpdates )
		{
			super.set( newValue , forceUpdates );
		}
	}
}
