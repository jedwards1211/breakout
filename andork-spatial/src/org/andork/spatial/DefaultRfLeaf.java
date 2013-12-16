package org.andork.spatial;
public class DefaultRfLeaf<T> extends DefaultRfNode<T> implements RfLeaf<T>
{
	public T	object;
	
	public DefaultRfLeaf( T object , float[ ] mbr )
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
