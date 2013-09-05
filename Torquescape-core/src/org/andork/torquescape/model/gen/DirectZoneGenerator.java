package org.andork.torquescape.model.gen;

import java.nio.ByteOrder;

public abstract class DirectZoneGenerator extends ZoneGenerator
{
	public static ZoneGenerator newInstance( )
	{
		return newInstance( ByteOrder.nativeOrder( ) );
	}
	
	public static ZoneGenerator newInstance( ByteOrder order )
	{
		return order == ByteOrder.BIG_ENDIAN ? new DirectZoneGeneratorB( ) : new DirectZoneGeneratorL( );
	}
}