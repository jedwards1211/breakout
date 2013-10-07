package org.andork.jogl.basic;

import static org.andork.vecmath.FloatArrayVecmath.invAffineToTranspose3x3;
import static org.andork.vecmath.FloatArrayVecmath.mmul3x3;
import static org.andork.vecmath.FloatArrayVecmath.mmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.newIdentityMatrix;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;

public class GL3XformGroup implements GL3Object
{
	public float[ ]			xform	= newIdentityMatrix( );
	private float[ ]		nxform	= new float[ 9 ];
	private float[ ]		m		= newIdentityMatrix( );
	private float[ ]		n		= new float[ 9 ];
	
	public List<GL3Object>	objects	= new ArrayList<GL3Object>( );
	
	public GL3XformGroup() {
		updateN( );
	}
	
	@Override
	public void init( GL3 gl )
	{
		for( GL3Object object : objects )
		{
			object.init( gl );
		}
	}
	
	public void updateN( )
	{
		invAffineToTranspose3x3( xform , nxform );
	}
	
	@Override
	public void draw( GL3 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		mmulAffine( m , xform , this.m );
		mmul3x3( n , nxform , this.n );
		
		for( GL3Object object : objects )
		{
			object.draw( gl , this.m , this.n , v , p );
		}
	}
}
