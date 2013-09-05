package org.andork.torquescape.model.vertex;


public interface IVertexVisitor
{
	public void visit( double d );
	
	public void visit( float f );
	
	public void visit( long l );
	
	public void visit( int i );
	
	public void visit( char c );
	
	public void visit( short s );
	
	public void visit( byte b );
}
