package org.andork.torquescape.model.vertex;

import org.andork.torquescape.model.coord.ICoordFn;

public class StandardVertexFn implements IVertexFn
{
	ICoordFn			coordFn;
	IVertexAttrFn[ ]	vertexAttrFns;
	int					bytesPerVertex;
	
	float[ ]			coords	= new float[ 3 ];
	
	public StandardVertexFn( ICoordFn coordFn , IVertexAttrFn ... vertexAttrFns )
	{
		this.coordFn = coordFn;
		this.vertexAttrFns = vertexAttrFns;
		bytesPerVertex = 24;
		for( IVertexAttrFn vertexAttrFn : vertexAttrFns )
		{
			bytesPerVertex += vertexAttrFn.getBytesPerVertex( );
		}
	}
	
	@Override
	public int getBytesPerVertex( )
	{
		return bytesPerVertex;
	}
	
	@Override
	public int getVertexCount( float param )
	{
		return coordFn.getCoordCount( param );
	}
	
	@Override
	public void eval( float param , int index , IVertexVisitor visitor )
	{
		int vertexCount = coordFn.getCoordCount( param );
		
		coordFn.eval( param , index , coords );
		
		visitor.visit( coords[ 0 ] );
		visitor.visit( coords[ 1 ] );
		visitor.visit( coords[ 2 ] );
		
		// this will become normal later
		visitor.visit( 0f );
		visitor.visit( 0f );
		visitor.visit( 0f );
		
		for( IVertexAttrFn vertexAttrFn : vertexAttrFns )
		{
			vertexAttrFn.eval( param , index , vertexCount , coords[ 0 ] , coords[ 1 ] , coords[ 2 ] , visitor );
		}
	}
}
