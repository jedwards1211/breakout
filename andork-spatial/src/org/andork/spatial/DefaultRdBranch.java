package org.andork.spatial;
public class DefaultRdBranch<T> extends DefaultRdNode<T> implements RdBranch<T>
{
	public RdNode<T>[ ]	children;
	
	public DefaultRdBranch( double[ ] mbr , RdNode<T>[ ] children )
	{
		super( mbr );
		this.children = children;
	}
	
	@Override
	public RdNode<T>[ ] children( )
	{
		return children;
	}
}
