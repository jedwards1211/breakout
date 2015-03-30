package org.andork.react;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Reactable<T>
{
	private T value;
	private final CopyOnWriteArraySet<Reaction<?>> reactions = new CopyOnWriteArraySet<>( );

	public void bind( Reaction<?> reaction )
	{
		if( reactions.add( reaction ) )
		{
			reaction.invalidate( );
		}
	}

	public void unbind( Reaction<?> reaction )
	{
		if( reactions.remove( reaction ) )
		{
			reaction.invalidate( );
		}
	}

	public final <R> FunctionReaction<T, R> react( Function<T, R> fn )
	{
		return new FunctionReaction<>( this , fn );
	}

	public final PredicateReaction<T> react( Predicate<T> p )
	{
		return new PredicateReaction<>( this , p );
	}

	public final ConsumerReaction<T> react( Consumer<T> consumer )
	{
		return new ConsumerReaction<>( this , consumer );
	}

	public final <U, R> BiFunctionReaction<T, U, R> react( Reactable<U> u , BiFunction<T, U, R> fn )
	{
		return new BiFunctionReaction<>( this , u , fn );
	}

	public final <U> BiPredicateReaction<T, U> react( Reactable<U> u , BiPredicate<T, U> p )
	{
		return new BiPredicateReaction<>( this , u , p );
	}

	public final <U> BiConsumerReaction<T, U> react( Reactable<U> u , BiConsumer<T, U> consumer )
	{
		return new BiConsumerReaction<>( this , u , consumer );
	}

	public T get( )
	{
		return value;
	}

	protected void invalidateReactions( )
	{
		for( Reaction<?> reaction : reactions )
		{
			reaction.invalidate( );
		}
	}

	protected void set( T newValue )
	{
		if( this.value != newValue )
		{
			T oldValue = this.value;
			this.value = newValue;
			onValueChanged( oldValue , newValue );
		}
	}

	protected void onValueChanged( T oldValue , T newValue )
	{

	}
}
