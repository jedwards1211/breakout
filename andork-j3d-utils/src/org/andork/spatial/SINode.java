package org.andork.spatial;

public interface SINode<T>
{
	public SIBranch<T> getParent( );
	
	public void setParent( SIBranch<T> newParent );
	
	public BBox getBBox( );
	
	public boolean isValid( );
	
	public void validate( );
}
