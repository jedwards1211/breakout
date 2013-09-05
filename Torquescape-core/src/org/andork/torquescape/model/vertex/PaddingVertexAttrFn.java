package org.andork.torquescape.model.vertex;

public class PaddingVertexAttrFn implements IVertexAttrFn
{
	int	bytes;
	
	public PaddingVertexAttrFn( int bytes )
	{
		super( );
		this.bytes = bytes;
	}

	@Override
	public int getBytesPerVertex( )
	{
		return bytes;
	}
	
	@Override
	public void eval( float param , int index , int vertexCount , float x , float y , float z , IVertexVisitor visitor )
	{
		for( int i = 0 ; i < bytes ; i++ )
		{
			visitor.visit( ( byte ) 0 );
		}
	}
	
}
