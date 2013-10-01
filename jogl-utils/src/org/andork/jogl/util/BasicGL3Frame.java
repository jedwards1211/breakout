package org.andork.jogl.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

@SuppressWarnings( "serial" )
public class BasicGL3Frame extends JFrame
{
	protected GLCanvas			glCanvas;
	protected BasicGL3Scene		scene;
	
	protected BasicNavigator	navigator;
	protected BasicOrbiter		orbiter;
	
	public BasicGL3Frame( )
	{
		init( );
	}
	
	protected void init( )
	{
		glCanvas = createCanvas( );
		scene = createScene( );
		glCanvas.addGLEventListener( scene );
		
		initMouseInput( );
		
		getContentPane( ).add( glCanvas , BorderLayout.CENTER );
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		Dimension screenSize = Toolkit.getDefaultToolkit( ).getScreenSize( );
		setSize( new Dimension( screenSize.width * 3 / 4 , screenSize.height * 3 / 4 ) );
		setLocationRelativeTo( null );
	}
	
	protected BasicGL3Scene createScene( )
	{
		return new BasicGL3Scene( );
	}
	
	protected GLCanvas createCanvas( )
	{
		GLProfile glp = GLProfile.get( GLProfile.GL3 );
		GLCapabilities userCapsRequest = new GLCapabilities( glp );
		return new GLCanvas( userCapsRequest );
	}
	
	protected void initMouseInput( )
	{
		navigator = new BasicNavigator( scene );
		
		glCanvas.addMouseListener( navigator );
		glCanvas.addMouseMotionListener( navigator );
		glCanvas.addMouseWheelListener( navigator );
		
		orbiter = new BasicOrbiter( scene );
		
		glCanvas.addMouseListener( orbiter );
		glCanvas.addMouseMotionListener( orbiter );
		glCanvas.addMouseWheelListener( orbiter );
	}
	
}
