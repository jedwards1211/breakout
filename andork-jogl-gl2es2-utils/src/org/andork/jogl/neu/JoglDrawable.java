package org.andork.jogl.neu;

import javax.media.opengl.GL2ES2;

public interface JoglDrawable
{
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m, float[ ] n );
}
