package org.andork.spatial;
public abstract class DefaultRfNode<T> implements RfNode<T>
{
	public RfNode<T>		parent;
	public float[ ]	mbr;
	
	public DefaultRfNode( float[ ] mbr )
	{
		super( );
		this.mbr = mbr;
	}
	
	@Override
	public float[ ] mbr( )
	{
		return mbr;
	}
	
	public RfNode<T> parent( )
	{
		return parent;
	}
}
