package org.andork.curves;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import org.andork.jogl.util.OrthoFrame;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;

public class CurvesTestFrame extends JFrame
{
	private GLCanvas		glCanvas;
	private CurvesTestScene	scene;
	
	public static void main( String[ ] args )
	{
		GLProfile.initSingleton( );
		
		CurvesTestFrame frame = new CurvesTestFrame( );
		frame.setSize( 800 , 600 );
		frame.setLocationRelativeTo( null );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}
	
	public CurvesTestFrame( )
	{
		init( );
	}
	
	private void init( )
	{
		GLProfile glp = GLProfile.get( GLProfile.GL3 );
		GLCapabilities userCapsRequest = new GLCapabilities( glp );
		glCanvas = new GLCanvas( userCapsRequest )
		{
		};
		
		scene = new CurvesTestScene( );
		
		glCanvas.addGLEventListener( scene );
		
		MouseAdapter mouseAdapter = new MouseAdapter( )
		{
			MouseEvent	lastEvent	= null;
			
			float[ ]	tempMatrix	= new float[ 16 ];
			float[ ]	tiltAxis	= new float[ 3 ];
			
			MouseEvent	pressEvent	= null;
			
			@Override
			public void mousePressed( MouseEvent e )
			{
				pressEvent = e;
				lastEvent = e;
			}
			
			private int getViewWidth( )
			{
				return getContentPane( ).getWidth( );
			}
			
			private int getViewHeight( )
			{
				return getContentPane( ).getHeight( );
			}
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				float lxf = OrthoFrame.reparam( lastEvent.getX( ) , 0 , getViewWidth( ) , scene.viewFrame[ 0 ] , scene.viewFrame[ 1 ] );
				float lyf = OrthoFrame.reparam( lastEvent.getY( ) , 0 , getViewHeight( ) , scene.viewFrame[ 3 ] , scene.viewFrame[ 2 ] );
				float nxf = OrthoFrame.reparam( e.getX( ) , 0 , getViewWidth( ) , scene.viewFrame[ 0 ] , scene.viewFrame[ 1 ] );
				float nyf = OrthoFrame.reparam( e.getY( ) , 0 , getViewHeight( ) , scene.viewFrame[ 3 ] , scene.viewFrame[ 2 ] );
				
				float dx = nxf - lxf;
				float dy = nyf - lyf;
				lastEvent = e;
				
				if( pressEvent.getButton( ) == MouseEvent.BUTTON1 && scene.highlightedPoint >= 0 )
				{
					scene.visualizer.controlPoints[ scene.highlightedPoint * 2 ] += dx;
					scene.visualizer.controlPoints[ scene.highlightedPoint * 2 + 1 ] += dy;
					
					scene.visualizer.recalculate( );
				}
				
				if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
				{
					scene.viewFrame[ 0 ] -= dx;
					scene.viewFrame[ 1 ] -= dx;
					scene.viewFrame[ 2 ] -= dy;
					scene.viewFrame[ 3 ] -= dy;
					
					scene.recomputeOrtho( );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent e )
			{
				int nearest = findNearestPoint( e.getPoint( ) , 10.0 );
				scene.highlightedPoint = nearest;
			}
			
			private int findNearestPoint( Point p , double maxDist )
			{
				float mx = p.x;
				float my = p.y;
				
				int closest = -1;
				double closestDistSq = maxDist * maxDist;
				
				for( int i = 0 ; i < scene.visualizer.numPoints ; i++ )
				{
					double x = scene.visualizer.controlPoints[ i * 2 ];
					double y = scene.visualizer.controlPoints[ i * 2 + 1 ];
					
					x = CurvesTestScene.reparam( ( float ) x , scene.viewFrame[ 0 ] , scene.viewFrame[ 1 ] , 0 , getViewWidth( ) );
					y = CurvesTestScene.reparam( ( float ) y , scene.viewFrame[ 2 ] , scene.viewFrame[ 3 ] , getViewHeight( ) , 0 );
					
					double dx = mx - x;
					double dy = my - y;
					
					double distSq = dx * dx + dy * dy;
					if( distSq < closestDistSq )
					{
						closest = i;
						closestDistSq = distSq;
					}
				}
				
				return closest;
			}
			
			@Override
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				float mx = OrthoFrame.reparam( e.getX( ) , 0 , getViewWidth( ) , scene.viewFrame[ 0 ] , scene.viewFrame[ 1 ] );
				float my = OrthoFrame.reparam( e.getY( ) , 0 , getViewHeight( ) , scene.viewFrame[ 3 ] , scene.viewFrame[ 2 ] );
				
				OrthoFrame.zoom( scene.viewFrame , 0 , mx , my , ( float ) Math.pow( 1.1 , e.getWheelRotation( ) ) );
				
				scene.recomputeOrtho( );
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
