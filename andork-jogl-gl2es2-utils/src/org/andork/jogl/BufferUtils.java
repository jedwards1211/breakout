package org.andork.jogl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils
{
	public static FloatBuffer newFloatBuffer( int capacity )
	{
		ByteBuffer bb = ByteBuffer.allocateDirect( capacity * 4 );
		bb.order( ByteOrder.nativeOrder( ) );
		return bb.asFloatBuffer( );
	}
	
	public static IntBuffer newIntBuffer( int capacity )
	{
		ByteBuffer bb = ByteBuffer.allocateDirect( capacity * 4 );
		bb.order( ByteOrder.nativeOrder( ) );
		return bb.asIntBuffer( );
	}
}
