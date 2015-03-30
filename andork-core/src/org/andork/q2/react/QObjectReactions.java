package org.andork.q2.react;

import java.util.HashMap;
import java.util.Map;

import org.andork.q2.QObject;
import org.andork.q2.QObjectListener;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;
import org.andork.react.FunctionReaction;
import org.andork.react.Reactable;
import org.andork.react.Reaction;

public class QObjectReactions<S extends QSpec> extends Reaction<QObject<? extends S>> implements QObjectListener
{
	private final S spec;

	private final Reactable<QObject<? extends S>> input;
	private final Map<Property<?>, Reaction<?>> reactions = new HashMap<>( );

	public QObjectReactions( S spec , Reactable<QObject<? extends S>> input )
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
			Reaction<?> reaction = reactions.get( property );
			if( reaction != null )
			{
				reaction.invalidate( );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	public <T> Reaction<T> react( final Property<T> property )
	{
		property.requirePropertyOf( spec );
		Reaction<T> reaction = ( Reaction<T> ) reactions.get( property );
		if( reaction == null )
		{
			reaction = new FunctionReaction<>( this , q -> q == null ? null : q.get( property ) );
			reactions.put( property , reaction );
		}
		return reaction;
	}

	@Override
	protected QObject<? extends S> calculate( )
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
