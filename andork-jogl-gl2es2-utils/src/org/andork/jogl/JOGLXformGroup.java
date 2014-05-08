package org.andork.jogl;

import static org.andork.math3d.Vecmath.invAffineToTranspose3x3;
import static org.andork.math3d.Vecmath.mmul3x3;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.newMat4f;


import javax.media.opengl.GL2ES2;

import org.andork.jogl.neu.JoglDrawContext;

public class JOGLXformGroup extends JOGLGroup implements JOGLObject
{
	public float[ ]			xform	= newMat4f( );
	private float[ ]		nxform	= new float[ 9 ];
	private float[ ]		m		= newMat4f( );
	private float[ ]		n		= new float[ 9 ];
	
	public JOGLXformGroup( )
	{
		updateN( );
	}
	
	public void updateN( )
	{
		invAffineToTranspose3x3( xform , nxform );
	}
	
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		mmulAffine( m , xform , this.m );
		mmul3x3( n , nxform , this.n );
		
		super.draw( gl , this.m , this.n , v , p );
	}

	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m, float[ ] n )
	{
		mmulAffine( m , xform , this.m );
		mmul3x3( n , nxform , this.n );
		
		super.draw( context , gl , m, n );
	}
}
