package org.andork.awt;

import javax.swing.SwingUtilities;

public class CheckEDT
{
	public static void checkEDT( )
	{
		if( !SwingUtilities.isEventDispatchThread( ) )
		{
			throw new RuntimeException( "Must be called from EDT" );
		}
	}
	
	public static void checkNotEDT( )
	{
		if( SwingUtilities.isEventDispatchThread( ) )
		{
			throw new RuntimeException( "Must not be called from EDT" );
		}
	}
}
