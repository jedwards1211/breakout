package org.andork.spatial;
public class DefaultRfBranch<T> extends DefaultRfNode<T> implements RfBranch<T>
{
	public RfNode<T>[ ]	children;
	
	public DefaultRfBranch( float[ ] mbr , RfNode<T>[ ] children )
	{
		super( mbr );
		this.children = children;
	}
	
	@Override
	public RfNode<T>[ ] children( )
	{
		return children;
	}
}
