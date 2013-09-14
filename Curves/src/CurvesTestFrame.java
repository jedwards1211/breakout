import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import com.sun.org.apache.bcel.internal.generic.DSTORE;

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
			
			@Override
			public void mouseDragged( MouseEvent e )
			{
				float lxf = CurvesTestScene.reparam( lastEvent.getX( ) , 0 , getWidth( ) , -1 , 1 );
				float lyf = CurvesTestScene.reparam( lastEvent.getY( ) , 0 , getHeight( ) , 1 , -1 );
				float nxf = CurvesTestScene.reparam( e.getX( ) , 0 , getWidth( ) , -1 , 1 );
				float nyf = CurvesTestScene.reparam( e.getY( ) , 0 , getHeight( ) , 1 , -1 );
				
				float dx = nxf - lxf;
				float dy = nyf - lyf;
				lastEvent = e;
				
				if( scene.highlightedPoint >= 0 )
				{
					scene.visualizer.controlPoints[ scene.highlightedPoint * 2 ] += dx;
					scene.visualizer.controlPoints[ scene.highlightedPoint * 2 + 1 ] += dy;
					
					scene.visualizer.recalculate( );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent e )
			{
				int nearest = findNearestPoint( e.getPoint( ) , 100.0 );
				scene.highlightedPoint = nearest;
			}
			
			private int findNearestPoint( Point p , double maxDistSq )
			{
				float mx = p.x;
				float my = p.y;
				
				int closest = -1;
				double closestDistSq = maxDistSq;
				
				for( int i = 0 ; i < scene.visualizer.numPoints ; i++ )
				{
					double x = scene.visualizer.controlPoints[ i * 2 ];
					double y = scene.visualizer.controlPoints[ i * 2 + 1 ];
					
					x = CurvesTestScene.reparam( ( float ) x , -1 , 1 , 0 , getWidth( ) );
					y = CurvesTestScene.reparam( ( float ) y , -1 , 1 , getHeight( ) , 0 );
					
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
