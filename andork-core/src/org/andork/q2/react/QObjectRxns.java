package org.andork.q2.react;

import java.util.HashMap;
import java.util.Map;

import org.andork.q2.QObject;
import org.andork.q2.QObjectListener;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;
import org.andork.react.FunctionRxn;
import org.andork.react.Node;
import org.andork.react.Rxn;

public class QObjectRxns<S extends QSpec> extends Rxn<QObject<? extends S>> implements QObjectListener
{
	private final S spec;

	private final Node<QObject<? extends S>> input;
	private final Map<Property<?>, Rxn<?>> rxns = new HashMap<>( );

	public QObjectRxns( S spec , Node<QObject<? extends S>> input )
	{
		this.spec = spec;
		this.input = input;
		input.bind( this );
	}

	@Override
	public void objectChanged( QObject<?> source , Property<?> property , Object oldValue , Object newValue )
	{
		if( source == value )
		{
			Rxn<?> rxn = rxns.get( property );
			if( rxn != null )
			{
				rxn.invalidate( );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	public <T> Rxn<T> rxn( final Property<T> property )
	{
		property.requirePropertyOf( spec );
		Rxn<T> rxn = ( Rxn<T> ) rxns.get( property );
		if( rxn == null )
		{
			rxn = new FunctionRxn<>( this , q -> q == null ? null : q.get( property ) );
			rxns.put( property , rxn );
		}
		return rxn;
	}

	@Override
	protected QObject<? extends S> recalculate( )
	{
		return input.get( );
	}

	protected void set( QObject<? extends S> newValue )
	{
		if( value != newValue )
		{
			if( value != null )
			{
				value.removeListener( this );
			}

			value = newValue;

			if( newValue != null )
			{
				newValue.addListener( this );
			}
		}
	}
}
