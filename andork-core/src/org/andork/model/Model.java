package org.andork.model;

public interface Model extends HasChangeSupport
{
	public Object get( Object key );
	
	public void set( Object key , Object newValue );
}
