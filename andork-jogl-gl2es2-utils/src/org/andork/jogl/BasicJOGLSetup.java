package org.andork.jogl;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

public class BasicJOGLSetup
{
	protected GLWindow			glWindow;
	protected BasicJOGLScene	scene;
	
	protected BasicNavigator	navigator;
	protected BasicOrbiter		orbiter;
	
	public BasicJOGLSetup( )
	{
		this( createDefaultGLWindow( ) );
	}
	
	public BasicJOGLSetup( GLWindow glWindow )
	{
		this.glWindow = glWindow;
		init( );
	}
	
	public static GLWindow createDefaultGLWindow( )
	{
		final GLProfile glp = GLProfile.get( GLProfile.GL3 );
		final GLCapabilities caps = new GLCapabilities( glp );
		Display dpy = NewtFactory.createDisplay( null );
		Screen screen = NewtFactory.createScreen( dpy , 0 );
		final GLWindow glWindow = GLWindow.create( screen , caps );
		return glWindow;
	}
	
	public void waitUntilClosed( )
	{
		final Object lock = new Object( );
		glWindow.addWindowListener( new WindowAdapter( )
		{
			@Override
			public void windowDestroyed( WindowEvent e )
			{
				synchronized( lock )
				{
					lock.notify( );
				}
			}
		} );
		
		while( glWindow.isVisible( ) )
		{
			synchronized( lock )
			{
				try
				{
					lock.wait( );
				}
				catch( InterruptedException e1 )
				{
					e1.printStackTrace( );
				}
			}
		}
	}
	
	protected void init( )
	{
		scene = createScene( );
		glWindow.addGLEventListener( scene );
		
		initMouseInput( );
		initKeyboardInput( );
		
		Dimension screenSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		glWindow.setSize( screenSize.width * 2 / 3 , screenSize.height * 2 / 3 );
		glWindow.setPosition( 50 , 50 );
		glWindow.setDefaultCloseOperation( WindowClosingMode.DISPOSE_ON_CLOSE );
	}
	
	protected BasicJOGLScene createScene( )
	{
		return new BasicJOGLScene( );
	}
	
	protected void initMouseInput( )
	{
		navigator = new BasicNavigator( this );
		
		glWindow.addMouseListener( navigator );
		
		orbiter = new BasicOrbiter( this );
		
		glWindow.addMouseListener( orbiter );
	}
	
	protected void initKeyboardInput( )
	{
		glWindow.addKeyListener( new KeyListener( )
		{
			@Override
			public void keyReleased( KeyEvent e )
			{
				
			}
			
			@Override
			public void keyPressed( KeyEvent e )
			{
				if( e.getKeyChar( ) == 'f' )
				{
					glWindow.setFullscreen( !glWindow.isFullscreen( ) );
				}
			}
		} );
	}
}
