package org.andork.torquescape.model2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Zone
{
	public FloatBuffer			vertBuffer;
	public CharBuffer			indexBuffer;
	
	public final List<ISlice>	slices	= new ArrayList<ISlice>( );
	
	public void init( float[ ] verts , char[ ] indices )
	{
		ByteBuffer bb = ByteBuffer.allocateDirect( verts.length * 4 );
		bb.order( ByteOrder.nativeOrder( ) );
		vertBuffer = bb.asFloatBuffer( );
		vertBuffer.put( verts );
		vertBuffer.position( 0 );
		
		bb = ByteBuffer.allocateDirect( indices.length * 2 );
		bb.order( ByteOrder.nativeOrder( ) );
		indexBuffer = bb.asCharBuffer( );
		indexBuffer.put( indices );
		indexBuffer.position( 0 );
	}
}
