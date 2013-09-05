package org.andork.torquescape.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zone
{
	ByteBuffer			vertByteBuffer;
	FloatBuffer			vertFloatBuffer;
	ByteBuffer			indexByteBuffer;
	CharBuffer			indexCharBuffer;
	int					bytesPerVertex;
	
	final List<ISlice>	slices	= new ArrayList<ISlice>( );
	
	public void init( int vertexCount , int bytesPerVertex , int indexCount )
	{
		this.bytesPerVertex = bytesPerVertex;
		
		ByteBuffer bb = ByteBuffer.allocateDirect( vertexCount * bytesPerVertex );
		bb.order( ByteOrder.nativeOrder( ) );
		vertByteBuffer = bb;
		vertFloatBuffer = vertByteBuffer.asFloatBuffer( );
		
		bb = ByteBuffer.allocateDirect( indexCount * 2 );
		bb.order( ByteOrder.nativeOrder( ) );
		indexByteBuffer = bb;
		indexCharBuffer = bb.asCharBuffer( );
	}
	
	public void init( float[ ] verts , int floatsPerVertex , char[ ] indices )
	{
		init( verts.length / floatsPerVertex , floatsPerVertex * 4 , indices.length );
		
		vertFloatBuffer.put( verts );
		vertByteBuffer.position( 0 );
		vertFloatBuffer.position( 0 );
		
		indexCharBuffer.put( indices );
		indexByteBuffer.position( 0 );
		indexCharBuffer.position( 0 );
	}
	
	public ByteBuffer getVertByteBuffer( )
	{
		return vertByteBuffer;
	}
	
	public FloatBuffer getVertFloatBuffer( )
	{
		return vertFloatBuffer;
	}
	
	public ByteBuffer getIndexByteBuffer( )
	{
		return indexByteBuffer;
	}
	
	public CharBuffer getIndexCharBuffer( )
	{
		return indexCharBuffer;
	}
	
	public int getBytesPerVertex( )
	{
		return bytesPerVertex;
	}
	
	public void addSlice( ISlice slice )
	{
		slices.add( slice );
	}
	
	public List<ISlice> getSlices( )
	{
		return Collections.unmodifiableList( slices );
	}
}
