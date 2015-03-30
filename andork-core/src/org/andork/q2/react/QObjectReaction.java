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

public class QObjectReaction<S extends QSpec> extends Reaction<QObject<? extends S>> implements QObjectListener
{
	private final S spec;

	private final Reactable<QObject<? extends S>> input;
	private final Map<Property<?>, Reaction<?>> reactions = new HashMap<>( );

	public QObjectReaction( S spec , Reactable<QObject<? extends S>> input )
	{
		this.spec = spec;
		this.input = input;
		input.bind( this );
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
	public void objectChanged( QObject<?> source , Property<?> property , Object oldValue , Object newValue )
	{
		if( isValid( ) && source == get( ) )
		{
			Reaction<?> reaction = reactions.get( property );
			if( reaction != null )
			{
				reaction.invalidate( );
			}
		}
	}

	@Override
	protected QObject<? extends S> calculate( )
	{
		return input.get( );
	}

	protected void onValueChanged( QObject<? extends S> oldValue , QObject<? extends S> newValue )
	{
		if( oldValue != null )
		{
			oldValue.removeListener( this );
		}

		if( newValue != null )
		{
			newValue.addListener( this );
		}
	}
}
