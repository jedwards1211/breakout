package org.andork.jogl.basic;

import static org.andork.math3d.Vecmath.mmul3x3;
import static org.andork.math3d.Vecmath.mmulAffine;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2ES2;

public class JOGLGroup implements JOGLObject
{
	
	public List<JOGLObject>	objects	= new ArrayList<JOGLObject>( );

	public JOGLGroup( )
	{
		super( );
	}

	@Override
	public void init( GL2ES2 gl )
	{
		for( JOGLObject object : objects )
		{
			object.init( gl );
		}
	}

	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		for( JOGLObject object : objects )
		{
			object.draw( gl , m , n , v , p );
		}
	}

	@Override
	public void destroy( GL2ES2 gl )
	{
		for( JOGLObject object : objects )
		{
			object.destroy( gl );
		}
	}
	
}