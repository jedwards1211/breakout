package org.andork.torquescape.model2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public abstract class AbstractIndexedSlice implements IIndexedSlice
{
	public IntBuffer	indexBuffer;
	
	public void setIndexBuffer( IntBuffer buffer )
	{
		indexBuffer = buffer;
	}
	
	public void setIndices( int[ ] indices )
	{
		ByteBuffer bb = ByteBuffer.allocateDirect( indices.length * 4 );
		bb.order( ByteOrder.nativeOrder( ) );
		indexBuffer = bb.asIntBuffer( );
		indexBuffer.put( indices );
		indexBuffer.position( 0 );
	}
	
	@Override
	public IntBuffer getIndexBuffer( )
	{
		return indexBuffer;
	}
}
