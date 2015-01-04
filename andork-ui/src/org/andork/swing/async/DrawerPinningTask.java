package org.andork.swing.async;

import java.awt.Component;

import org.andork.awt.layout.DrawerHolder;

public abstract class DrawerPinningTask extends SelfReportingTask
{
	private DrawerHolder	drawerHolder;

	public DrawerPinningTask( Component dialogParent , DrawerHolder drawerHolder )
	{
		super( dialogParent );
		this.drawerHolder = drawerHolder;
	}

	@Override
	protected final void duringDialog( ) throws Exception
	{
		try
		{
			drawerHolder.hold( this );
			reallyDuringDialog( );
		}
		finally
		{
			drawerHolder.release( this );
		}

	}

	protected abstract void reallyDuringDialog( ) throws Exception;
}