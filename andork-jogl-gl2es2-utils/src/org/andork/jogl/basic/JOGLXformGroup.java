package org.andork.jogl.basic;

import static org.andork.vecmath.Vecmath.invAffineToTranspose3x3;
import static org.andork.vecmath.Vecmath.mmul3x3;
import static org.andork.vecmath.Vecmath.mmulAffine;
import static org.andork.vecmath.Vecmath.newMat4f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2ES2;

public class JOGLXformGroup implements JOGLObject
{
	public float[ ]			xform	= newMat4f( );
	private float[ ]		nxform	= new float[ 9 ];
	private float[ ]		m		= newMat4f( );
	private float[ ]		n		= new float[ 9 ];
	
	public List<JOGLObject>	objects	= new ArrayList<JOGLObject>( );
	
	public JOGLXformGroup() {
		updateN( );
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		for( JOGLObject object : objects )
		{
			object.init( gl );
		}
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
		
		for( JOGLObject object : objects )
		{
			object.draw( gl , this.m , this.n , v , p );
		}
	}
}
