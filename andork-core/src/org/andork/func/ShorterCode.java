package org.andork.func;

public class ShorterCode
{
	public static void swallowException( ExceptionRunnable runnable )
	{
		try
		{
			runnable.run( );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}

	public static <T> T swallowException( ExceptionSupplier<T> supplier )
	{
		try
		{
			return supplier.get( );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
