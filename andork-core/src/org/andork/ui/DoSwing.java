package org.andork.ui;

import javax.swing.SwingUtilities;

public abstract class DoSwing implements Runnable
{
	public DoSwing( )
	{
		super( );
		doSwing( this );
	}
	
	public static void doSwing( Runnable r )
	{
		if( SwingUtilities.isEventDispatchThread( ) )
		{
			r.run( );
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait( r );
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
	}
}
