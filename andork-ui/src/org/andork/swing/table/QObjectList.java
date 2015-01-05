package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.andork.event.BasicPropertyChangeListener;
import org.andork.q2.QObject;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;

/**
 * A list of {@link QObject}s designed specifically to back a {@link QObjectListTableModel}.
 * 
 * @author James
 *
 * @param <S>
 *            the {@link QSpec} for the {@link QObject}s in the list.
 */
public class QObjectList<S extends QSpec>
{
	private final S								spec;
	private final Property<Integer>				indexProperty;

	private final List<QObject<? extends S>>	elements		= new ArrayList<QObject<? extends S>>( );

	private List<Listener<S>>					listeners		= new LinkedList<Listener<S>>( );
	private final ElementListener				elementListener	= new ElementListener( );

	public QObjectList( S spec , Property<Integer> indexProperty )
	{
		this.spec = spec;
		this.indexProperty = indexProperty.requirePropertyOf( spec );
	}

	public S spec( )
	{
		return spec;
	}

	public int size( )
	{
		return elements.size( );
	}

	public boolean isEmpty( )
	{
		return elements.isEmpty( );
	}

	public QObject<? extends S> get( int index )
	{
		return elements.get( index );
	}

	public void set( int index , QObject<? extends S> element )
	{
		QObject<? extends S> previous = elements.set( index , element );
		updateIndices( index );
		if( !previous.equals( element ) )
		{
			previous.removePropertyChangeListener( elementListener );
			element.addPropertyChangeListener( elementListener );
			fireElementsUpdated( index , index );
		}
	}

	public void add( QObject<? extends S> element )
	{
		add( elements.size( ) , element );
	}

	public void add( int index , QObject<? extends S> element )
	{
		elements.add( index , element );
		updateIndices( index );
		element.addPropertyChangeListener( elementListener );
		fireElementsInserted( index , index );
	}

	public void addAll( Collection<? extends QObject<? extends S>> elements )
	{
		addAll( elements.size( ) , elements );
	}

	public void addAll( int index , Collection<? extends QObject<? extends S>> elements )
	{
		this.elements.addAll( index , elements );
		updateIndices( index );
		for( QObject<? extends S> element : elements )
		{
			element.addPropertyChangeListener( elementListener );
		}
		fireElementsInserted( index , index + elements.size( ) - 1 );
	}

	public void remove( int index )
	{
		QObject<? extends S> removed = elements.remove( index );
		removed.removePropertyChangeListener( elementListener );
		updateIndices( index );
		fireElementsDeleted( index , index );
	}

	public void remove( QObject<? extends S> element )
	{
		int index = elements.indexOf( element );
		if( index >= 0 )
		{
			remove( index );
		}
	}

	public void removeSublist( int fromIndex , int toIndex )
	{
		for( int i = fromIndex ; i < toIndex ; i++ )
		{
			QObject<? extends S> removed = elements.remove( fromIndex );
			removed.removePropertyChangeListener( elementListener );
		}
		updateIndices( fromIndex );
		fireElementsDeleted( fromIndex , toIndex - 1 );
	}

	public void clear( )
	{
		int size = elements.size( );
		for( QObject<? extends S> element : elements )
		{
			element.removePropertyChangeListener( elementListener );
		}
		elements.clear( );
		fireElementsDeleted( 0 , size - 1 );
	}

	public void addListener( Listener<S> listener )
	{
		if( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( Listener<S> listener )
	{
		listeners.remove( listener );
	}

	protected void updateIndices( int fromIndex )
	{
		for( int index = fromIndex ; index < elements.size( ) ; index++ )
		{
			elements.get( index ).set( indexProperty , index );
		}
	}

	protected void fireElementsUpdated( int fromIndex , int toIndex )
	{
		for( Listener<S> listener : listeners )
		{
			listener.elementsUpdated( this , fromIndex , toIndex );
		}
	}

	protected void fireElementsUpdated( int fromIndex , int toIndex , Property<?> property )
	{
		for( Listener<S> listener : listeners )
		{
			listener.elementsUpdated( this , fromIndex , toIndex , property );
		}
	}

	protected void fireElementsInserted( int fromIndex , int toIndex )
	{
		for( Listener<S> listener : listeners )
		{
			listener.elementsInserted( this , fromIndex , toIndex );
		}
	}

	protected void fireElementsDeleted( int fromIndex , int toIndex )
	{
		for( Listener<S> listener : listeners )
		{
			listener.elementsDeleted( this , fromIndex , toIndex );
		}
	}

	protected void handlePropertyChange( QObject<? extends S> source , Property<?> property , Object oldValue ,
		Object newValue , int index )
	{
		if( property != indexProperty )
		{
			int elementIndex = source.get( indexProperty );
			fireElementsUpdated( elementIndex , elementIndex , ( Property<?> ) property );
		}
	}

	public static interface Listener<S extends QSpec>
	{
		public void elementsInserted( QObjectList<? extends S> list , int fromIndex , int toIndex );

		public void elementsDeleted( QObjectList<? extends S> list , int fromIndex , int toIndex );

		public void elementsUpdated( QObjectList<? extends S> list , int fromIndex , int toIndex );

		public void
			elementsUpdated( QObjectList<? extends S> list , int fromIndex , int toIndex , Property<?> property );
	}

	private class ElementListener implements BasicPropertyChangeListener
	{
		@SuppressWarnings( "unchecked" )
		@Override
		public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
		{
			handlePropertyChange( ( QObject<? extends S> ) source ,
				( Property<?> ) property , oldValue , newValue , index );
		}
	}
}
