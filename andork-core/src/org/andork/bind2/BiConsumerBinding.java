package org.andork.bind2;

import java.util.function.BiConsumer;

public class BiConsumerBinding<A, B, O> implements Binding
{
	public final Link<A>			inputALink	= new Link<A>( this );
	public final Link<B>			inputBLink	= new Link<B>( this );
	public final BiConsumer<A, B>	consumer;
	
	public BiConsumerBinding( BiConsumer<A, B> consumer )
	{
		super( );
		this.consumer = consumer;
	}
	
	public BiConsumerBinding( Binder<A> inputA , Binder<B> inputB , BiConsumer<A, B> consumer )
	{
		this( consumer );
		inputALink.bind( inputA );
		inputBLink.bind( inputB );
	}
	
	public void update( boolean force )
	{
		consumer.accept( inputALink.get( ) , inputBLink.get( ) );
	}
}
