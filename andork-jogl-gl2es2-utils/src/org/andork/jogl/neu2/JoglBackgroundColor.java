package org.andork.jogl.neu2;

import static org.andork.math3d.Vecmath.setf;

import java.util.Arrays;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.neu.JoglDrawContext;
import org.andork.jogl.neu.JoglDrawable;

public class JoglBackgroundColor implements JoglDrawable
{

	private final float[ ]	bgColor	=
									{ 0 , 0 , 0 , 1 };
	private boolean			bgColorDirty;

	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		if( bgColorDirty )
		{
			gl.glClearColor( bgColor[ 0 ] , bgColor[ 1 ] , bgColor[ 2 ] , bgColor[ 3 ] );
			bgColorDirty = false;
		}
		gl.glClear( GL2ES2.GL_COLOR_BUFFER_BIT | GL2ES2.GL_DEPTH_BUFFER_BIT );
	}

	public void set( float ... bgColor )
	{
		if( !Arrays.equals( this.bgColor , bgColor ) )
		{
			setf( this.bgColor , bgColor );
			bgColorDirty = true;
		}
	}

	public float[ ] get( float[ ] out )
	{
		setf( out , this.bgColor );
		return out;
	}

	public float[ ] get( )
	{
		return Arrays.copyOf( bgColor , bgColor.length );
	}

}
