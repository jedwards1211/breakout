package org.andork.event;

import java.util.function.Consumer;

public class RootChangeListener<M> implements HierarchicalBasicPropertyChangeListener
{
	Consumer<M>	consumer;

	/**
	 * Creates a {@code RootChangeListener}.
	 * 
	 * @param consumer
	 *            the {@link Consumer} to which this {@code RootChangeListener} will pass the root model of any change
	 *            events it receives.
	 */
	public RootChangeListener( Consumer<M> consumer )
	{
		super( );
		this.consumer = consumer;
	}

	private M getRootModel( Object source )
	{
		if( source instanceof SourcePath )
		{
			return getRootModel( ( M ) ( ( SourcePath ) source ).parent );
		}
		return ( M ) source;
	}

	@Override
	public void propertyChange( Object source , Object property , Object oldValue , Object newValue , int index )
	{
		consumer.accept( getRootModel( source ) );
	}

	@Override
	public void childrenChanged( Object source , ChangeType changeType , Object ... children )
	{
		consumer.accept( getRootModel( source ) );
	}
}
