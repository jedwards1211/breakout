package org.andork.torquescape.model.vertex;

public interface IVertexAttrFn
{
	public int getBytesPerVertex( );

	public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor );
}
