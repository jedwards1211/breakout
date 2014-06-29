package org.andork.breakout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLRunnable;
import javax.media.opengl.awt.GLCanvas;

import org.andork.breakout.model.Survey3dModel;
import org.andork.jogl.neu.JoglScene;

public class WindowSelectionMouseHandler extends MouseAdapter
{
	public static interface Context
	{
		public Survey3dModel getSurvey3dModel( );
		
		public void endSelection( );
		
		public GLCanvas getCanvas( );
		
		public JoglScene getScene( );
	}
	
	public WindowSelectionMouseHandler( Context context )
	{
		this.context = context;
	}
	
	private final Context			context;
	private final List<float[ ]>	points				= new ArrayList<float[ ]>( );
	SelectionPolygon				selectionPolygon	= new SelectionPolygon( );
	
	public void start( MouseEvent e )
	{
		points.add( new float[ ] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		points.add( new float[ ] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		context.getCanvas( ).invoke( true , new GLRunnable( )
		{
			@Override
			public boolean run( GLAutoDrawable drawable )
			{
				selectionPolygon.setPoints( points );
				context.getScene( ).add( selectionPolygon );
				return false;
			}
		} );
	}
	
	public void end( )
	{
		points.clear( );
		context.getCanvas( ).invoke( true , new GLRunnable( )
		{
			@Override
			public boolean run( GLAutoDrawable drawable )
			{
				context.getScene( ).remove( selectionPolygon );
				return true;
			}
		} );
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
	}
	
	@Override
	public void mouseReleased( MouseEvent e ) {
		if( e.getButton( ) == MouseEvent.BUTTON3 ) {
//			RBranch root = context.getSurvey3dModel( ).getTree( ).getRoot( );
//			RTraversal.traverse( root ,
//					{ RNode node -> return node.mbr()[0] < 0 },
//					{ System.out.println(it); return true });
			end( );
			return;
		}
		if( e.getButton( ) != MouseEvent.BUTTON1 ) {
			return;
		}
		points.add( new float[] { e.getX( ) , context.getCanvas( ).getHeight( ) - e.getY( ) } );
		context.getCanvas().invoke(true, new GLRunnable( )
		{
			@Override
			public boolean run( GLAutoDrawable drawable )
			{
				selectionPolygon.setPoints( points );
				return true;
			}
		});
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
			context.getCanvas( ).invoke( true , new GLRunnable( )
			{
				
				@Override
				public boolean run( GLAutoDrawable drawable )
				{
					selectionPolygon.setPoints( points );
					return true;
				}
			} );
		}
	}
}
