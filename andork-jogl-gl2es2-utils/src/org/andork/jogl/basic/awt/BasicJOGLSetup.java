package org.andork.jogl.basic.awt;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.andork.jogl.basic.BasicJOGLScene;

public class BasicJOGLSetup
{
	protected GLCanvas			canvas;
	protected BasicJOGLScene	scene;
	
	protected BasicNavigator	navigator;
	protected BasicOrbiter		orbiter;
	
	public BasicJOGLSetup( )
	{
		this( createDefaultCanvas( ) );
	}
	
	public BasicJOGLSetup( GLCanvas canvas )
	{
		this.canvas = canvas;
		init( );
	}
	
	public static GLCanvas createDefaultCanvas( )
	{
		final GLProfile glp = GLProfile.get( GLProfile.GL2ES2 );
		final GLCapabilities caps = new GLCapabilities( glp );
		GLCanvas canvas = new GLCanvas( caps );
		return canvas;
	}
	
	protected void init( )
	{
		scene = createScene( );
		canvas.addGLEventListener( scene );
		
		initMouseInput( );
		initKeyboardInput( );
	}
	
	protected BasicJOGLScene createScene( )
	{
		return new BasicJOGLScene( );
	}
	
	protected void initMouseInput( )
	{
		navigator = new BasicNavigator( this );
		
		canvas.addMouseListener( navigator );
		canvas.addMouseMotionListener( navigator );
		canvas.addMouseWheelListener( navigator );
		
		orbiter = new BasicOrbiter( this );
		
		canvas.addMouseListener( orbiter );
		canvas.addMouseMotionListener( orbiter );
		canvas.addMouseWheelListener( orbiter );
	}
	
	protected void initKeyboardInput( )
	{
	}
}
