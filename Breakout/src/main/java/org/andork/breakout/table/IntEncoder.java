package org.andork.breakout.table;

import java.nio.ByteBuffer;
import java.util.Base64;

public class IntEncoder
{
	ByteBuffer	buffer;
	
	public IntEncoder( )
	{
		buffer = ByteBuffer.allocate( 4 );
	}
	
	public String encode( int i )
	{
		buffer.rewind( );
		buffer.putInt( i );
		return new String( Base64.getEncoder( ).encode( buffer.array( ) ) );
	}
	
	public int decode( String s )
	{
		Base64.getDecoder( ).decode( s.getBytes( ) , buffer.array( ) );
		buffer.rewind( );
		return buffer.getInt( );
	}
}
