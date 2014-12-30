package org.andork.awt.layout;

import java.util.WeakHashMap;

public class DrawerHolder
{
	private DrawerLayoutDelegate				delegate;
	private boolean								animate;

	private final WeakHashMap<Object, Object>	holders	= new WeakHashMap<>( );

	public DrawerHolder( DrawerLayoutDelegate delegate , boolean animate )
	{
		super( );
		this.delegate = delegate;
		this.animate = animate;
	}

	public void hold( Object holder )
	{
		holders.put( holder , null );
		delegate.setOpen( !holders.isEmpty( ) , animate );
	}

	public void release( Object holder )
	{
		holders.remove( holder );
		delegate.setOpen( !holders.isEmpty( ) , animate );
	}

	public void releaseAll( )
	{
		holders.clear( );
		delegate.close( animate );
	}

	public boolean isHeld( )
	{
		return !holders.isEmpty( );
	}

	public boolean isHeldBy( Object holder )
	{
		return holders.containsKey( holder );
	}
}
