package org.andork.spatial;
public abstract class DefaultRdNode<T> implements RdNode<T>
{
	public RdNode<T>		parent;
	public double[ ]	mbr;
	
	public DefaultRdNode( double[ ] mbr )
	{
		super( );
		this.mbr = mbr;
	}
	
	@Override
	public double[ ] mbr( )
	{
		return mbr;
	}
	
	public RdNode<T> parent( )
	{
		return parent;
	}
}
