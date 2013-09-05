package org.andork.torquescape.model.section;

public interface ISectionFn
{
	public int getVertexCount( float param );
	
	public void eval( float param , int index , float[ ] result );
}
