package org.andork.q;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.andork.func.BigDecimalBimapper;
import org.andork.func.BigIntegerBimapper;
import org.andork.func.Bimapper;
import org.andork.func.BooleanBimapper;
import org.andork.func.DoubleBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FloatArray2ListBimapper;
import org.andork.func.FloatBimapper;
import org.andork.func.IntegerBimapper;
import org.andork.func.LongBimapper;
import org.andork.func.StringBimapper;
import org.andork.q.QSpec.Attribute;

public class QObjectMapBimapper<S extends QSpec<S>> implements Bimapper<QObject<S>, Object>
{
	S			spec;
	
	Bimapper[ ]	attrBimappers;
	
	public QObjectMapBimapper( S spec )
	{
		this( spec , new Bimapper[ spec.getAttributeCount( ) ] );
		for( int i = 0 ; i < spec.getAttributeCount( ) ; i++ )
		{
			Class<?> valueClass = spec.attributeAt( i ).getValueClass( );
			if( valueClass == String.class )
			{
				attrBimappers[ i ] = StringBimapper.instance;
			}
			if( valueClass == Boolean.class )
			{
				attrBimappers[ i ] = BooleanBimapper.instance;
			}
			else if( valueClass == Integer.class )
			{
				attrBimappers[ i ] = IntegerBimapper.instance;
			}
			else if( valueClass == Long.class )
			{
				attrBimappers[ i ] = LongBimapper.instance;
			}
			else if( valueClass == Float.class )
			{
				attrBimappers[ i ] = FloatBimapper.instance;
			}
			else if( valueClass == float[ ].class )
			{
				attrBimappers[ i ] = FloatArray2ListBimapper.instance;
			}
			else if( valueClass.isEnum( ) )
			{
				attrBimappers[ i ] = EnumBimapper.newInstance( ( Class<Enum> ) valueClass );
			}
			else if( valueClass == Double.class )
			{
				attrBimappers[ i ] = DoubleBimapper.instance;
			}
			else if( valueClass == BigInteger.class )
			{
				attrBimappers[ i ] = BigIntegerBimapper.instance;
			}
			else if( valueClass == BigDecimal.class )
			{
				attrBimappers[ i ] = BigDecimalBimapper.instance;
			}
		}
	}
	
	public QObjectMapBimapper( S spec , Bimapper ... attrBimappers )
	{
		if( attrBimappers.length != spec.getAttributeCount( ) )
		{
			throw new IllegalArgumentException( "attrBimappers.length must equal spec.getAttributeCount()" );
		}
		this.spec = spec;
		this.attrBimappers = attrBimappers;
	}
	
	public QObjectMapBimapper<S> map( Attribute<?> attr , Bimapper bimapper )
	{
		attrBimappers[ attr.index ] = bimapper;
		return this;
	}
	
	public static <S extends QSpec<S>> QObjectMapBimapper<S> newInstance( S spec , Bimapper ... attrBimappers )
	{
		return new QObjectMapBimapper<S>( spec , attrBimappers );
	}
	
	@Override
	public Object map( QObject<S> in )
	{
		if( in == null )
		{
			return null;
		}
		Map<Object, Object> result = new LinkedHashMap<Object, Object>( );
		for( int i = 0 ; i < spec.getAttributeCount( ) ; i++ )
		{
			Attribute<?> attribute = spec.attributeAt( i );
			if( in.has( attribute ) )
			{
				Object value = in.get( attribute );
				result.put( attribute.getName( ) , value == null || attrBimappers[ i ] == null ? value : attrBimappers[ i ].map( value ) );
				
			}
		}
		return result;
	}
	
	@Override
	public QObject<S> unmap( Object out )
	{
		if( out == null )
		{
			return null;
		}
		Map<?, ?> m = ( Map<?, ?> ) out;
		QObject<S> result = spec.newObject( );
		for( int i = 0 ; i < spec.getAttributeCount( ) ; i++ )
		{
			Attribute<?> attribute = spec.attributeAt( i );
			if( m.containsKey( attribute.getName( ) ) )
			{
				Object value = m.get( attribute.getName( ) );
				result.set( attribute , value == null || attrBimappers[ i ] == null ? value : attrBimappers[ i ].unmap( value ) );
			}
		}
		return result;
	}
}