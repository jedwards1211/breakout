package org.andork.func;

public interface Bimapper<I, O> extends Mapper<I, O>
{
	public I unmap( O out );
}
