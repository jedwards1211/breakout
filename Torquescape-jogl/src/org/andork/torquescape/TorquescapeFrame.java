package org.andork.torquescape;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeFrame extends JFrame
{
	private GLCanvas			glCanvas;
	private TorquescapeScene	scene;
	
	public static void main( String[ ] args )
	{
		GLProfile.initSingleton( );
		
		TorquescapeFrame frame = new TorquescapeFrame( );
		frame.setSize( 800 , 600 );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public TorquescapeFrame( )
	{
		init( );
	}
	
	private void init( )
	{
		GLProfile glp = GLProfile.get( GLProfile.GL3 );
		GLCapabilities userCapsRequest = new GLCapabilities( glp );
		glCanvas = new GLCanvas( userCapsRequest );
		
		scene = new TorquescapeScene( );
		
		glCanvas.addGLEventListener( scene );
		
		MouseAdapter mouseAdapter = new MouseAdapter( )
		{
			MouseEvent	lastEvent	= null;
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				lastEvent = e;
			}
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				int dx = e.getX( ) - lastEvent.getX( );
				int dy = e.getY( ) - lastEvent.getY( );
				lastEvent = e;
				
				scene.mPan -= dx * Math.PI / glCanvas.getWidth( ); // = 180.0f / 320
				scene.mTilt -= dy * Math.PI / glCanvas.getHeight( ); // = 180.0f / 320
			}
		};
		
		glCanvas.addMouseListener( mouseAdapter );
		glCanvas.addMouseMotionListener( mouseAdapter );
		
		AnimatorBase animator = new FPSAnimator( glCanvas , 60 );
		
		getContentPane( ).add( glCanvas , BorderLayout.CENTER );
		animator.start( );
	}
}
