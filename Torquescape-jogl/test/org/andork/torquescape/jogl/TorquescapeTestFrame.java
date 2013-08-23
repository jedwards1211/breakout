package org.andork.torquescape.jogl;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.andork.vecmath.FloatArrayVecmath;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeTestFrame extends JFrame
{
	private GLCanvas				glCanvas;
	private TorquescapeTestScene	scene;
	
	public static void main( String[ ] args )
	{
		GLProfile.initSingleton( );
		
		TorquescapeTestFrame frame = new TorquescapeTestFrame( );
		frame.setSize( 800 , 600 );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public TorquescapeTestFrame( )
	{
		init( );
	}
	
	private void init( )
	{
		GLProfile glp = GLProfile.get( GLProfile.GL3 );
		GLCapabilities userCapsRequest = new GLCapabilities( glp );
		glCanvas = new GLCanvas( userCapsRequest );
		
		scene = new TorquescapeTestScene( );
		
		glCanvas.addGLEventListener( scene );
		
		MouseAdapter mouseAdapter = new MouseAdapter( )
		{
			MouseEvent	lastEvent	= null;
			
			float[ ]	tempMatrix	= new float[ 16 ];
			float[ ]	tempVec1	= new float[ 3 ];
			
			MouseEvent	pressEvent	= null;
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				pressEvent = e;
				lastEvent = e;
			}
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				int dx = e.getX( ) - lastEvent.getX( );
				int dy = e.getY( ) - lastEvent.getY( );
				lastEvent = e;
				
				if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
				{
					scene.pan -= dx * Math.PI / glCanvas.getWidth( ); // = 180.0f / 320
					scene.tilt -= dy * Math.PI / glCanvas.getHeight( ); // = 180.0f / 320
					
					float pan = ( float ) ( dx * Math.PI / glCanvas.getWidth( ) );
					float tilt = ( float ) ( dy * Math.PI / glCanvas.getHeight( ) );
					
					FloatArrayVecmath.rotY( tempMatrix , pan );
					
					float x = scene.cameraMatrix[ 3 ];
					float y = scene.cameraMatrix[ 7 ];
					float z = scene.cameraMatrix[ 11 ];
					
					scene.cameraMatrix[ 3 ] = 0;
					scene.cameraMatrix[ 7 ] = 0;
					scene.cameraMatrix[ 11 ] = 0;
					
					FloatArrayVecmath.mmulAffine( tempMatrix , scene.cameraMatrix , scene.cameraMatrix );
					
					tempVec1[ 0 ] = 1;
					tempVec1[ 1 ] = 0;
					tempVec1[ 2 ] = 0;
					
					FloatArrayVecmath.mvmulAffine( scene.cameraMatrix , tempVec1 );
					FloatArrayVecmath.setRotation( tempMatrix , tempVec1[ 0 ] , tempVec1[ 1 ] , tempVec1[ 2 ] , tilt );
					FloatArrayVecmath.mmulAffine( tempMatrix , scene.cameraMatrix , scene.cameraMatrix );
					
					scene.cameraMatrix[ 3 ] = x;
					scene.cameraMatrix[ 7 ] = y;
					scene.cameraMatrix[ 11 ] = z;
				}
				else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
				{
					scene.cameraMatrix[ 3 ] += scene.cameraMatrix[ 2 ] * dy;
					scene.cameraMatrix[ 7 ] += scene.cameraMatrix[ 6 ] * dy;
					scene.cameraMatrix[ 11 ] += scene.cameraMatrix[ 10 ] * dy;
				}
				else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
				{
					scene.cameraMatrix[ 3 ] += scene.cameraMatrix[ 0 ] * -dx + scene.cameraMatrix[ 1 ] * dy;
					scene.cameraMatrix[ 7 ] += scene.cameraMatrix[ 4 ] * -dx + scene.cameraMatrix[ 5 ] * dy;
					scene.cameraMatrix[ 11 ] += scene.cameraMatrix[ 8 ] * -dx + scene.cameraMatrix[ 9 ] * dy;
				}
			}
			
			@Override
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				int distance = e.getWheelRotation( ) * 5;
				
				scene.cameraMatrix[ 3 ] += scene.cameraMatrix[ 2 ] * distance;
				scene.cameraMatrix[ 7 ] += scene.cameraMatrix[ 6 ] * distance;
				scene.cameraMatrix[ 11 ] += scene.cameraMatrix[ 10 ] * distance;
			}
		};
		
		glCanvas.addMouseListener( mouseAdapter );
		glCanvas.addMouseMotionListener( mouseAdapter );
		glCanvas.addMouseWheelListener( mouseAdapter );
		
		AnimatorBase animator = new FPSAnimator( glCanvas , 60 );
		
		getContentPane( ).add( glCanvas , BorderLayout.CENTER );
		animator.start( );
	}
}
