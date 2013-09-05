package org.andork.torquescape.model.vertex;

import org.andork.torquescape.model.section.ISectionFn;

public class StandardVertexFn implements IVertexFn
{
	ISectionFn			sectionFn;
	IVertexAttrFn[ ]	vertexAttrFns;
	int					bytesPerVertex;
	
	float[ ]			coords	= new float[ 3 ];
	
	public StandardVertexFn( ISectionFn sectionFn , IVertexAttrFn ... vertexAttrFns )
	{
		this.sectionFn = sectionFn;
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
		return sectionFn.getVertexCount( param );
	}
	
	@Override
	public void eval( float param , int index , IVertexVisitor visitor )
	{
		int vertexCount = sectionFn.getVertexCount( param );
		
		sectionFn.eval( param , index , coords );
		
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
