package org.andork.torquescape.model.vertex;

public interface IVertexFn
{
	public int getVertexCount( float param );
	
	public int getBytesPerVertex( );
	
	public void eval( float param , int index , IVertexVisitor visitor );
}
