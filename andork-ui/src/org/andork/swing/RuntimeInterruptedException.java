package org.andork.swing;

public class RuntimeInterruptedException extends RuntimeException
{
	public RuntimeInterruptedException( InterruptedException cause )
	{
		super( cause );
	}
	
	public InterruptedException getCause( )
	{
		return ( InterruptedException ) super.getCause( );
	}
}
