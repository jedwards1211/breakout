package org.andork.bind;

import org.andork.event.BasicPropertyChangeListener;
import org.andork.q.QMap;

public class QMapKeyedBinder<K, V> extends Binder<V> implements BasicPropertyChangeListener
{
	Binder<? extends K>						keyBinder;
	Binder<? extends QMap<? super K, V, ?>>	mapBinder;
	
	K										key;
	QMap<? super K, ? super V, ?>			map;
	V										value;
	
	public static <K, V> QMapKeyedBinder<K, V> bindKeyed( Binder<? extends K> keyBinder , Binder<? extends QMap<? super K, V, ?>> mapBinder )
	{
		return new QMapKeyedBinder<K, V>( ).bind( keyBinder , mapBinder );
	}
	
	public QMapKeyedBinder<K, V> bind( Binder<? extends K> keyBinder , Binder<? extends QMap<? super K, V, ?>> mapBinder )
	{
		boolean update = this.keyBinder != keyBinder || this.mapBinder != mapBinder;
		
		if( this.keyBinder != keyBinder )
		{
			if( this.keyBinder != null )
			{
				unbind( this.keyBinder , this );
			}
			this.keyBinder = keyBinder;
			if( keyBinder != null )
			{
				bind( keyBinder , this );
			}
		}
		if( this.mapBinder != mapBinder )
		{
			if( this.mapBinder != null )
			{
				unbind( mapBinder , this );
			}
			this.mapBinder = mapBinder;
			if( mapBinder != null )
			{
				bind( mapBinder , this );
			}
		}
		
		if( update )
		{
			update( false );
		}
		return this;
	}
	
	public void unbind( )
	{
		bind( null , null );
	}
	
	@Override
	public V get( )
	{
		return value;
	}
	
	@Override
	public void set( V newValue )
	{
		if( map != null && key != null )
		{
			map.put( key , newValue );
		}
	}
	
	@Override
	public void update( boolean force )
	{
		K newKey = keyBinder == null ? null : keyBinder.get( );
		QMap<? super K, V, ?> newMap = mapBinder == null ? null : mapBinder.get( );
		V newValue = newKey == null || newMap == null ? null : newMap.get( newKey );
		
		if( key != newKey || map != newMap )
		{
			if( map != null && key != null )
			{
				map.changeSupport( ).removePropertyChangeListener( key , this );
			}
			map = newMap;
			key = newKey;
			if( newMap != null && newKey != null )
			{
				newMap.changeSupport( ).addPropertyChangeListener( newKey , this );
			}
		}
		if( force || value != newValue )
		{
			value = newValue;
			updateDownstream( force );
		}
	}
	
	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		if( source == map && property == key )
		{
			update( false );
		}
	}
}
