package org.andork.func;

import java.io.InputStream;

import java.io.OutputStream;

public interface StreamBimapper<T>
{
	public void write( T t , OutputStream out ) throws Exception;
	
	public T read( InputStream in ) throws Exception;
}
