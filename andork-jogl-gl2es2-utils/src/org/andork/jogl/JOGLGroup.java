package org.andork.jogl;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2ES2;

import org.andork.jogl.neu.JoglDrawContext;
import org.andork.jogl.neu.JoglDrawable;
import org.andork.jogl.neu.JoglResource;

public class JOGLGroup implements JOGLObject , JoglResource
{
	public Object				userObj;
	public List<JOGLResource>	objects	= new ArrayList<JOGLResource>( );
	
	public JOGLGroup( )
	{
		super( );
	}
	
	public JOGLGroup( Object userObj )
	{
		this.userObj = userObj;
	}
	
	@Override
	public void init( GL2ES2 gl )
	{
		for( JOGLResource object : objects )
		{
			object.init( gl );
		}
	}
	
	@Override
	public void draw( GL2ES2 gl , float[ ] m , float[ ] n , float[ ] v , float[ ] p )
	{
		for( JOGLResource object : objects )
		{
			if( object instanceof JOGLObject )
			{
				( ( JOGLObject ) object ).draw( gl , m , n , v , p );
			}
		}
	}
	
	@Override
	public void destroy( GL2ES2 gl )
	{
		for( JOGLResource object : objects )
		{
			object.destroy( gl );
		}
	}
	
	@Override
	public void draw( JoglDrawContext context , GL2ES2 gl , float[ ] m , float[ ] n )
	{
		draw( gl , m , n , context.viewXform( ) , context.projXform( ) );
	}
	
	@Override
	public void dispose( GL2ES2 gl )
	{
		destroy( gl );
	}
	
}