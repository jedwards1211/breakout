package org.andork.spatial;
public class DefaultRdLeaf<T> extends DefaultRdNode<T> implements RdLeaf<T>
{
	public T	object;
	
	public DefaultRdLeaf( T object , double[ ] mbr )
	{
		super( mbr );
		this.object = object;
	}
	
	@Override
	public T object( )
	{
		return object;
	}
}
