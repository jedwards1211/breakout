package org.andork.q2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.andork.util.Java7;

/**
 * Defines a set of properties that any {@link QObject} constructed with this
 * spec will have.<br>
 * <br>
 * To use this, create a class that extends {@code QSpec}, give it {@code public static final Property<?>} fields, and
 * pass those fields into {@link #QSpec(Property...)}. You'll also probably want to create a {@code public static final}
 * instance of your class and make its constructor {@code private}.<br>
 * <br>
 * The Q doesn't stand for anything.
 * 
 * @author andy.edwards
 * @param <S>
 *            the type of {@link QSpec} for this object.
 */
public class QSpec
{
	public static class Property<T>
	{
		int					index;
		QSpec				spec;
		String				name;
		Class<? super T>	type;
		T					initValue;

		public Property( String name , Class<? super T> type )
		{
			this.name = name;
			this.type = type;
		}

		public Property( String name , Class<? super T> type , T initValue )
		{
			this( name , type );
			this.initValue = initValue;
		}

		public final boolean isPropertyOf( QObject<?> object )
		{
			return object.spec.properties[ index ] == this;
		}

		public final boolean isPropertyOf( QSpec spec )
		{
			return spec.properties[ index ] == this;
		}

		public final void requirePropertyOf( QObject<?> object )
		{
			if( !isPropertyOf( object ) )
			{
				throw new IllegalArgumentException( this + " is not a property of the given object" );
			}
		}

		@SuppressWarnings( "unchecked" )
		public T get( QObject<?> object )
		{
			requirePropertyOf( object );
			return ( T ) object.values[ index ];
		}

		@SuppressWarnings( "unchecked" )
		public T set( QObject<?> object , T newValue )
		{
			requirePropertyOf( object );
			T oldValue = ( T ) object.values[ index ];
			if( !Java7.Objects.equals( oldValue , newValue ) )
			{
				if( oldValue instanceof QElement )
				{
					( ( QElement ) oldValue ).removePropertyChangeListener( object );
					object.fireChildrenRemoved( object , oldValue );
				}
				object.values[ index ] = newValue;
				object.firePropertyChange( object , this , oldValue , newValue );
				if( newValue instanceof QElement )
				{
					( ( QElement ) newValue ).addPropertyChangeListener( object );
					object.fireChildrenAdded( object , newValue );
				}
			}
			return oldValue;
		}

		public final int index( )
		{
			return index;
		}

		public final String name( )
		{
			return name;
		}

		public final Class<? super T> type( )
		{
			return type;
		}

		public T initValue( )
		{
			return initValue;
		}

		public String toString( )
		{
			return name;
		}
	}

	public static class NonNullProperty<T> extends Property<T>
	{

		public NonNullProperty( String name , Class<? super T> type , T initValue )
		{
			super( name , type , Objects.requireNonNull( initValue ) );
		}

		@Override
		public T set( QObject<?> object , T newValue )
		{
			return super.set( object , Objects.requireNonNull( newValue ) );
		}
	}

	QSpec				superspec;
	Property<?>[ ]		properties;
	List<Property<?>>	propertyList;

	/**
	 * Creates a "derived" {@code QSpec} with all the properties of a
	 * "superspec" and more. Implementing classes that use this constructor
	 * should extend the superspec class.
	 * 
	 * @param superspec
	 * @param properties
	 */
	protected QSpec( QSpec superspec , Property<?> ... properties )
	{
		this.superspec = superspec;
		this.properties = new Property[ superspec.properties.length + properties.length ];
		System.arraycopy( superspec.properties , 0 , this.properties , 0 , superspec.properties.length );
		for( int i = 0 ; i < properties.length ; i++ )
		{
			if( properties[ i ].spec != null )
			{
				throw new IllegalStateException( "properties[" + i + "] (" + properties[ i ].name +
					") already belongs to spec " + properties[ i ].spec );
			}
			int ii = i + superspec.properties.length;
			this.properties[ ii ] = properties[ i ];
			this.properties[ ii ].spec = this;
			this.properties[ ii ].index = ii;
		}
		propertyList = Collections.unmodifiableList( Arrays.asList( properties ) );
	}

	protected QSpec( Property<?> ... properties )
	{
		this.superspec = null;
		this.properties = new Property[ properties.length ];
		for( int i = 0 ; i < properties.length ; i++ )
		{
			if( properties[ i ].spec != null )
			{
				throw new IllegalStateException( "properties[" + i + "] (" + properties[ i ].name +
					") already belongs to spec " + properties[ i ].spec );
			}
			this.properties[ i ] = properties[ i ];
			this.properties[ i ].spec = this;
			this.properties[ i ].index = i;
		}
		propertyList = Collections.unmodifiableList( Arrays.asList( properties ) );
	}

	protected static <T> Property<T> property( String name , Class<? super T> type )
	{
		return new Property<T>( name , type );
	}

	protected static <T> Property<T> property( String name , Class<? super T> type , T initValue )
	{
		return new Property<T>( name , type , initValue );
	}

	protected static <T> NonNullProperty<T> nonNullProperty( String name , Class<? super T> type , T initValue )
	{
		return new NonNullProperty<T>( name , type , initValue );
	}

	public final Property<?> propertyAt( int index )
	{
		return properties[ index ];
	}

	public final List<Property<?>> properties( )
	{
		return propertyList;
	}

	public final int propertyCount( )
	{
		return properties.length;
	}
}
