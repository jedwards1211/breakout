package org.andork.torquescape.app;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.andork.j3d.math.OrientationUtils;
import org.andork.j3d.newcamera.CameraPosition;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class TorquescapeEditorFrame extends JFrame
{
	private GLCanvas		glcanvas;
	private AnimatorBase	animator;
	
	public TorquescapeEditorFrame( )
	{
		init( );
	}
	
	private void init( )
	{
		GLProfile glprofile = GLProfile.getDefault( );
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		glcanvas = new GLCanvas( glcapabilities );
		
		final TorquescapeRenderer renderer = new TorquescapeRenderer( );
		
		glcanvas.addGLEventListener( new GLEventListener( )
		{
			
			@Override
			public void reshape( GLAutoDrawable glautodrawable , int x , int y , int width , int height )
			{
				renderer.setup( glautodrawable.getGL( ).getGL2( ) , width , height );
			}
			
			@Override
			public void init( GLAutoDrawable glautodrawable )
			{
			}
			
			@Override
			public void dispose( GLAutoDrawable glautodrawable )
			{
			}
			
			@Override
			public void display( GLAutoDrawable glautodrawable )
			{
				renderer.update( );
				renderer.render( glautodrawable.getGL( ).getGL2( ) , glautodrawable.getWidth( ) , glautodrawable.getHeight( ) );
			}
		} );
		
		Matrix4f mat = new Matrix4f( );
		final CameraPosition camPosition = new CameraPosition( );
		camPosition.setLocation( 0 , 0 , 5 );
		
		Quat4f q = new Quat4f( );
		OrientationUtils.taitBryanToQuat( 0 , ( float ) Math.PI / 10 , ( float ) Math.PI / 15 , q );
		camPosition.orientation.mul( q , camPosition.orientation );
		OrientationUtils.taitBryanToQuat( 0 , ( float ) -Math.PI / 10 , ( float ) -Math.PI / 15 , q );
		camPosition.orientation.mul( q , camPosition.orientation );
		
		camPosition.toMatrix( mat );
		mat.invert( );
		renderer.setModelMatrix( mat );
		
		MouseAdapter mouseAdapter = new MouseAdapter( )
		{
			MouseEvent	lastEvent	= null;
			Matrix4f	mat			= new Matrix4f( );
			
			public void mousePressed( MouseEvent e )
			{
				lastEvent = e;
			}
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				int dx = e.getX( ) - lastEvent.getX( );
				int dy = e.getY( ) - lastEvent.getY( );
				
				float pan = dx * ( float ) Math.PI / 180;
				float tilt = dy * ( float ) Math.PI / 180;
				Quat4f q = new Quat4f( );
				OrientationUtils.taitBryanToQuat( 0 , pan , tilt , q );
				
				camPosition.orientation.mul( q , camPosition.orientation );
				
				camPosition.toMatrix( mat );
				mat.invert( );
				renderer.setModelMatrix( mat );
				
				lastEvent = e;
			}
			
			@Override
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				int dz = e.getWheelRotation( );
				
				camPosition.move( 0 , 0 , dz / 10f );
				camPosition.toMatrix( mat );
				mat.invert( );
				renderer.setModelMatrix( mat );
			}
		};
		
		glcanvas.addMouseListener( mouseAdapter );
		glcanvas.addMouseMotionListener( mouseAdapter );
		glcanvas.addMouseWheelListener( mouseAdapter );
		
		getContentPane( ).add( glcanvas , BorderLayout.CENTER );
	}
	
	public void startAnimation( )
	{
		animator = new FPSAnimator( glcanvas , 60 );
		animator.start( );
	}
}
