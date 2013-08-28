package org.andork.torquescape;

import org.andork.torquescape.model.ISlice;

public interface ISliceRenderer<S extends ISlice>
{
	public abstract void init( );
	
	public abstract void draw( float[ ] mvMatrix , float[ ] pMatrix );
}