package org.andork.torquescape.model.gen;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.andork.torquescape.model.Zone;
import org.andork.torquescape.model.index.IIndexFn;
import org.andork.torquescape.model.vertex.IVertexFn;
import org.andork.torquescape.model.vertex.IVertexVisitor;

public abstract class ZoneGenerator implements IVertexVisitor
{
	protected Zone			zone;
	protected ByteBuffer	buffer;
	
	public ZoneGenerator( )
	{
		super( );
	}
	
	public Zone getZone( )
	{
		return zone;
	}
	
	public void setZone( Zone zone )
	{
		this.zone = zone;
		this.buffer = zone.getVertBuffer( );
	}
	
	public void generate( IVertexFn vertexFn , IIndexFn meshingFn , Iterable<Float> params )
	{
		int indexOffset = 0;
		
		Iterator<Float> paramIter = params.iterator( );
		while( paramIter.hasNext( ) )
		{
			float param = paramIter.next( );
			
			int vertexCount = vertexFn.getVertexCount( param );
			for( int index = 0 ; index < vertexCount ; index++ )
			{
				vertexFn.eval( param , index , this );
			}
			
			if( paramIter.hasNext( ) )
			{
				int indexCount = meshingFn.getIndexCount( param );
				for( int index = 0 ; index < indexCount ; index++ )
				{
					zone.getIndexBuffer( ).put( ( char ) ( meshingFn.eval( param , index ) + indexOffset ) );
				}
			}
			
			indexOffset += vertexCount;
		}
	}
}