package org.andork.bind.ui;

import java.awt.Container;

import org.andork.awt.layout.BetterCardLayout;
import org.andork.bind.Binder;
import org.andork.util.Java7;

public class BetterCardLayoutBinder extends Binder<Object>
{
	Binder<?>		upstream;
	Container			parent;
	BetterCardLayout	layout;
	
	public BetterCardLayoutBinder( Container parent , BetterCardLayout layout )
	{
		this.parent = parent;
		this.layout = layout;
	}
	
	public static BetterCardLayoutBinder bind( Container parent , BetterCardLayout layout , Binder<?> upstream )
	{
		return new BetterCardLayoutBinder( parent , layout ).bind( upstream );
	}
	
	public BetterCardLayoutBinder bind( Binder<?> upstream )
	{
		if( this.upstream != upstream )
		{
			if( this.upstream != null )
			{
				unbind( this.upstream , this );
			}
			this.upstream = upstream;
			if( this.upstream != null )
			{
				bind( this.upstream , this );
			}
			
			update(false );
		}
		return this;
	}
	
	boolean	updating;
	
	@Override
	public Object get( )
	{
		return upstream == null ? null : upstream.get( );
	}
	
	@Override
	public void set( Object newValue )
	{
		if( layout != null )
		{
			layout.show( parent , newValue );
		}
	}
	
	@Override
	public void update(boolean force )
	{
		updating = true;
		try
		{
			Object newValue = upstream == null ? null : upstream.get( );
			if( force || !Java7.Objects.equals( get( ) , newValue ) )
			{
				set( newValue );
			}
		}
		finally
		{
			updating = false;
		}
	}
}
