package org.andork.torquescape.model.gen;

import java.nio.ByteOrder;

public abstract class DirectZoneGenerator extends ZoneGenerator
{
	public static DirectZoneGenerator newInstance( )
	{
		return newInstance( ByteOrder.nativeOrder( ) );
	}
	
	public static DirectZoneGenerator newInstance( ByteOrder order )
	{
		return order == ByteOrder.BIG_ENDIAN ? new DirectZoneGeneratorB( ) : new DirectZoneGeneratorL( );
	}
}