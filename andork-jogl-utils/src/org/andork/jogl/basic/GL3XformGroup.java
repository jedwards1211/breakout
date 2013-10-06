package org.andork.jogl.basic;

import static org.andork.vecmath.FloatArrayVecmath.mmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.newIdentityMatrix;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL3;

public class GL3XformGroup implements GL3Object
{
	public float[ ]			xform	= newIdentityMatrix( );
	private float[ ]		m		= newIdentityMatrix( );
	
	public List<GL3Object>	objects	= new ArrayList<GL3Object>( );
	
	@Override
	public void init( GL3 gl )
	{
		for( GL3Object object : objects )
		{
			object.init( gl );
		}
	}
	
	@Override
	public void draw( GL3 gl , float[ ] m , float[ ] v , float[ ] p )
	{
		mmulAffine( m , xform , this.m );
		
		for( GL3Object object : objects )
		{
			object.draw( gl , this.m , v , p );
		}
	}
	
}
