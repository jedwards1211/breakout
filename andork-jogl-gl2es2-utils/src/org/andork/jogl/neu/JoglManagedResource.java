package org.andork.jogl.neu;

import javax.media.opengl.GL2ES2;

public abstract class JoglManagedResource implements JoglResource
{
	private final JoglResourceManager	manager;
	private int							useCount;
	
	public JoglManagedResource( JoglResourceManager manager )
	{
		this.manager = manager;
	}
	
	public void use( )
	{
		if( useCount++ == 0 )
		{
			manager.initLater( this );
		}
	}
	
	public void release( )
	{
		if( useCount > 0 && --useCount == 0 )
		{
			manager.disposeLater( this );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#init(javax.media.opengl.GL2ES2)
	 */
	@Override
	public abstract void init( GL2ES2 gl );
	
	/* (non-Javadoc)
	 * @see org.andork.jogl.neu.JoglResource#dispose(javax.media.opengl.GL2ES2)
	 */
	@Override
	public abstract void dispose( GL2ES2 gl );
}
