package org.andork.q;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.reflect.ReflectionUtils;

public abstract class QSpec<S extends QSpec<S>>
{
	final Attribute<?>[ ]			attributes;
	final List<Attribute<?>>		attributeList;
	final Map<String, Attribute<?>>	attributesByName	= new LinkedHashMap<String, Attribute<?>>( );
	
	protected QSpec( )
	{
		for( Field field : ReflectionUtils.getStaticFieldList( getClass( ) , true ) )
		{
			if( field.getType( ) == Attribute.class )
			{
				try
				{
					Attribute<?> attr = ( Attribute<?> ) field.get( null );
					attributesByName.put( attr.getName( ) , attr );
				}
				catch( Exception ex )
				{
					// Shouldn't happen...
				}
			}
		}
		this.attributes = attributesByName.values( ).toArray( new Attribute[ attributesByName.size( ) ] );
		
		for( int i = 0 ; i < attributes.length ; i++ )
		{
			if( attributes[ i ].index < 0 )
			{
				attributes[ i ].index = i;
			}
			else if( attributes[ i ].index != i )
			{
				throw new IllegalStateException( "attribute order conflicts with another spec" );
			}
		}
		this.attributeList = Collections.unmodifiableList( Arrays.asList( attributes ) );
	}
	
	protected QSpec( Iterable<Attribute<?>> attrIterable )
	{
		for( Attribute<?> attr : attrIterable )
		{
			attributesByName.put( attr.getName( ) , attr );
		}
		ArrayList<Attribute<?>> attrList = CollectionUtils.toArrayList( attrIterable );
		attrList.trimToSize( );
		attributeList = Collections.unmodifiableList( attrList );
		attributes = attributeList.toArray( new Attribute[ attributeList.size( ) ] );
		
		for( int i = 0 ; i < attributeList.size( ) ; i++ )
		{
			if( attributes[ i ].index < 0 )
			{
				attributes[ i ].index = i;
			}
			else if( attributes[ i ].index != i )
			{
				throw new IllegalArgumentException( "attributes[" + i + "].index == " + attributes[ i ].index );
			}
		}
	}
	
	protected QSpec( Attribute<?> ... attributes )
	{
		this( Arrays.asList( attributes ) );
	}
	
	public QObject<S> newObject( )
	{
		return QObject.newInstance( ( S ) this );
	}
	
	public Attribute<?> getAttribute( String name )
	{
		return attributesByName.get( name );
	}
	
	public List<Attribute<?>> getAttributes( )
	{
		return attributeList;
	}
	
	public int getAttributeCount( )
	{
		return attributes.length;
	}
	
	public Attribute<?> attributeAt( int index )
	{
		return attributes[ index ];
	}
	
	public static class Attribute<T>
	{
		final Class<? super T>	valueClass;
		int						index	= -1;
		final String			name;
		
		public static <T> Attribute<T> newInstance( Class<? super T> valueClass , String name )
		{
			return new Attribute<T>( valueClass , name );
		}
		
		public Attribute( Class<? super T> valueClass , String name )
		{
			super( );
			this.valueClass = valueClass;
			this.name = name;
		}
		
		public Class<? super T> getValueClass( )
		{
			return valueClass;
		}
		
		public int getIndex( )
		{
			return index;
		}
		
		public String getName( )
		{
			return name;
		}
		
		public String toString( )
		{
			return getName( );
		}
	}
	
	public static <T> Attribute<T> newAttribute( Class<? super T> valueClass , String name )
	{
		return new Attribute<T>( valueClass , name );
	}
	
	public static <S extends QSpec<S>> Attribute<QObject<S>> newAttribute( S spec , String name )
	{
		return  new Attribute<QObject<S>>( QObject.class , name );
	}
}
