package org.andork.torquescape.model2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

public abstract class AbstractIndexedSlice implements IIndexedSlice
{
	public CharBuffer	indexBuffer;
	
	public void setIndexBuffer( CharBuffer buffer )
	{
		indexBuffer = buffer;
	}
	
	public void setIndices( char[ ] indices )
	{
		ByteBuffer bb = ByteBuffer.allocateDirect( indices.length * 2 );
		bb.order( ByteOrder.nativeOrder( ) );
		indexBuffer = bb.asCharBuffer( );
		indexBuffer.put( indices );
		indexBuffer.position( 0 );
	}
	
	@Override
	public CharBuffer getIndexBuffer( )
	{
		return indexBuffer;
	}
}
