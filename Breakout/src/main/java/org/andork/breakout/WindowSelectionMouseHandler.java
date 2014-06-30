package org.andork.breakout;

import static org.andork.spatial.Rectmath.ppunion;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.awt.GLCanvas;

import org.andork.breakout.model.Survey3dModel;
import org.andork.breakout.model.Survey3dModel.Shot;
import org.andork.func.StreamUtils;
import org.andork.jogl.neu.JoglScene;
import org.andork.math3d.InFrustumTester3f;
import org.andork.math3d.PickXform;
import org.andork.math3d.Vecmath;
import org.andork.spatial.EdgeTrees;
import org.andork.spatial.RBranch;
import org.andork.spatial.RTraversal;
import org.andork.spatial.RfStarTree;
import org.andork.swing.OnEDT;
import org.andork.swing.async.TaskService;
import org.andork.util.Reparam;

public class WindowSelectionMouseHandler extends MouseAdapter
{
	public static interface Context
	{
		public Survey3dModel getSurvey3dModel( );
		
		public void endSelection( );
		
		public GLCanvas getCanvas( );
		
		public JoglScene getScene( );
		
		public TaskService getRebuildTaskService( );
		
		public void selectShots( Set<Shot> newSelected , boolean add , boolean toggle );
	}
	
	public WindowSelectionMouseHandler( Context context )
	{
		this.context = context;
	}
	
	private final Context			context;
	private final List<float[ ]>	points				= new ArrayList<float[ ]>( );
	SelectionPolygon				selectionPolygon	= new SelectionPolygon( );
	
	private InFrustumTester3f		inFrustumTester		= new InFrustumTester3f( );
	
	final float[ ]					pointOnScreen		= new float[ 3 ];
	
	public void start( MouseEvent e )
	{
		points.add( new float[ ] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		points.add( new float[ ] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		context.getCanvas( ).invoke( true , drawable -> {
			selectionPolygon.setPoints( points );
			context.getScene( ).add( selectionPolygon );
			return false;
		} );
	}
	
	protected void selectLoopedShots( boolean add , boolean toggle )
	{
		final Survey3dModel model3d = context.getSurvey3dModel( );
		
		final List<float[ ]> points = new ArrayList<>( this.points );
		
		if( model3d == null )
		{
			return;
		}
		
		context.getRebuildTaskService( ).submit( task -> {
			task.setStatus( "Finding lassoed shots..." );
			task.setIndeterminate( true );
			RBranch<float[ ], Shot> root = model3d.getTree( ).getRoot( );
			RfStarTree<float[ ]> edgeTree = new RfStarTree<>( 2 , 4 , 1 , 2 );
			
			StreamUtils.forEachPairLooped( points.stream( ) , ( p1 , p2 ) -> {
				edgeTree.insert( edgeTree.createLeaf( ppunion( p1 , p2 ) , new float[ ] { p1[ 0 ] , p1[ 1 ] , p2[ 0 ] , p2[ 1 ] } ) );
			} );
			
			float[ ] mbr = edgeTree.getRoot( ).mbr( );
			
			PickXform pickXform = context.getScene( ).pickXform( );
			
			GLCanvas canvas = context.getCanvas( );
			int cw = canvas.getWidth( );
			int ch = canvas.getHeight( );
			
			pickXform.xform( mbr[ 0 ] , ch - mbr[ 1 ] , cw , ch , inFrustumTester.origin , inFrustumTester.rays[ 0 ] );
			pickXform.xform( mbr[ 2 ] , ch - mbr[ 1 ] , cw , ch , inFrustumTester.origin , inFrustumTester.rays[ 1 ] );
			pickXform.xform( mbr[ 2 ] , ch - mbr[ 3 ] , cw , ch , inFrustumTester.origin , inFrustumTester.rays[ 2 ] );
			pickXform.xform( mbr[ 0 ] , ch - mbr[ 3 ] , cw , ch , inFrustumTester.origin , inFrustumTester.rays[ 3 ] );
			inFrustumTester.computeNormals( );
			
			JoglScene scene = context.getScene( );
			
			float[ ] pv = Vecmath.newMat4f( );
			Vecmath.mmul( scene.projXform( ) , scene.viewXform( ) , pv );
			
			Set<Shot> newSelected = new HashSet<>( );
			
			RTraversal.traverse( root ,
					node -> inFrustumTester.intersectsBox( node.mbr( ) ) ,
					leaf -> {
						for( float[ ] point : leaf.object( ).coordIterable( ) )
						{
							Vecmath.mpmul( pv , point , pointOnScreen );
							pointOnScreen[ 0 ] = Reparam.linear( pointOnScreen[ 0 ] , -1 , 1 , 0 , cw );
							pointOnScreen[ 1 ] = Reparam.linear( pointOnScreen[ 1 ] , -1 , 1 , 0 , ch );
							if( !EdgeTrees.isInPolygon( pointOnScreen , edgeTree.getRoot( ) ) )
							{
								return true;
							}
						}
						newSelected.add( leaf.object( ) );
						return true;
					} );
			
			context.selectShots( newSelected , add , toggle );
			
			OnEDT.onEDT( ( ) -> context.getCanvas( ).repaint( ) );
		} );
	}
	
	public void end( )
	{
		points.clear( );
		context.getCanvas( ).invoke( true , drawable -> {
			context.getScene( ).remove( selectionPolygon );
			return false;
		} );
		context.endSelection( );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( e.getButton( ) == MouseEvent.BUTTON3 )
		{
			selectLoopedShots( ( e.getModifiersEx( ) & MouseEvent.CTRL_DOWN_MASK ) != 0 ,
					( e.getModifiersEx( ) & MouseEvent.SHIFT_DOWN_MASK ) != 0 );
			end( );
			return;
		}
		if( e.getButton( ) != MouseEvent.BUTTON1 )
		{
			return;
		}
		points.add( new float[ ] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		context.getCanvas( ).invoke( true , drawable -> {
			selectionPolygon.setPoints( points );
			return false;
		} );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		mouseMoved( e );
	}
	
	@Override
	public void mouseMoved( MouseEvent e )
	{
		if( !points.isEmpty( ) )
		{
			float[ ] last = points.get( points.size( ) - 1 );
			last[ 0 ] = e.getX( );
			last[ 1 ] = context.getCanvas( ).getHeight( ) - e.getY( );
			context.getCanvas( ).invoke( true , drawable -> {
				selectionPolygon.setPoints( points );
				return false;
			} );
		}
	}
}
