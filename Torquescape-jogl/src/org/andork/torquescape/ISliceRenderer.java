package org.andork.torquescape;

import javax.media.opengl.GL3;

import org.andork.torquescape.model.ISlice;

public interface ISliceRenderer<S extends ISlice>
{
	public abstract void init( GL3 gl3 );
	
	public abstract void draw( GL3 gl3 , float[ ] mvMatrix , float[ ] pMatrix );
}