package org.andork.q;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andork.func.Bimapper;

public class QMapBimapper<K, V> implements Bimapper<QMap<K, V, ?>, Object>
{
	private Bimapper<K, Object>	keyBimapper;
	private Bimapper<V, Object>	valueBimapper;
	private static final Logger	LOGGER	= Logger.getLogger( QObjectMapBimapper.class.getName( ) );
	
	private QMapBimapper( Bimapper<K, Object> keyBimapper , Bimapper<V, Object> valueBimapper )
	{
		super( );
		this.keyBimapper = keyBimapper;
		this.valueBimapper = valueBimapper;
	}
	
	public static <K, V> QMapBimapper<K, V> newInstance( Bimapper<K, Object> keyBimapper , Bimapper<V, Object> valueBimapper )
	{
		return new QMapBimapper<K, V>( keyBimapper , valueBimapper );
	}
	
	@Override
	public Object map( QMap<K, V, ?> in )
	{
		if( in == null )
		{
			return null;
		}
		Map<Object, Object> result = new LinkedHashMap<Object, Object>( );
		for( Map.Entry<K, V> entry : in.entrySet( ) )
		{
			result.put( keyBimapper == null ? entry.getKey( ) : keyBimapper.map( entry.getKey( ) ) ,
					valueBimapper == null ? entry.getValue( ) : valueBimapper.map( entry.getValue( ) ) );
		}
		return result;
	}
	
	@Override
	public QMap<K, V, ?> unmap( Object out )
	{
		if( out == null )
		{
			return null;
		}
		
		Map<?, ?> m = ( Map<?, ?> ) out;
		QLinkedHashMap<K, V> result = QLinkedHashMap.newInstance( );
		for( Map.Entry<?, ?> entry : m.entrySet( ) )
		{
			try
			{
				K key = ( K ) ( keyBimapper == null ? entry.getKey( ) : keyBimapper.unmap( entry.getKey( ) ) );
				V value = ( V ) ( valueBimapper == null ? entry.getValue( ) : valueBimapper.unmap( entry.getValue( ) ) );
				result.put( key , value );
			}
			catch( Throwable t )
			{
				LOGGER.log( Level.WARNING , "Failed to add entry: " + entry , t );
			}
		}
		
		return result;
	}
	
}
