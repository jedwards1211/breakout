package org.andork.bind;

import org.andork.util.Java7;

public class EqualsBinder<T> extends Binder<Boolean>
{
	Binder<T>	upstream;
	T			trueValue;
	boolean		value;
	
	public EqualsBinder( T trueValue )
	{
		this.trueValue = trueValue;
	}
	
	public static <T> EqualsBinder<T> bindEquals( T trueValue , Binder<T> upstream )
	{
		return new EqualsBinder<T>( trueValue ).bind( upstream );
	}
	
	public EqualsBinder<T> bind( Binder<T> upstream )
	{
		if( this.upstream != upstream )
		{
			if( this.upstream != null )
			{
				unbind( this.upstream , this );
			}
			this.upstream = upstream;
			if( upstream != null )
			{
				bind( this.upstream , this );
			}
			
			update( false );
		}
		return this;
	}
	
	@Override
	public Boolean get( )
	{
		return value;
	}
	
	@Override
	public void set( Boolean newValue )
	{
		if( upstream != null && Boolean.TRUE.equals( newValue ) )
		{
			upstream.set( trueValue );
		}
	}
	
	@Override
	public void update( boolean force )
	{
		T upstreamValue = upstream == null ? null : upstream.get( );
		boolean newValue = Java7.Objects.equals( upstreamValue , trueValue );
		if( force || newValue != value )
		{
			value = newValue;
			updateDownstream( force );
		}
	}
}
