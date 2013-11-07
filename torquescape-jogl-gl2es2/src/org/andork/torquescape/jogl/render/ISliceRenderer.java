package org.andork.torquescape.jogl.render;

import javax.media.opengl.GL2ES2;

import org.andork.torquescape.model.ISlice;

public interface ISliceRenderer<S extends ISlice>
{
	public abstract void init( GL2ES2 gl3 );
	
	public abstract void draw( GL2ES2 gl3 , float[ ] m , float[ ] n, float[ ] v, float[ ] p );
}